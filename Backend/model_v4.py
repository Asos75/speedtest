import os
import json
import shutil
import random
import tensorflow as tf
from tensorflow.keras import layers, models
from PIL import Image

# Paths
data_dir = "Data"
processed_dir = os.path.join(data_dir, "Processed")
raw_no_tower_dir = os.path.join(data_dir, "Raw", "no_tower")
bounding_boxes_path = os.path.join(data_dir, "bounding_boxes_list.json")
combined_dir = os.path.join(data_dir, "Combined")
output_dir = "TFRecords"
os.makedirs(combined_dir, exist_ok=True)
os.makedirs(output_dir, exist_ok=True)

# Load bounding boxes
with open(bounding_boxes_path) as f:
    bounding_boxes = json.load(f)

# Prepare labeled data from Processed
labeled_data = []
for image_file in os.listdir(processed_dir):
    if image_file in bounding_boxes:
        labeled_data.append({
            "file_path": os.path.join(processed_dir, image_file),
            "label": 1,  # Tower present
            "bounding_box": bounding_boxes[image_file]
        })

# Prepare negative data (no tower)
negative_data = []
for no_tower_image in os.listdir(raw_no_tower_dir):
    negative_data.append({
        "file_path": os.path.join(raw_no_tower_dir, no_tower_image),
        "label": 0  # No tower
    })

# Limit the negatives to match the number of labeled data
random.shuffle(negative_data)
negative_data = negative_data[:len(labeled_data)]

# Combine and shuffle data
all_data = labeled_data + negative_data
random.shuffle(all_data)

# Split data into training, validation, and test sets
train_ratio, val_ratio = 0.8, 0.1
train_split = int(len(all_data) * train_ratio)
val_split = train_split + int(len(all_data) * val_ratio)

train_data = all_data[:train_split]
val_data = all_data[train_split:val_split]
test_data = all_data[val_split:]

# Function to copy data to respective folders
def copy_data(data, subset):
    subset_dir = os.path.join(combined_dir, subset)
    os.makedirs(subset_dir, exist_ok=True)
    for item in data:
        dest_path = os.path.join(subset_dir, os.path.basename(item["file_path"]))
        shutil.copy(item["file_path"], dest_path)

# Copy files to Combined directory
copy_data(train_data, "train")
copy_data(val_data, "val")
copy_data(test_data, "test")

print(f"Data combined and split: {len(train_data)} train, {len(val_data)} val, {len(test_data)} test.")

# Helper function to create TF Example
def create_tf_example(image_path, label, bounding_box=None):
    with open(image_path, "rb") as img_file:
        encoded_image = img_file.read()
    image = Image.open(image_path)
    width, height = image.size

    x_min, y_min, x_max, y_max = (0, 0, 0, 0)
    if bounding_box:
        x_min, y_min, x_max, y_max = bounding_box
        x_min, x_max = x_min / width, x_max / width
        y_min, y_max = y_min / height, y_max / height

    feature = {
        "image/encoded": tf.train.Feature(bytes_list=tf.train.BytesList(value=[encoded_image])),
        "image/filename": tf.train.Feature(bytes_list=tf.train.BytesList(value=[image_path.encode()])),
        "image/format": tf.train.Feature(bytes_list=tf.train.BytesList(value=[b"jpeg"])),
        "image/object/bbox/xmin": tf.train.Feature(float_list=tf.train.FloatList(value=[x_min])),
        "image/object/bbox/ymin": tf.train.Feature(float_list=tf.train.FloatList(value=[y_min])),
        "image/object/bbox/xmax": tf.train.Feature(float_list=tf.train.FloatList(value=[x_max])),
        "image/object/bbox/ymax": tf.train.Feature(float_list=tf.train.FloatList(value=[y_max])),
        "image/object/class/label": tf.train.Feature(int64_list=tf.train.Int64List(value=[label])),
    }
    return tf.train.Example(features=tf.train.Features(feature=feature))

