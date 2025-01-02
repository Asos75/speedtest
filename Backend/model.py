import os
import json
import cv2
import numpy as np
import tensorflow as tf

RAW_DIR = 'Data/Raw'
PROCESSED_DIR = 'Data/Processed'
BBOX_FILE = "Data/bounding_boxes_list.json"

# GPU Setup
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except RuntimeError as e:
        print(e)

def build_model(input_shape=(64, 64, 3)):
    inputs = tf.keras.Input(shape=input_shape)
    
    # First conv block
    x = tf.keras.layers.Conv2D(16, (3,3), padding='same', activation='relu')(inputs)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.MaxPooling2D((2,2))(x)
    
    # Second conv block
    x = tf.keras.layers.Conv2D(32, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.MaxPooling2D((2,2))(x)
    
    # Third conv block
    x = tf.keras.layers.Conv2D(64, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.GlobalAveragePooling2D()(x)
    
    # Outputs
    classification = tf.keras.layers.Dense(1, activation='sigmoid', name='class_output')(x)
    bbox = tf.keras.layers.Dense(4, activation='linear', name='bbox_output')(x)
    
    model = tf.keras.Model(inputs=inputs, outputs=[classification, bbox])
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
        batch_files = self.files[idx * self.batch_size:(idx + 1) * self.batch_size]
        images = []
        class_labels = []
        bbox_labels = []

        for f in batch_files:
            img_path = os.path.join(PROCESSED_DIR, f)
            img = cv2.imread(img_path)
            if img is None:
                continue
                
            img = cv2.resize(img, self.input_size)
            img = img.astype('float32') / 255.0
            
            bbox = self.bbox_data[f]
            h, w, _ = cv2.imread(img_path).shape
            x1, y1, x2, y2 = bbox
            norm_bbox = [x1/w, y1/h, x2/w, y2/h]
            
            images.append(img)
            class_labels.append([1.0])  # Classification label
            bbox_labels.append(norm_bbox)  # Bounding box coordinates

        if not images:
            images = np.zeros([self.batch_size, *self.input_size, 3], dtype=np.float32)
            class_labels = np.zeros([self.batch_size, 1], dtype=np.float32)
            bbox_labels = np.zeros([self.batch_size, 4], dtype=np.float32)
        
        return np.array(images), [np.array(class_labels), np.array(bbox_labels)]

if __name__ == "__main__":
    # Build and compile model
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss={
            'class_output': 'binary_crossentropy',
            'bbox_output': 'mse'
        },
        loss_weights={
            'class_output': 1.0,
            'bbox_output': 1.0
        },
        metrics={
            'class_output': ['accuracy'],
            'bbox_output': ['mae']
        }
    )
    
    # Create datasets
    train_dataset = TowerDataset(batch_size=4)
    val_dataset = TowerDataset(batch_size=4)
    
    # Setup callbacks
    callbacks = [
        tf.keras.callbacks.EarlyStopping(
            monitor='val_loss',
            patience=5,
            restore_best_weights=True
        ),
        tf.keras.callbacks.ModelCheckpoint(
            'best_tower_model.h5',
            monitor='val_loss',
            save_best_only=True,
            #save_weights_only=True
        )
    ]
    
    # Train model
    history = model.fit(
        train_dataset,
        validation_data=val_dataset,
        epochs=10,
        callbacks=callbacks
    )