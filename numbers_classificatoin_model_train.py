import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import tensorflow_datasets as tfds

if __name__ == '__main__':
    dataset = tfds.load("credit_card_numbers", split="train")
    print("das")