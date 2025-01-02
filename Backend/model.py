import os
import logging
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
logging.getLogger('tensorflow').setLevel(logging.ERROR)  # Suppress TF logging
import json
import cv2
import numpy as np
import tensorflow as tf
tf.get_logger().setLevel('ERROR')  # Suppress TF logger
tf.autograph.set_verbosity(0)  # Suppress autograph warnings
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import (
    BatchNormalization, Dropout, Dense,
    GlobalAveragePooling2D, Input
)

RAW_DIR = 'Data/Raw'
PROCESSED_DIR = 'Data/Processed'
BBOX_FILE = "Data/bounding_boxes_list.json"

def focal_loss(y_true, y_pred, alpha=0.25, gamma=2.0):
    bce = tf.keras.losses.binary_crossentropy(y_true, y_pred)
    pt = tf.where(tf.equal(y_true, 1), y_pred, 1 - y_pred)
    fl = alpha * tf.pow((1 - pt), gamma) * bce
    return tf.reduce_mean(fl)

def custom_loss(y_true, y_pred):
    """
    Scenario A custom loss for single merged output.
    y_pred shape: (batch,5)
      y_pred[...,0:1] => class prediction
      y_pred[...,1:5] => bbox prediction
    
    y_true shape: (batch,5)
      y_true[...,0:1] => class label
      y_true[...,1:5] => bbox label
    """
    # Slice out class vs bbox
    class_pred = y_pred[..., 0:1]
    bbox_pred  = y_pred[..., 1:5]

    class_true = y_true[..., 0:1]
    bbox_true  = y_true[..., 1:5]

    # Example: focal loss for classification
    class_loss = focal_loss(class_true, class_pred)

    # Example: MSE or SmoothL1 for bounding box
    bbox_mse = tf.reduce_mean(
        tf.keras.losses.mean_squared_error(bbox_true, bbox_pred)
    )
    
    return class_loss + bbox_mse

def build_model(input_shape=(224, 224, 3)):
    """
    Scenario A: Single merged output shape=(batch,5).
    - The first element is class_pred (tower classification),
    - The next four are bbox_pred (x1,y1,x2,y2).
    """
    base_model = MobileNetV2(include_top=False, weights='imagenet', input_shape=input_shape)
    base_model.trainable = False

    inputs = Input(shape=input_shape)

    x = tf.keras.layers.RandomFlip("horizontal")(inputs)
    x = tf.keras.layers.RandomRotation(0.1)(x)
    x = tf.keras.layers.RandomZoom(0.1)(x)

    x = base_model(x, training=False)
    x = GlobalAveragePooling2D()(x)
    x = Dense(256, activation='relu')(x)
    x = Dropout(0.5)(x)

    # Produce a single output: [class_pred, bbox_pred]
    class_out = Dense(1, activation='sigmoid')(x)  # shape=(batch,1)
    bbox_out  = Dense(4, activation='linear')(x)   # shape=(batch,4)

    # Concatenate => shape=(batch,5)
    merged_out = tf.keras.layers.Concatenate(axis=-1)([class_out, bbox_out])

    return tf.keras.Model(inputs, outputs=merged_out)

class TowerDataset(tf.keras.utils.Sequence):
    def __init__(self, batch_size=4, input_size=(224,224)):
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
    
    def resize_with_bbox(self, img, bbox, target_size):
        h, w = img.shape[:2]
        new_w, new_h = target_size
        w_scale = new_w / w
        h_scale = new_h / h

        resized_img = cv2.resize(img, (new_w, new_h))
        x1, y1, x2, y2 = bbox
        new_bbox = [
            int(x1 * w_scale),
            int(y1 * h_scale),
            int(x2 * w_scale),
            int(y2 * h_scale)
        ]
        return resized_img, new_bbox

    def __getitem__(self, idx):
        batch_files = self.files[idx * self.batch_size : (idx+1) * self.batch_size]
        images = []
        labels = []  # shape => (class, x1, y1, x2, y2)
        
        for f in batch_files:
            img_path = os.path.join(PROCESSED_DIR, f)
            img = cv2.imread(img_path)
            if img is None:
                continue
            bbox = self.bbox_data[f]  # [x1,y1,x2,y2]
            img, resized_bbox = self.resize_with_bbox(img, bbox, self.input_size)
            img = img.astype('float32') / 255.0

            x1, y1, x2, y2 = resized_bbox
            w, h = self.input_size
            # Normalize bbox
            norm_bbox = [x1 / w, y1 / h, x2 / w, y2 / h]
            
            images.append(img)
            # class=1 for tower, then bbox coords
            labels.append([1.0] + norm_bbox)

        if not images:
            images = np.zeros([self.batch_size, *self.input_size, 3], dtype=np.float32)
            labels = np.zeros([self.batch_size, 5], dtype=np.float32)

        X = np.array(images, dtype=np.float32)
        Y = np.array(labels, dtype=np.float32)
        # Return (X, single Y-tensor); custom_loss expects Y shape (batch,5)
        return X, Y

if __name__ == "__main__":
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=1e-4),
        loss=custom_loss,
        metrics=['accuracy'],
        run_eagerly=True  # Simplest fix, slower
    )

    train_dataset = TowerDataset(batch_size=4)
    val_dataset = TowerDataset(batch_size=4)

    callbacks = [
        tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True),
        tf.keras.callbacks.ModelCheckpoint('best_tower_model.h5', monitor='val_loss', save_best_only=True)
    ]

    history = model.fit(
        train_dataset,
        epochs=20,
        validation_data=val_dataset,
        callbacks=callbacks
    )