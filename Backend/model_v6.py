import os
import json
import cv2
import numpy as np
import tensorflow as tf
import random

RAW_DIR = 'Data/Raw/tower'
PROCESSED_DIR = 'Data/Processed'
BBOX_FILE = "Data/bounding_boxes_list.json"

BATCH_SIZE = 8
LEARNING_RATE = 0.0005
EPOCHS = 50

def preprocess_image(img):
    # Convert to grayscale
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    # Apply Gaussian blur
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    # Edge detection
    edges = cv2.Canny(blurred, 50, 150)
    # Find contours
    contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    # Draw contours
    shape_image = np.zeros_like(gray)
    cv2.drawContours(shape_image, contours, -1, (255,255,255), 2)
    return shape_image

# GPU Setup
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
    except RuntimeError as e:
        print(e)

def build_model(input_shape=(128, 128, 1)):
    data_augmentation = tf.keras.Sequential([
        tf.keras.layers.RandomFlip("horizontal_and_vertical"),
        tf.keras.layers.RandomRotation(0.1),
        tf.keras.layers.RandomZoom(0.1)
    ])

    inputs = tf.keras.Input(shape=input_shape)
    
    x = data_augmentation(inputs)
    # First conv block
    x = tf.keras.layers.Conv2D(16, (3,3), padding='same', activation='relu')(x)
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
    
    class_out = tf.keras.layers.Dense(1, activation='sigmoid', name='class_output')(x)
    bbox_out = tf.keras.layers.Dense(4, activation='linear', name='bbox_output')(x)
    
    model = tf.keras.Model(inputs=inputs, outputs=[class_out, bbox_out])
    return model

class TowerDataset(tf.keras.utils.Sequence):
    def __init__(self, batch_size=8, input_size=(128,128)):
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

            # Process image for shape detection
            img = cv2.resize(img, self.input_size)
            processed_img = preprocess_image(img)
            processed_img = processed_img.astype('float32') / 255.0
            processed_img = np.expand_dims(processed_img, axis=-1)  # Add channel dimension
            
            bbox = self.bbox_data[f]
            h, w, _ = cv2.imread(img_path).shape
            x1, y1, x2, y2 = bbox
            
            norm_bbox = [x1/w, y1/h, x2/w, y2/h]
            
            images.append(processed_img)
            class_labels.append([1.0])
            bbox_labels.append(norm_bbox) 

        if not images:
            images = np.zeros([self.batch_size, *self.input_size, 1], dtype=np.float32)
            class_labels = np.zeros([self.batch_size, 1], dtype=np.float32)
            bbox_labels = np.zeros([self.batch_size, 4], dtype=np.float32)
        
        return np.array(images), [np.array(class_labels), np.array(bbox_labels)]

if __name__ == "__main__":
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=LEARNING_RATE),
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
    
    train_dataset = TowerDataset(batch_size=BATCH_SIZE)
    val_dataset = TowerDataset(batch_size=BATCH_SIZE)
    
    callbacks = [
        tf.keras.callbacks.EarlyStopping(
            monitor='val_loss',
            patience=5,
            restore_best_weights=True
        ),
        tf.keras.callbacks.ModelCheckpoint(
            'best_tower_model_v2.h5',
            monitor='val_loss',
            save_best_only=True,
            verbose = 1
        )
    ]
    
    history = model.fit(
        train_dataset,
        validation_data=val_dataset,
        epochs=EPOCHS,
        callbacks=callbacks
    )