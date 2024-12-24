import time
import torch
import torch.nn as nn
import torch.optim as optim
from load_data import load_data

data_dir = './downloaded_images/'


train_loader, valid_loader = load_data(data_dir, subset_size=0)

print("Loaded data")
