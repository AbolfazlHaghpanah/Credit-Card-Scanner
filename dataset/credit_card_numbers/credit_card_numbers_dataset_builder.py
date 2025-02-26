from typing import Iterator, Tuple, Any, Dict

import tensorflow_datasets as tfds
import tensorflow as tf
import os


class CreditCardNumbers(tfds.core.GeneratorBasedBuilder):
    """DatasetBuilder for digit classification (0-9)."""

    VERSION = tfds.core.Version("1.0.0")
    MANUAL_DOWNLOAD_INSTRUCTIONS = """
    Please download or prepare your dataset manually by placing it in the following directory structure:
    dataset ----
        credit_card_numbers ----
            builder files ----
        labeled_data ----
            0 ----
                0_1.png
                ...
            1 ----
            ...
    """

    def _info(self):
        return tfds.core.DatasetInfo(
            builder=self,
            description="Dataset of handwritten digits 0-9.",
            features=tfds.features.FeaturesDict({
                "image": tfds.features.Image(shape=(None, None, 3)),  # Adjust shape if needed
                "label": tfds.features.ClassLabel(num_classes=10),
            }),
            supervised_keys=("image", "label"),
        )

    def _split_generators(self, dl_manager):
        """Returns SplitGenerators."""
        dataset_path = os.path.join(os.path.dirname(__file__), "../..", "dataset/labeled_data")

        return {
            "train": self._generate_examples(os.path.join(dataset_path))
        }

    def _generate_examples(self, path) -> Iterator[Tuple[str, Dict[str, Any]]]:
        """Yields examples."""
        for label in range(10):
            label_dir = os.path.join(path, label.__str__())

            if not os.path.isdir(label_dir):
                raise ValueError("boobs")

            for image_file in os.listdir(label_dir):
                if image_file.endswith(".png"):
                    yield image_file, {
                        "image": os.path.join(label_dir, image_file),
                        "label": label,
                    }
