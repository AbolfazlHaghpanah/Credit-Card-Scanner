import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import tensorflow_datasets as tfds
from orca.debug import println

if __name__ == '__main__':
    dataset = tfds.load(
        name="credit_card_numbers"
    )
    train_dataset = dataset["train"]

    for i in range(10):
        print("booooooooooooooooooooooooooooooooooooooooooooooooooooooooooooobs")
        for example in train_dataset.take(i):
            image, label = (example['image'], example['label'])
            print(str(label))
