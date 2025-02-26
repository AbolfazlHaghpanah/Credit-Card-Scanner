"""credit_card_numbers dataset."""

from . import credit_card_numbers_dataset_builder
import tensorflow_datasets as tfds
import tensorflow_datasets as tfds
from dataset.credit_card_numbers import CreditCardNumbers


class CreditCardNumbersTest(tfds.testing.DatasetBuilderTestCase):
    """Tests for credit_card_numbers dataset."""
    DATASET_CLASS = CreditCardNumbers
    SPLITS = {
        'train': 3,  # Number of fake train example
        'test': 1,  # Number of fake test example
    }

    def test_dataset(self):
        dataset = tfds.load("credit_card_numbers", split="train", as_supervised=True)
        for image, label in dataset.take(1):
            assert image.shape is not None
            assert 0 <= label < 10


if __name__ == '__main__':
    tfds.testing.test_main()
