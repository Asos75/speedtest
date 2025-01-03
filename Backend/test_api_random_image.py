import os
import random
import requests

# Configuration
API_URL = "http://127.0.0.1:5000/predict_image"  # Change this to your server's URL if different
DATA_DIR = "Data/Raw"  # Path to your raw data directory

# Function to pick a random image
def get_random_image():
    all_images = []
    for subdir in ["tower", "no_tower"]:
        dir_path = os.path.join(DATA_DIR, subdir)
        if os.path.exists(dir_path):
            all_images.extend([os.path.join(dir_path, f) for f in os.listdir(dir_path) if f.lower().endswith(".jpg")])
    if not all_images:
        raise FileNotFoundError("No images found in the dataset directories")
    return random.choice(all_images)

# Function to test the API with a random image
def test_api():
    try:
        # Get a random image
        image_path = get_random_image()
        print(f"Selected Image: {image_path}")
        
        # Open the image and send it to the API
        with open(image_path, "rb") as img_file:
            response = requests.post(API_URL, files={"image": img_file})
        
        # Handle the API response
        if response.status_code == 200:
            print("Response:", response.json())
        else:
            print(f"Failed with status code {response.status_code}")
            print("Error:", response.text)
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    test_api()
