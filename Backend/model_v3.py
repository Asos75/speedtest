import os
import numpy as np
import tensorflow as tf
from tensorflow.keras.applications import ResNet50
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
from tensorflow.keras.models import Model
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from sklearn.model_selection import train_test_split
from tensorflow.keras.callbacks import ModelCheckpoint

# Folder structure
DATA_DIR = 'Data/Raw'  # Folder containing both tower and no_tower images

# File paths
tower_images = [os.path.join(DATA_DIR, 'tower', f) for f in os.listdir(os.path.join(DATA_DIR, 'tower'))]
no_tower_images = [os.path.join(DATA_DIR, 'no_tower', f) for f in os.listdir(os.path.join(DATA_DIR, 'no_tower'))]

# Combine the file paths for both classes
all_images = tower_images + no_tower_images
all_labels = [1] * len(tower_images) + [0] * len(no_tower_images)  # 1 for tower, 0 for no_tower

# Split data into 90% training and 10% validation using train_test_split
train_images, val_images, train_labels, val_labels = train_test_split(
    all_images, all_labels, test_size=0.1, random_state=42
)

# Function to load images and labels
def load_images_and_labels(image_paths, labels, target_size=(224, 224)):
    images = []
    for img_path in image_paths:
        img = tf.keras.preprocessing.image.load_img(img_path, target_size=target_size)
        img_array = tf.keras.preprocessing.image.img_to_array(img) / 255.0  # Normalize image
        images.append(img_array)
    return np.array(images), np.array(labels)

# Load train and validation images
X_train, y_train = load_images_and_labels(train_images, train_labels)
X_val, y_val = load_images_and_labels(val_images, val_labels)

# Load the pretrained ResNet50 model without the top classification layer
base_model = ResNet50(weights='imagenet', include_top=False, input_shape=(224, 224, 3))

# Freeze the layers of the base model
base_model.trainable = False

# Add custom layers on top for tower detection
x = base_model.output
x = GlobalAveragePooling2D()(x)  # Global pooling to get a fixed-size output
x = Dense(1024, activation='relu')(x)
output = Dense(1, activation='sigmoid')(x)  # Binary classification: tower or no tower

# Create the final model
model = Model(inputs=base_model.input, outputs=output)

# Compile the model
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.0001),  # Fine-tune with a small learning rate
    loss='binary_crossentropy',  # Binary classification
    metrics=['accuracy']
)

# Callback to save the best model
checkpoint = ModelCheckpoint('best_tower_detection_model.h5', 
                             monitor='val_loss', 
                             save_best_only=True, 
                             mode='min', 
                             verbose=1)

# Train the model using the loaded images
history = model.fit(
    X_train, y_train,
    epochs=10,
    batch_size=32,
    validation_data=(X_val, y_val),
    callbacks=[checkpoint]  # Use the checkpoint callback
)

# Unfreeze the last few layers of the base model
base_model.trainable = True
for layer in base_model.layers[:100]:  # Freeze all layers except the last 100
    layer.trainable = False

# Recompile the model after unfreezing layers
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.00001),  # Lower learning rate for fine-tuning
    loss='binary_crossentropy',
    metrics=['accuracy']
)

# Continue training
history_finetune = model.fit(
    X_train, y_train,
    epochs=10,
    batch_size=32,
    validation_data=(X_val, y_val),
    callbacks=[checkpoint]  # Use the checkpoint callback
)

# Evaluate the model on the validation set
test_loss, test_acc = model.evaluate(X_val, y_val)
print(f"Test Loss: {test_loss}, Test Accuracy: {test_acc}")

# Save the final model after training
model.save('final_tower_detection_model.h5')  # Save the model in HDF5 format

