import os
import random
import requests

API_URL = "http://127.0.0.1:5000/predict_image" 
DATA_DIR = "Data/Raw"  

def get_random_image():
    all_images = []
    for subdir in ["tower", "no_tower"]:
        dir_path = os.path.join(DATA_DIR, subdir)
        if os.path.exists(dir_path):
            all_images.extend([os.path.join(dir_path, f) for f in os.listdir(dir_path) if f.lower().endswith(".jpg")])
    if not all_images:
        raise FileNotFoundError("No images found in the dataset directories")
    return random.choice(all_images)

def test_api():
    try:
        image_path = get_random_image()
        print(f"Selected Image: {image_path}")
        
        with open(image_path, "rb") as img_file:
            response = requests.post(API_URL, files={"image": img_file})
        
        if response.status_code == 200:
            print("Response:", response.json())
        else:
            print(f"Failed with status code {response.status_code}")
            print("Error:", response.text)
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    test_api()