# Function to create TFRecord file
def write_tfrecord(data_subset, output_file):
    with tf.io.TFRecordWriter(output_file) as writer:
        for item in data_subset:
            image_path = item["file_path"]
            label = item["label"]
            bounding_box = item.get("bounding_box")
            tf_example = create_tf_example(image_path, label, bounding_box)
            writer.write(tf_example.SerializeToString())

# Prepare train, val, and test TFRecords
for subset in ["train", "val", "test"]:
    subset_dir = os.path.join(combined_dir, subset)
    data_subset = []
    for image_file in os.listdir(subset_dir):
        file_path = os.path.join(subset_dir, image_file)
        if image_file in bounding_boxes:
            data_subset.append({"file_path": file_path, "label": 1, "bounding_box": bounding_boxes[image_file]})
        else:
            data_subset.append({"file_path": file_path, "label": 0})  # No bounding box

    tfrecord_path = os.path.join(output_dir, f"{subset}.tfrecord")
    write_tfrecord(data_subset, tfrecord_path)
    print(f"Created {tfrecord_path}")

# Define the model (Simple CNN)
def create_model(input_shape=(256, 256, 3)):
    model = models.Sequential([
        layers.InputLayer(input_shape=input_shape),
        layers.Conv2D(32, (3, 3), activation='relu'),
        layers.MaxPooling2D(pool_size=(2, 2)),
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.MaxPooling2D(pool_size=(2, 2)),
        layers.Conv2D(128, (3, 3), activation='relu'),
        layers.Flatten(),
        layers.Dense(128, activation='relu'),
        layers.Dense(1, activation='sigmoid')  # Binary classification (Tower or No Tower)
    ])
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    return model

# Parse the TFRecord file
def _parse_function(proto):
    # Define your `tfrecord` feature structure
    keys_to_features = {
        "image/encoded": tf.io.FixedLenFeature([], tf.string),
        "image/object/class/label": tf.io.FixedLenFeature([1], tf.int64),
        "image/filename": tf.io.FixedLenFeature([], tf.string),
        "image/object/bbox/xmin": tf.io.FixedLenFeature([1], tf.float32),
        "image/object/bbox/ymin": tf.io.FixedLenFeature([1], tf.float32),
        "image/object/bbox/xmax": tf.io.FixedLenFeature([1], tf.float32),
        "image/object/bbox/ymax": tf.io.FixedLenFeature([1], tf.float32),
    }

    # Parse the input `tfrecord`
    parsed_features = tf.io.parse_single_example(proto, keys_to_features)

    # Decode the image
    image = tf.io.decode_jpeg(parsed_features['image/encoded'], channels=3)
    label = parsed_features['image/object/class/label'][0]

    # Normalize and resize image
    image = tf.image.resize(image, [256, 256])  # Resize to your model's expected input size
    image = tf.cast(image, tf.float32) / 255.0  # Normalize to [0,1]

    return image, label

# Load the data from TFRecord files
def load_tfrecord_data(tfrecord_file, batch_size=32):
    dataset = tf.data.TFRecordDataset(tfrecord_file)
    dataset = dataset.map(_parse_function)
    dataset = dataset.shuffle(1000)  # Shuffle data
    dataset = dataset.batch(batch_size)
    dataset = dataset.prefetch(tf.data.experimental.AUTOTUNE)  # Optimize data loading
    return dataset

# Define paths to the TFRecord files
train_tfrecords = "TFRecords/train.tfrecord"
val_tfrecords = "TFRecords/val.tfrecord"
test_tfrecords = "TFRecords/test.tfrecord"

# Load the datasets
train_dataset = load_tfrecord_data(train_tfrecords, batch_size=32)
val_dataset = load_tfrecord_data(val_tfrecords, batch_size=32)
test_dataset = load_tfrecord_data(test_tfrecords, batch_size=32)

# Create and train the model
model = create_model(input_shape=(256, 256, 3))  # Modify the input shape as per your model
history = model.fit(train_dataset, epochs=10, validation_data=val_dataset)

# Evaluate the model on the test data
test_loss, test_acc = model.evaluate(test_dataset)
print(f"Test Accuracy: {test_acc:.4f}")

# Save the model
model.save("tower_detection_model_v4.h5")
print("Model saved as 'tower_detection_model_v4.h5'")
