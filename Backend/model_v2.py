import os
import json
import cv2
import numpy as np
import tensorflow as tf

# Directories for raw images
RAW_DIR = 'Data/Raw'
BBOX_FILE = "Data/bounding_boxes_list.json"

# GPU Settings
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except:
        pass

# Use mixed precision
tf.keras.mixed_precision.set_global_policy('mixed_float16')

def build_model(input_shape=(64, 64, 3)):
    """
    Simple CNN model with a single output for classifying whether a tower is present or not.
    """
    inputs = tf.keras.Input(shape=input_shape)
    x = tf.keras.layers.Conv2D(16, (3, 3), padding='same', activation='relu')(inputs)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.AveragePooling2D((2, 2))(x)

    x = tf.keras.layers.Conv2D(32, (3, 3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.AveragePooling2D((2, 2))(x)

    x = tf.keras.layers.Conv2D(64, (3, 3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.GlobalAveragePooling2D()(x)

    # Classification output (tower or no tower)
    class_out = tf.keras.layers.Dense(1, activation='sigmoid', name='class_output')(x)

    model = tf.keras.Model(inputs=inputs, outputs=[class_out])
    return model

class TowerDataset(tf.keras.utils.Sequence):
    def __init__(self, batch_size=8, input_size=(64, 64)):
        self.files = []
        self.bbox_data = {}

        # Load tower images and bounding boxes
        tower_images = [f"tower/{f}" for f in os.listdir(os.path.join(RAW_DIR, 'tower'))]
        for f in tower_images:
            self.files.append(f)
            # Load bounding boxes for tower images from the JSON file
            bbox = self.bbox_data.get(f, [0, 0, 0, 0])
            self.bbox_data[f] = bbox

        # Load no-tower images (no bounding box needed)
        no_tower_images = [f"no_tower/{f}" for f in os.listdir(os.path.join(RAW_DIR, 'no_tower'))]
        for f in no_tower_images:
            self.files.append(f)

        self.batch_size = batch_size
        self.input_size = input_size

    def __len__(self):
        return int(np.ceil(len(self.files) / self.batch_size))

    def __getitem__(self, idx):
        batch_files = self.files[idx * self.batch_size : (idx + 1) * self.batch_size]
        images = []
        labels = []

        for f in batch_files:
            img_path = os.path.join(RAW_DIR, f)  # Correct path construction
            img = cv2.imread(img_path)
            if img is None:
                continue
            img = cv2.resize(img, self.input_size)
            img = img.astype('float32') / 255.0

            if f in self.bbox_data:  # Tower present
                bbox = self.bbox_data[f]
                h, w, _ = cv2.imread(img_path).shape
                x1, y1, x2, y2 = bbox
                norm_bbox = [x1 / w, y1 / h, x2 / w, y2 / h]
                class_label = 1.0
            else:  # No tower
                norm_bbox = [0.0, 0.0, 0.0, 0.0]
                class_label = 0.0

            images.append(img)
            labels.append([class_label])

        if not images:  # Empty batch handling
            images = np.zeros([self.batch_size, *self.input_size, 3], dtype=np.float32)
            labels = np.zeros([self.batch_size, 1], dtype=np.float32)

        X = np.array(images, dtype=np.float32)
        Y = np.array(labels, dtype=np.float32)
        return X, Y


def custom_loss(y_true, y_pred):
    class_true = y_true  # shape (batch, 1)
    class_pred = y_pred  # shape (batch, 1)

    class_loss = tf.keras.losses.binary_crossentropy(class_true, class_pred)
    return class_loss

if __name__ == "__main__":
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss=custom_loss,
        metrics=['accuracy']
    )
    model.summary()

    # Initialize datasets
    train_dataset = TowerDataset(batch_size=4)
    val_dataset   = TowerDataset(batch_size=4)

    # Callbacks for early stopping and best model saving
    callbacks = [
        tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5),
        tf.keras.callbacks.ModelCheckpoint('best_tower_model.h5', save_best_only=True, monitor='val_loss')
    ]

    # Train the model
    history = model.fit(
        train_dataset,
        epochs=10,
        validation_data=val_dataset,
        callbacks=callbacks
    )
