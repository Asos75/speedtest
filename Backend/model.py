"""
Datoteka (model.py) z arhitekturo in učenjem nevronske mreže za razpoznavanje in lokalizacijo mobilnega stolpa.
Dopolnjena tako, da primerja mapi Data/Raw in Data/Processed ter naloži slike z rdečim pravokotnikom (označen stolp).
"""

import os
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt

# Mapi z neobdelanimi in obdelanimi slikami
RAW_DIR = 'Data/Raw'
PROCESSED_DIR = 'Data/Processed'

# Štetje obdelanih slik
raw_count = len(os.listdir(RAW_DIR))
processed_count = len(os.listdir(PROCESSED_DIR))
print(f"Obdelanih slik: {processed_count}/{raw_count}")

tf.keras.mixed_precision.set_global_policy('mixed_float16')

# Nastavitve GPU
gpus = tf.config.list_physical_devices('GPU')
if gpus:
    for gpu in gpus:
        tf.config.experimental.set_memory_growth(gpu, True)

# Minimalni primer generatorja, ki naloži slikovne datoteke iz 'Processed' mape (z že označenim stolpom)
class TowerDataset(tf.keras.utils.Sequence):
    def __init__(self, directory=PROCESSED_DIR, batch_size=8, input_size=(64, 64)):
        self.batch_size = batch_size
        self.input_size = input_size
        # Pridobi samo obdelane slike (predpostavimo .jpg)
        self.image_paths = [os.path.join(directory, f) 
                            for f in os.listdir(directory) 
                            if f.endswith('.jpg')]

    def __len__(self):
        return max(1, len(self.image_paths)//self.batch_size)

    def __getitem__(self, idx):
        batch_paths = self.image_paths[idx*self.batch_size:(idx+1)*self.batch_size]
        images = []
        # Tu privzamemo, da so bounding box oznake del slike (rdeči pravokotnik),
        # zato bo class_label=1, bbox=nepotrebno (fiksno).
        labels = []

        for p in batch_paths:
            img_raw = tf.io.read_file(p)
            img = tf.io.decode_jpeg(img_raw, channels=3)
            img = tf.image.resize(img, self.input_size)
            img = tf.cast(img, tf.float32)/255.0
            images.append(img)
            labels.append([1.0, 0.0, 0.0, 0.0, 0.0])  

        images = tf.stack(images, axis=0)
        labels = tf.constant(labels, dtype=tf.float32)
        return images, labels

def build_model(input_shape=(64, 64, 3)):
    inputs = tf.keras.Input(shape=input_shape)
    x = tf.keras.layers.Conv2D(16, (3,3), padding='same', activation='relu')(inputs)
    x = tf.keras.layers.AveragePooling2D((2,2))(x)
    x = tf.keras.layers.Conv2D(32, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.AveragePooling2D((2,2))(x)
    x = tf.keras.layers.Conv2D(64, (3,3), padding='same', activation='relu')(x)
    x = tf.keras.layers.AveragePooling2D((2,2))(x)
    x = tf.keras.layers.Flatten()(x)
    x = tf.keras.layers.Dense(128, activation='relu')(x)
    x = tf.keras.layers.Dropout(0.5)(x)
    class_output = tf.keras.layers.Dense(1, activation='sigmoid', name='class_output')(x)
    bbox_output = tf.keras.layers.Dense(4, activation='linear', name='bbox_output')(x)
    return tf.keras.Model(inputs=inputs, outputs=[class_output, bbox_output])

def custom_loss(y_true, y_pred):
    class_true = y_true[..., 0:1]
    bbox_true = y_true[..., 1:5]
    class_pred, bbox_pred = y_pred
    class_loss = tf.keras.losses.binary_crossentropy(class_true, class_pred)
    bbox_loss = tf.reduce_mean(tf.keras.losses.mean_squared_error(bbox_true, bbox_pred))
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

    model.save('tower_detection_model.h5')