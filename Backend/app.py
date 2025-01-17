import os
import random
import cv2
import numpy as np
import base64
from flask import Flask, request, jsonify, render_template_string
from tensorflow.keras.preprocessing.image import load_img, img_to_array
import tensorflow as tf
# from model import build_model 

# Configuration
MODEL_PATH = "app/best_tower_model.h5"
RAW_DIR = "Data/Raw/tower"
INPUT_SHAPE = (128, 128, 3)

CLASSIFICATOR_MODEL_PATH = "app/final_tower_detection_model.h5"
classificator_model = tf.keras.models.load_model(CLASSIFICATOR_MODEL_PATH)

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

# Initialize Flask app
app = Flask(__name__)

model = tf.keras.models.load_model('app/best_tower_model.h5')

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

# model = initialize_model()
# Uporabli initialize_model(), če se pri buildanju modela uporablja  #save_weights_only=True
# Druga pa load model best_tower_model.h5.
model = tf.keras.models.load_model('app/best_tower_model.h5')

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
    
    orig_h, orig_w = img.shape[:2]
    
    # Resize and preprocess for model
    resized_img = cv2.resize(img, INPUT_SHAPE[:2])
    processed_img = preprocess_image(resized_img)
    processed_img = processed_img.astype('float32') / 255.0
    model_input = np.expand_dims(processed_img, axis=-1)  # Add channel dimension
    model_input = np.expand_dims(model_input, axis=0)  # Add batch dimension

    # Get predictions
    class_pred, bbox_pred = model.predict(model_input)
    class_conf = float(class_pred[0][0])
    
    # Convert normalized coordinates to pixel coordinates
    x1 = int(bbox_pred[0][0] * orig_w)
    y1 = int(bbox_pred[0][1] * orig_h)
    x2 = int(bbox_pred[0][2] * orig_w)
    y2 = int(bbox_pred[0][3] * orig_h)
    
    # Draw rectangle on original image
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


@app.route("/predict_uploaded_image", methods=["POST"])
def predict_uploaded_image():
    if 'image' not in request.files:
        return jsonify({"error": "No image file provided"}), 400

    file = request.files['image']

    if file.filename == '':
        return jsonify({"error": "Empty filename"}), 400

    try:
        # Read the image file
        img = BytesIO(file.read())
        file.seek(0)  # Reset the file pointer for further processing
        
        # Load the image using OpenCV
        nparr = np.frombuffer(img.getvalue(), np.uint8)
        img_cv2 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if img_cv2 is None:
            return jsonify({"error": "Invalid image format"}), 400

        orig_h, orig_w = img_cv2.shape[:2]

        # Resize and preprocess the image
        resized_img = cv2.resize(img_cv2, INPUT_SHAPE[:2])
        processed_img = preprocess_image(resized_img)
        processed_img = processed_img.astype('float32') / 255.0
        model_input = np.expand_dims(processed_img, axis=-1)  # Add channel dimension
        model_input = np.expand_dims(model_input, axis=0)  # Add batch dimension

        # Get predictions
        class_pred, bbox_pred = model.predict(model_input)
        class_conf = float(class_pred[0][0])

        # Convert normalized coordinates to pixel coordinates
        x1 = int(bbox_pred[0][0] * orig_w)
        y1 = int(bbox_pred[0][1] * orig_h)
        x2 = int(bbox_pred[0][2] * orig_w)
        y2 = int(bbox_pred[0][3] * orig_h)

        # Draw the bounding box on the original image
        cv2.rectangle(img_cv2, (x1, y1), (x2, y2), (0, 255, 0), 2)

        # Convert the image with bounding box to base64
        _, buffer = cv2.imencode('.jpg', img_cv2)
        img_base64 = base64.b64encode(buffer).decode('utf-8')

        # Return the results as JSON
        return jsonify({
            "filename": file.filename,
            "confidence": class_conf,
            "x1": x1,
            "y1": y1,
            "x2": x2,
            "y2": y2,
            "image": img_base64
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500



from io import BytesIO

def predict_tower(img, target_size=(224, 224)):
    img = load_img(img, target_size=target_size)
    img_array = img_to_array(img) / 255.0  # Normalize the image
    img_array = np.expand_dims(img_array, axis=0)  # Add batch dimension
    prediction = classificator_model.predict(img_array)
    
    # If the model predicts > 0.5, return "Tower detected"
    if prediction[0] > 0.5:
        return "Tower detected!", True
    else:
        return "No tower detected!", False



@app.route("/predict_image", methods=["POST"])
def predict_image():
    if 'image' not in request.files:
        return jsonify({"error": "No image file provided"}), 400
    
    file = request.files['image']
    
    if file.filename == '':
        return jsonify({"error": "Empty filename"}), 400

    try:
        img = BytesIO(file.read())
        
        result, is_tower = predict_tower(img)
        
        return jsonify({
            "result": result,
            "is_tower": is_tower,  
            "image_name": file.filename
        })
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(debug=True, port=6000)