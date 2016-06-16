"""
Data generator for Text detection training.
"""

import glob
import sys
import numpy as np
import scipy.ndimage
from scipy.ndimage.filters import gaussian_filter

from datetime import datetime
from datetime import timedelta

from numpy.random import RandomState

from PIL import Image, ImageFont, ImageDraw

from dooer.ai.common.image import normalize_pixels
from dooer.ai.common.numpy_utils import fast_choice



class StringImageBatchGenerator:
    """
    Generates batches of images containing self.rnd.random text. Used to train the CTC OCR.
    """

    def __init__(self,
                 string_generator,
                 string_renderer,
                 reload_fonts_interval=100,
                 max_string_lens_range=(10, 20),
                 batch_size=32,
                 random_state=None):

        self.string_generator = string_generator
        self.string_renderer = string_renderer
        self.batch_size = batch_size
        self.reload_fonts_interval = reload_fonts_interval
        self.max_string_lens_range = max_string_lens_range
        self.rnd = random_state if random_state is not None else RandomState()

        self.num_generated = 0

    def get_random_max_string_len(self):
        return self.rnd.randint(self.max_string_lens_range[0],
                                self.max_string_lens_range[1] + 1)

    def get_batch(self, batch_size):
        """
        Returns one batch of `batch_size` size.
        """

        words = []
        images = []
        targets = []

        max_string_len = self.get_random_max_string_len()

        for i in range(batch_size):
            string = self.string_generator.get_string(max_string_len=max_string_len)
            image, target_mask = self.string_renderer.render_string(string)

            words.append(string)
            targets.append(target_mask)
            images.append(image)

        images = np.expand_dims(images, axis=1)
        targets = np.expand_dims(targets, axis=1)

        return images, targets  # words

    def generate(self, num_batches=None, raise_exceptions=False):
        """
        Generates `num_batches` images with the corresponding training targets and
        optionally words.

        Args:
            num_batches: int or None
                Number of batches to generate.
                If None the generator will just keep running.
            yield_words: bool
                Yield images, targets and words, or only images and targets
            raise_exceptions: bool
                Should exceptions be caught to make sure training does not fail.

        Returns:
            images: [array]
                List of generated images.
            targets: array
                Label sequences corrensponding to words of shape
                (num_batches, max_word_len).
            words: [str]
                List of words/strings in the images.
                Only returned if yield_words is True.
        """

        batch_count = 0

        while num_batches is None or batch_count < num_batches:
            try:
                images, targets = self.get_batch(self.batch_size)

                # Reload the fonts with different sizes at regular intervals
                batch_count += 1
                if batch_count % self.reload_fonts_interval == 0:
                    self.string_renderer.load_fonts()
                    self.string_renderer.load_backgrounds()

                self.num_generated += self.batch_size

                yield images, targets

            except Exception as ex:
                # To make sure training does not fail just because some exception, catch it here.
                # It there is an error, just generate a new batch...
                print("Data generator exception", ex, file=sys.stderr)
                if raise_exceptions:
                    raise


class StringGenerator:
    """
    Used to generates random strings for the StringImageBatchGenerator
    """

    def __init__(self,
                 text_file,
                 dict_file,
                 number_separators=NUMBER_SEPARATORS,
                 random_state=None):

        self.text_file = text_file
        self.dict_file = dict_file
        self.number_separators = number_separators

        self.rnd = random_state if random_state is not None else RandomState()

        # Set the weight between different string generation methods
        self.generation_funcs = []
        self.generation_funcs += [self.get_cc_number_string] * 1

        # locale.setlocale(locale.LC_ALL, LOCALE)

        self.load_dict()
        self.load_text()

    def generate(self):
        while True:
            string, target = self.get_string()
            yield string, target

    def get_string(self, max_string_len):
        """
        Returns a randomly generated string using one of the generation methods.
        """

        num_lines = self.rnd.randint(1, 8)

        assert max_string_len >= 1

        generation_func = self.rnd.choice(self.generation_funcs)

        strings = []

        for line in range(num_lines):
            string = generation_func(max_string_len)
            string = string.strip()
            strings.append(string)
        string = "\n".join(strings)

        return string

    def load_text(self):
        with open(self.text_file, 'r') as f:
            self.text = f.read()

    def load_dict(self):
        with open(self.dict_file, 'r') as f:
            self.dict_words = f.readlines()
        self.dict_words = [word.rstrip('\n') for word in self.dict_words]

    def get_cc_number_string(self, *args):
        price = self.rnd.randint(0, 10 ** self.rnd.randint(low=1, high=8))
        if self.rnd.rand() > 0.3:
            cents = self.rnd.randint(0, self.rnd.randint(1, 2) * 10)
        else:
            cents = '00'  # 00 is very common at the end of numbers, prices..

        price_format = fast_choice(self.rnd, PRICE_FORMATS)
        currency = fast_choice(self.rnd, CURRENCIES)

        return price_format.format(price=price, cents=cents, currency=currency)



