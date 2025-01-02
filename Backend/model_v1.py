import os
import json
import cv2
import numpy as np
import tensorflow as tf

# Mapi z neobdelanimi in obdelanimi slikami
RAW_DIR = 'Data/Raw/tower'
PROCESSED_DIR = 'Data/Processed'
BBOX_FILE = "Data/bounding_boxes_list.json"

# Nastavitve GPU
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except:
        pass

tf.keras.mixed_precision.set_global_policy('mixed_float16')

def build_model(input_shape=(64, 64, 3)):
    """
    Example CNN model with two outputs:
    1) Tower present (classification)
    2) Bounding box [x1, y1, x2, y2]
    """
    inputs = tf.keras.Input(shape=input_shape)
    x = tf.keras.layers.Conv2D(16, (3,3), padding='same', activation='relu')(inputs)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.AveragePooling2D((2,2))(x)

    x = tf.keras.layers.Conv2D(32, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.AveragePooling2D((2,2))(x)

    x = tf.keras.layers.Conv2D(64, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.GlobalAveragePooling2D()(x)

    # Classification output
    class_out = tf.keras.layers.Dense(1, activation='sigmoid', name='class_output')(x)
    # Bounding box regression output
    bbox_out = tf.keras.layers.Dense(4, activation='linear', name='bbox_output')(x)

    model = tf.keras.Model(inputs=inputs, outputs=[class_out, bbox_out])
    return model

class TowerDataset(tf.keras.utils.Sequence):
    def __init__(self, batch_size=8, input_size=(64,64)):
        if not os.path.exists(BBOX_FILE):
            self.bbox_data = {}
        else:
            with open(BBOX_FILE, "r") as f:
                self.bbox_data = json.load(f)
        self.files = list(self.bbox_data.keys())
        self.batch_size = batch_size
        self.input_size = input_size

    def __len__(self):
        return int(np.ceil(len(self.files) / self.batch_size))

    def __getitem__(self, idx):
        batch_files = self.files[idx * self.batch_size : (idx+1) * self.batch_size]
        images = []
        labels = []
        for f in batch_files:
            img_path = os.path.join(PROCESSED_DIR, f)
            img = cv2.imread(img_path)
            if img is None:
                continue
            img = cv2.resize(img, self.input_size)
            img = img.astype('float32') / 255.0

            # [x1, y1, x2, y2] in bounding_boxes_list.json
            bbox = self.bbox_data[f]
            h, w, _ = cv2.imread(img_path).shape
            x1, y1, x2, y2 = bbox
            norm_bbox = [x1/w, y1/h, x2/w, y2/h]

            # Labels => [class, x1, y1, x2, y2] in normalized form
            images.append(img)
            labels.append([1.0] + norm_bbox)

        if not images:  # empty batch
            images = np.zeros([self.batch_size, *self.input_size, 3], dtype=np.float32)
            labels = np.zeros([self.batch_size, 5], dtype=np.float32)

        X = np.array(images, dtype=np.float32)
        Y = np.array(labels, dtype=np.float32)
        return X, [Y[..., 0:1], Y[..., 1:5]]

def custom_loss(y_true, y_pred):
    class_true = y_true[0]  # shape (batch, 1)
    bbox_true  = y_true[1]  # shape (batch, 4)
    class_pred = y_pred[0]  # shape (batch, 1)
    bbox_pred  = y_pred[1]  # shape (batch, 4)

    class_loss = tf.keras.losses.binary_crossentropy(class_true, class_pred)
    bbox_loss  = tf.reduce_mean(tf.keras.losses.mean_squared_error(bbox_true, bbox_pred))
    return class_loss + bbox_loss

if __name__ == "__main__":
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss=custom_loss,
        metrics=['accuracy']
    )
    model.summary()

    train_dataset = TowerDataset(batch_size=4)
    val_dataset   = TowerDataset(batch_size=4)

    callbacks = [
        tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5),
        tf.keras.callbacks.ModelCheckpoint('best_tower_model.h5', save_best_only=True, monitor='val_loss')
    ]

    history = model.fit(
        train_dataset,
        epochs=10,
        validation_data=val_dataset,
        callbacks=callbacks
    )