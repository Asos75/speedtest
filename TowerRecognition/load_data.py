import os
import albumentations as A
from albumentations.pytorch import ToTensorV2
from torchvision import datasets
from torch.utils.data import DataLoader, random_split, SubsetRandomSampler, ConcatDataset
from PIL import Image
import numpy as np

class AlbumentationsDataset:
    def __init__(self, dataset, transform):
        """
        Custom dataset to apply Albumentations transformations.

        Args:
            dataset: The original dataset (e.g., torchvision.datasets.ImageFolder).
            transform: Albumentations transformation pipeline.
        """
        self.dataset = dataset
        self.transform = transform

    def __getitem__(self, index):
        image, label = self.dataset[index]
        # Convert PIL image to numpy array for Albumentations
        image = np.array(image)
        # Apply Albumentations transformations
        image = self.transform(image=image)['image']
        return image, label

    def __len__(self):
        return len(self.dataset)

def augment_dataset(dataset, transform, expansion_factor):
    """
    Expands the dataset by applying transformations multiple times.

    Args:
        dataset: Original dataset to augment.
        transform: Albumentations transformation pipeline.
        expansion_factor: Number of augmented versions to create for each original image.

    Returns:
        ConcatDataset: Dataset containing the original and augmented images.
    """
    augmented_datasets = [dataset]  # Start with the original dataset

    for _ in range(expansion_factor):
        augmented_datasets.append(AlbumentationsDataset(dataset, transform))

    return ConcatDataset(augmented_datasets)

def load_data(data_dir, subset_size=0, batch_size=32, train_split=0.9, expansion_factor=5):
    """
    Load and preprocess the dataset, then split it into training and validation sets.
    Augment the dataset to expand its size.

    Args:
        data_dir (str): The directory where the dataset is stored.
        subset_size (int, optional): Size of the subset. If 0 then it takes the whole dataset. Default is 0.
        batch_size (int, optional): The batch size for the DataLoader. Default is 32.
        train_split (float, optional): The fraction of data to use for training. Default is 0.9.
        expansion_factor (int, optional): Number of augmented versions to add per original image. Default is 4.

    Returns:
        train_loader (DataLoader): DataLoader for the training dataset.
        valid_loader (DataLoader): DataLoader for the validation dataset.
    """
    # Albumentations transformation pipeline
    augment_transform = A.Compose([
        A.Resize(64, 64),  # Resize to 64x64
        A.Rotate(limit=30, p=0.5),  # Random rotation
        A.ShiftScaleRotate(shift_limit=0.1, scale_limit=0.1, rotate_limit=10, p=0.5),  # Translation and scaling
        A.RandomBrightnessContrast(p=0.5),  # Random brightness and contrast
        A.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),  # Normalize
        ToTensorV2(),  # Convert to tensor
    ])

    # Load the original dataset
    raw_dataset = datasets.ImageFolder(root=data_dir)

    # Expand the dataset using augmentation
    expanded_dataset = augment_dataset(raw_dataset, augment_transform, expansion_factor)

    # Split the dataset into training (90%) and validation (10%) sets
    train_size = int(train_split * len(expanded_dataset))
    valid_size = len(expanded_dataset) - train_size
    train_dataset, valid_dataset = random_split(expanded_dataset, [train_size, valid_size])

    # Create DataLoader for batching
    if subset_size == 0:
        train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
        valid_loader = DataLoader(valid_dataset, batch_size=batch_size, shuffle=False)
    else:
        train_loader = DataLoader(
            train_dataset, batch_size=batch_size,
            sampler=SubsetRandomSampler(range(int(subset_size * 0.9)))
        )
        valid_loader = DataLoader(
            valid_dataset, batch_size=batch_size,
            sampler=SubsetRandomSampler(range(int(subset_size * 0.1)))
        )

    print(f"Subset size: {subset_size}, Dataset length: {len(raw_dataset)} (Training size: {len(train_loader.dataset)}, Validation size: {len(valid_loader.dataset)})")

    return train_loader, valid_loader
