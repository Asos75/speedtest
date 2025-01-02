import os
import random
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img, img_to_array

# Load the saved model
model = load_model('best_tower_detection_model.h5')

# Folder structure
DATA_DIR = 'Data/Raw'  # Folder containing both tower and no_tower images

# File paths
tower_images = [os.path.join(DATA_DIR, 'tower', f) for f in os.listdir(os.path.join(DATA_DIR, 'tower'))]
no_tower_images = [os.path.join(DATA_DIR, 'no_tower', f) for f in os.listdir(os.path.join(DATA_DIR, 'no_tower'))]

# Combine the tower and no_tower images into one list
all_images = tower_images + no_tower_images

# Function to make predictions on a random image
def predict_random_tower(target_size=(224, 224)):
    # Select a random image from the combined list
    random_image_path = random.choice(all_images)
    
    # Load the image and preprocess it
    img = load_img(random_image_path, target_size=target_size)
    img_array = img_to_array(img) / 255.0  # Normalize image
    img_array = np.expand_dims(img_array, axis=0)  # Add batch dimension

    # Make prediction
    prediction = model.predict(img_array)
    
    # Interpret the prediction
    if prediction[0] > 0.5:
        return f"Tower detected! Image: {random_image_path}"
    else:
        return f"No tower detected! Image: {random_image_path}"

# Example usage
result = predict_random_tower()
print(result)