'''
Epoch 1/10
172/172 [==============================] - ETA: 0s - loss: 0.6166 - accuracy: 0.6986   
Epoch 1: val_loss improved from inf to 0.54762, saving model to best_tower_detection_model.h5
172/172 [==============================] - 176s 1s/step - loss: 0.6166 - accuracy: 0.6986 - val_loss: 0.5476 - val_accuracy: 0.6710
Epoch 2/10
172/172 [==============================] - ETA: 0s - loss: 0.4856 - accuracy: 0.8231  
Epoch 2: val_loss improved from 0.54762 to 0.42659, saving model to best_tower_detection_model.h5
172/172 [==============================] - 185s 1s/step - loss: 0.4856 - accuracy: 0.8231 - val_loss: 0.4266 - val_accuracy: 0.8707
Epoch 3/10
172/172 [==============================] - ETA: 0s - loss: 0.4130 - accuracy: 0.8608  
Epoch 3: val_loss improved from 0.42659 to 0.37412, saving model to best_tower_detection_model.h5
172/172 [==============================] - 179s 1s/step - loss: 0.4130 - accuracy: 0.8608 - val_loss: 0.3741 - val_accuracy: 0.8822
Epoch 4/10
172/172 [==============================] - ETA: 0s - loss: 0.3685 - accuracy: 0.8668  
Epoch 4: val_loss improved from 0.37412 to 0.33052, saving model to best_tower_detection_model.h5
172/172 [==============================] - 160s 931ms/step - loss: 0.3685 - accuracy: 0.8668 - val_loss: 0.3305 - val_accuracy: 0.8969
Epoch 5/10
172/172 [==============================] - ETA: 0s - loss: 0.3361 - accuracy: 0.8806  
Epoch 5: val_loss improved from 0.33052 to 0.31175, saving model to best_tower_detection_model.h5
172/172 [==============================] - 152s 883ms/step - loss: 0.3361 - accuracy: 0.8806 - val_loss: 0.3117 - val_accuracy: 0.9018
Epoch 6/10
172/172 [==============================] - ETA: 0s - loss: 0.3091 - accuracy: 0.8895  
Epoch 6: val_loss improved from 0.31175 to 0.28463, saving model to best_tower_detection_model.h5
172/172 [==============================] - 151s 879ms/step - loss: 0.3091 - accuracy: 0.8895 - val_loss: 0.2846 - val_accuracy: 0.9165
Epoch 7/10
172/172 [==============================] - ETA: 0s - loss: 0.2908 - accuracy: 0.8924  
Epoch 7: val_loss improved from 0.28463 to 0.26555, saving model to best_tower_detection_model.h5
172/172 [==============================] - 149s 869ms/step - loss: 0.2908 - accuracy: 0.8924 - val_loss: 0.2656 - val_accuracy: 0.9100
Epoch 8/10
172/172 [==============================] - ETA: 0s - loss: 0.2748 - accuracy: 0.9025  
Epoch 8: val_loss did not improve from 0.26555
172/172 [==============================] - 151s 878ms/step - loss: 0.2748 - accuracy: 0.9025 - val_loss: 0.2957 - val_accuracy: 0.8920
Epoch 9/10
172/172 [==============================] - ETA: 0s - loss: 0.2708 - accuracy: 0.8954  
Epoch 9: val_loss improved from 0.26555 to 0.24686, saving model to best_tower_detection_model.h5
172/172 [==============================] - 151s 878ms/step - loss: 0.2708 - accuracy: 0.8954 - val_loss: 0.2469 - val_accuracy: 0.9165
Epoch 10/10
172/172 [==============================] - ETA: 0s - loss: 0.2586 - accuracy: 0.9030  
Epoch 10: val_loss improved from 0.24686 to 0.24260, saving model to best_tower_detection_model.h5
172/172 [==============================] - 151s 877ms/step - loss: 0.2586 - accuracy: 0.9030 - val_loss: 0.2426 - val_accuracy: 0.9198
Epoch 1/10
172/172 [==============================] - ETA: 0s - loss: 0.7586 - accuracy: 0.8934    
Epoch 1: val_loss did not improve from 0.24260
172/172 [==============================] - 275s 2s/step - loss: 0.7586 - accuracy: 0.8934 - val_loss: 7.8827 - val_accuracy: 0.4910
Epoch 2/10
172/172 [==============================] - ETA: 0s - loss: 0.1166 - accuracy: 0.9576  
Epoch 2: val_loss improved from 0.24260 to 0.15602, saving model to best_tower_detection_model.h5
172/172 [==============================] - 286s 2s/step - loss: 0.1166 - accuracy: 0.9576 - val_loss: 0.1560 - val_accuracy: 0.9378
Epoch 3/10
172/172 [==============================] - ETA: 0s - loss: 0.0598 - accuracy: 0.9802  
Epoch 3: val_loss did not improve from 0.15602
172/172 [==============================] - 263s 2s/step - loss: 0.0598 - accuracy: 0.9802 - val_loss: 0.2351 - val_accuracy: 0.9313
Epoch 4/10
172/172 [==============================] - ETA: 0s - loss: 0.0776 - accuracy: 0.9756  
Epoch 4: val_loss did not improve from 0.15602
172/172 [==============================] - 254s 1s/step - loss: 0.0776 - accuracy: 0.9756 - val_loss: 0.1950 - val_accuracy: 0.9247
Epoch 5/10
172/172 [==============================] - ETA: 0s - loss: 0.0440 - accuracy: 0.9849  
Epoch 5: val_loss did not improve from 0.15602
172/172 [==============================] - 252s 1s/step - loss: 0.0440 - accuracy: 0.9849 - val_loss: 0.2538 - val_accuracy: 0.9280
Epoch 6/10
172/172 [==============================] - ETA: 0s - loss: 0.0430 - accuracy: 0.9840  
Epoch 6: val_loss improved from 0.15602 to 0.13199, saving model to best_tower_detection_model.h5
172/172 [==============================] - 296s 2s/step - loss: 0.0430 - accuracy: 0.9840 - val_loss: 0.1320 - val_accuracy: 0.9574
Epoch 7/10
172/172 [==============================] - ETA: 0s - loss: 0.0362 - accuracy: 0.9865  
Epoch 7: val_loss did not improve from 0.13199
172/172 [==============================] - 290s 2s/step - loss: 0.0362 - accuracy: 0.9865 - val_loss: 0.2875 - val_accuracy: 0.9116
Epoch 8/10
172/172 [==============================] - ETA: 0s - loss: 0.0272 - accuracy: 0.9904  
Epoch 8: val_loss improved from 0.13199 to 0.11044, saving model to best_tower_detection_model.h5
172/172 [==============================] - 276s 2s/step - loss: 0.0272 - accuracy: 0.9904 - val_loss: 0.1104 - val_accuracy: 0.9624
Epoch 9/10
172/172 [==============================] - ETA: 0s - loss: 0.0251 - accuracy: 0.9914  
Epoch 9: val_loss improved from 0.11044 to 0.09835, saving model to best_tower_detection_model.h5
172/172 [==============================] - 258s 1s/step - loss: 0.0251 - accuracy: 0.9914 - val_loss: 0.0983 - val_accuracy: 0.9673
Epoch 10/10
172/172 [==============================] - ETA: 0s - loss: 0.0153 - accuracy: 0.9945      
Epoch 10: val_loss did not improve from 0.09835
172/172 [==============================] - 253s 1s/step - loss: 0.0153 - accuracy: 0.9945 - val_loss: 0.1968 - val_accuracy: 0.9493
20/20 [==============================] - 15s 759ms/step - loss: 0.1968 - accuracy: 0.9493
Test Loss: 0.19676004350185394, Test Accuracy: 0.9492635130882263
'''