import os
import requests
import sys

# Configuration
API_URL = "http://127.0.0.1:5000/predict_image"  # Change this to your server's URL if different

# Function to test the API with a given image path
def test_api_with_image(image_path):
    if not os.path.exists(image_path):
        print(f"Error: Image file {image_path} does not exist")
        return
    
    try:
        # Open the image and send it to the API
        with open(image_path, "rb") as img_file:
            response = requests.post(API_URL, files={"image": img_file})
        
        # Handle the API response
        if response.status_code == 200:
            prediction = response.json()
            result = prediction.get('result', '')
            
            print(f"Result for {image_path}: {result}")
        else:
            print(f"Failed with status code {response.status_code}")
            print("Error:", response.text)
    
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python test_image_api.py <image_path>")
        sys.exit(1)
    
    image_path = sys.argv[1]
    test_api_with_image(image_path)