class StringRenderer:
    """
    Renders a string using random fonts and applies a random
    transformatios/distortions. See .render_string()
    """

    def __init__(self,
                 font_folder,
                 backgrounds_folder,
                 neg_backgrounds_folder,
                 image_size=(80, 80),
                 target_size=None,
                 num_background_variations=3,
                 rotation_sigma=3,
                 max_noise_sigma=0.02,
                 font_size_range=(14, 30),
                 num_font_sizes=10,
                 max_blur_sigma=1.0,
                 max_background_mixture=0.75,
                 background_probability=0.75,
                 random_state=None):

        self.font_folder = font_folder
        self.backgrounds_folder = backgrounds_folder
        self.neg_backgrounds_folder = neg_backgrounds_folder
        self.image_size = image_size
        self.image_padding = 20
        self.render_image_size = (image_size[0] + self.image_padding * 2,
                                  image_size[1] + self.image_padding * 2)
        self.num_background_variations = num_background_variations
        self.rotation_sigma = rotation_sigma  # degrees
        self.max_noise_sigma = max_noise_sigma
        self.font_size_range = font_size_range
        self.max_blur_sigma = max_blur_sigma
        self.max_background_mixture = max_background_mixture
        self.num_font_sizes = num_font_sizes

        if target_size is None:
            self.target_size = (image_size[0] // 2, image_size[1] // 2)
        else:
            self.target_size = target_size

        self.background_probability = background_probability
        self.rnd = random_state if random_state is not None else RandomState()

        self.load_fonts()
        self.load_backgrounds()

        self.num_generated = 0

    def load_backgrounds(self):
        self.backgrounds = self.load_backgrounds_from_folder(self.backgrounds_folder)
        self.neg_backgrounds = self.load_backgrounds_from_folder(self.neg_backgrounds_folder)

    def load_backgrounds_from_folder(self, folder):
        image_files_pattern = '{}/*.jpg'.format(folder)
        image_files = glob.iglob(image_files_pattern)
        backgrounds = []
        for image_file in image_files:
            background = scipy.ndimage.imread(image_file, flatten=True)
            for i in range(self.num_background_variations):
                scale = self.rnd.rand() + 0.5
                new_size = tuple(np.int32(np.array(background.shape[:2]) * scale))
                scaled_background = scipy.misc.imresize(background, new_size, interp='bilinear')
                scaled_background = normalize_pixels(scaled_background, invert=True)
                scaled_background, _ = self.rotate_image(scaled_background)
                backgrounds.append(scaled_background)
        return backgrounds

    def load_fonts(self):
        """
        Loads all fonts with num_font_sizes self.rnd.random font sizes each.
        """
        font_files_pattern = '%s/*.ttf' % self.font_folder
        font_files = glob.iglob(font_files_pattern)
        self.fonts = []

        for i in range(self.num_font_sizes):
            for font_file in font_files:
                font_size = self.rnd.randint(self.font_size_range[0], self.font_size_range[1] + 1)
                font = ImageFont.truetype(font_file, font_size)
                self.fonts.append(font)

        assert len(self.fonts) > 0

    def get_random_font(self):
        return self.rnd.choice(self.fonts)

    def add_background(self, image, backgrounds, background_probability):

        if self.rnd.rand() < background_probability and len(backgrounds) > 0:
            background = fast_choice(self.rnd, backgrounds)

            y = self.rnd.randint(max(1, background.shape[0] - image.shape[0]))
            x = self.rnd.randint(max(1, background.shape[1] - image.shape[1]))

            background_patch = background[y:y + image.shape[0], x:x + image.shape[1]]
            if self.rnd.rand() >= .5:
                background_patch = np.fliplr(background_patch)
            if self.rnd.rand() >= .5:
                background_patch = np.flipud(background_patch)
            if self.rnd.rand() >= .5:
                background_patch = np.rot90(background_patch, k=self.rnd.randint(low=1, high=5))

            if background_patch.shape == image.shape:
                fraction = self.rnd.rand() * self.max_background_mixture
                image = fraction * background_patch + (1 - fraction) * image

        return image

    def add_text_thining(self, image):
        if self.rnd.rand() < self.background_probability and len(self.backgrounds) > 0:
            if self.max_background_mixture > 0:
                background = fast_choice(self.rnd, self.backgrounds)
                y = self.rnd.randint(max(1, background.shape[0] - image.shape[0]))
                x = self.rnd.randint(max(1, background.shape[1] - image.shape[1]))

                background_patch = background[y:y + image.shape[0], x:x + image.shape[1]]

                if background_patch.shape == image.shape:
                    image -= background_patch
                    image[image < 0] = 0

        return image

    def add_noise(self, image):
        if self.max_noise_sigma > 0:
            noise_sigma = np.abs(self.rnd.rand() * self.max_noise_sigma)
            image += self.rnd.randn(image.shape[0], image.shape[1]) * noise_sigma
        return image

    def add_blur(self, image):
        if self.max_blur_sigma > 0:
            blur_sigma = self.rnd.rand() * self.max_blur_sigma
            image = gaussian_filter(image, sigma=blur_sigma, mode='mirror', cval=0)
        return image

    def add_binarize(self, image):
        if self.rnd.rand() > 0.95:
            image[image > 0.75] = 1
        return image

    def rotate_image(self, image):
        angle = 0
        if self.rotation_sigma > 0 and self.rnd.rand() > 0.1:
            angle = self.rnd.randn() * self.rotation_sigma
            if abs(angle) > 0.1:
                image = scipy.ndimage.interpolation.rotate(image, angle=angle, order=1, mode='constant', cval=0)
                image[image < 0.05] = 0
        return image, angle


    def render_string(self, string, font=None):
        """
        Renders a string using a font.

        Args:
            string: str
                String to render.
            font: ImageFont or None
                Pillow ImageFont. If None an random font is
                chosen.
        Returns:
            image: array, float32
                Grayscale image.
        """

        if self.rnd.rand() > 0.05:

            if font is None:
                font = self.get_random_font()

            text_image = Image.new('F', self.render_image_size, color=0)
            draw = ImageDraw.Draw(text_image)

            text_position = (self.rnd.randint(-self.render_image_size[0], self.render_image_size[0]),
                             self.rnd.randint(-self.render_image_size[1], self.render_image_size[1]))

            draw.text(text_position, string, 1, font=font)

            text_image = np.array(text_image, dtype=np.float32)

            text_image, angle = self.rotate_image(text_image)
            text_image = text_image[self.image_padding:self.render_image_size[0] - self.image_padding,
                         self.image_padding:self.render_image_size[1] - self.image_padding]

            assert text_image.shape == self.image_size

            image = text_image.copy()
            image = self.add_binarize(image)
            image = self.add_text_thining(image)
            image = self.add_noise(image)
            image = self.add_blur(image)
            image = self.add_background(image, self.backgrounds,
                                        background_probability=self.background_probability)
        else:
            text_image = np.zeros(self.image_size, dtype=np.float32)
            image = text_image.copy()
            image = self.add_background(image, self.neg_backgrounds, background_probability=1)

        image = normalize_pixels(image, invert=False)

        if self.image_size != self.target_size:
            text_image = np.float32(scipy.misc.imresize(text_image, self.target_size, interp='bilinear'))

        image_max = text_image.max()
        if image_max != 0:
            text_image /= image_max

        mask = text_image > 0.25
        text_image[mask] = 1
        text_image[np.logical_not(mask)] = 0

        return image, text_image

