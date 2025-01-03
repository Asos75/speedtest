import os
import random
import requests

# Configuration
API_URL = "http://127.0.0.1:5000/predict_image"  
DATA_DIR = "Data/Raw"  
MAX_RETRIES = 100  

def get_random_image():
    all_images = []
    for subdir in ["tower", "no_tower"]:
        dir_path = os.path.join(DATA_DIR, subdir)
        if os.path.exists(dir_path):
            all_images.extend([os.path.join(dir_path, f) for f in os.listdir(dir_path) if f.lower().endswith(".jpg")])
    if not all_images:
        raise FileNotFoundError("No images found in the dataset directories")
    return random.choice(all_images)

def test_api_with_retries(n):
    attempts = 0
    success = 0
    while attempts < n:
        try:
            image_path = get_random_image()
            print(f"Attempt {attempts + 1}: Selected Image: {image_path}")
            
            with open(image_path, "rb") as img_file:
                response = requests.post(API_URL, files={"image": img_file})
            
            if response.status_code == 200:
                prediction = response.json()
                is_tower = prediction.get('is_tower', None)
                
                expected_result = 'true' if 'no_tower' not in image_path else 'false'
                
                if is_tower is not None and str(is_tower).lower() == expected_result:
                    print(f"Success on attempt {attempts + 1}: Response:", prediction)
                    success += 1
                else:
                    print(f"Failure on attempt {attempts + 1}: Expected {expected_result}, but got {is_tower}")
            else:
                print(f"Failed with status code {response.status_code} on attempt {attempts + 1}")
                print("Error:", response.text)
        
        except Exception as e:
            print(f"An error occurred on attempt {attempts + 1}: {e}")
        
        attempts += 1
    
    success_percentage = (success / n) * 100
    print(f"Success percentage after {n} attempts: {success_percentage:.2f}%")

if __name__ == "__main__":
    test_api_with_retries(MAX_RETRIES)
