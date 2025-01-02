import os
import random
import cv2
import numpy as np
import base64
from flask import Flask, request, jsonify, render_template_string
import tensorflow as tf
from model import build_model  # Import the model architecture from model.py

# Configuration
MODEL_PATH = "best_tower_model.h5"
RAW_DIR = "Data/Raw"
INPUT_SHAPE = (224, 224, 3)

# Initialize Flask app
app = Flask(__name__)

# Create and load model
def initialize_model():
    if os.path.exists(MODEL_PATH):
        return tf.keras.models.load_model(MODEL_PATH, compile=False)
    else:
        model = build_model(input_shape=INPUT_SHAPE)
        model.compile(
            optimizer=tf.keras.optimizers.Adam(learning_rate=1e-4),
            loss={
                'class_output': 'binary_crossentropy',
                'bbox_output': 'huber'
            },
            metrics={
                'class_output': 'accuracy',
                'bbox_output': 'mae'
            }
        )
        return model

model = initialize_model()

HTML_TEMPLATE = """
<!DOCTYPE html>
<html>
<head>
    <title>Tower Detection</title>
    <style>
        body { font-family: Arial; margin: 20px; }
        .container { max-width: 800px; margin: auto; }
        .image { margin: 20px 0; }
        .stats { margin: 10px 0; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Mobile Tower Detection</h2>
        <div class="stats">
            <p>Filename: {{ filename }}</p>
            <p>Confidence: {{ "%.2f"|format(confidence) }}</p>
            <p>Location: [{{ x1 }}, {{ y1 }}, {{ x2 }}, {{ y2 }}]</p>
        </div>
        <div class="image">
            <img src="data:image/jpeg;base64,{{ image }}" style="max-width:100%">
        </div>
        <button onclick="window.location.reload()">Next Image</button>
    </div>
</body>
</html>
"""

@app.route("/")
def index():
    return """
    <h1>Tower Detection API</h1>
    <p>Use /predict to analyze random image</p>
    """

@app.route("/predict")
def predict():
    raw_files = [f for f in os.listdir(RAW_DIR) if f.lower().endswith(".jpg")]
    if not raw_files:
        return "No images found", 400
    
    random_file = random.choice(raw_files)
    image_path = os.path.join(RAW_DIR, random_file)
    
    # Load and prepare image
    img = cv2.imread(image_path)
    if img is None:
        return "Could not read image", 400
    
    # Convert BGR to RGB
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    orig_h, orig_w = img.shape[:2]
    
    # Resize and preprocess
    resized_img = cv2.resize(img, INPUT_SHAPE[:2])
    model_input = resized_img.astype('float32') / 255.0
    model_input = np.expand_dims(model_input, axis=0)

    # Get predictions
    class_pred, bbox_pred = model.predict(model_input)
    class_conf = float(class_pred[0][0])
    
    # Convert normalized coordinates to pixel coordinates
    x1 = int(bbox_pred[0][0] * orig_w)
    y1 = int(bbox_pred[0][1] * orig_h)
    x2 = int(bbox_pred[0][2] * orig_w)
    y2 = int(bbox_pred[0][3] * orig_h)
    
    # Convert back to BGR for OpenCV
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
    
    # Draw rectangle
    cv2.rectangle(img, (x1, y1), (x2, y2), (0, 255, 0), 2)
    
    # Convert to base64
    _, buffer = cv2.imencode('.jpg', img)
    img_base64 = base64.b64encode(buffer).decode('utf-8')
    
    return render_template_string(HTML_TEMPLATE,
        filename=random_file,
        confidence=class_conf,
        x1=x1, y1=y1, x2=x2, y2=y2,
        image=img_base64
    )

if __name__ == "__main__":
    app.run(debug=True)