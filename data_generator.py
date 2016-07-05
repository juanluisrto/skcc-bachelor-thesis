import glob
import sys
import numpy as np
import scipy.ndimage

from scipy.ndimage.filters import gaussian_filter
from PIL import Image, ImageFont, ImageDraw
from image import normalize_pixels
from numpy.random import RandomState


def fast_choice(random_state, choices):
    """
    RandomState.choice() is very slow for large lists since it supports weighted sampling.
    Use this instead.
    """
    idx = random_state.randint(low=0, high=len(choices))
    return choices[idx]


class StringGenerator:
    """
    Used to generates random strings for the StringImageBatchGenerator
    """

    def __init__(self,
                 random_state=None):

        self.rnd = random_state if random_state is not None else RandomState()

    def get_string(self):
        """

        Returns:
            credit_card_number: str
                Random string with format xxxx xxxx xxxx xxxx.
        """
        return ' '.join(['{0:04d}'.format(self.rnd.randint(0, 9999)) for _ in range(4)])


class StringRenderer:
    """
    Renders a string using random fonts and applies a random
    transformatios/distortions. See .render_string()
    """

    def __init__(self,
                 fonts_folder,
                 backgrounds_folder,
                 image_size=(160, 160),
                 target_size=None,
                 max_noise_sigma=0.02,
                 font_size_range=(20, 30),
                 max_blur_sigma=1.0,
                 max_background_mixture=0.75,
                 background_probability=0.75,
                 random_state=None):

        self.fonts_folder = fonts_folder
        self.backgrounds_folder = backgrounds_folder
        self.image_size = image_size
        self.target_size = target_size if target_size else image_size
        self.image_padding = 20
        self.render_image_size = (image_size[0] + self.image_padding * 2,
                                  image_size[1] + self.image_padding * 2)
        self.max_noise_sigma = max_noise_sigma
        self.font_size_range = font_size_range
        self.max_blur_sigma = max_blur_sigma
        self.max_background_mixture = max_background_mixture
        self.background_probability = background_probability
        self.num_background_variations = 3

        # Supplie a RandomState object if you want to controll the seed.
        self.rnd = random_state if random_state is not None else RandomState()

        self.fonts = self.load_fonts()
        self.backgrounds = self.load_backgrounds()

    def load_fonts(self):
        font_files = glob.iglob('%s/*.ttf' % self.fonts_folder)
        fonts = [ImageFont.truetype(font_file, font_size)
                 for font_file in font_files
                 for font_size in range(self.font_size_range[0], self.font_size_range[1])]
        return fonts

    def load_backgrounds(self):
        bakground_files = list(glob.iglob('{}/*.jpg'.format(self.backgrounds_folder)))
        bakground_files += list(glob.iglob('{}/*.png'.format(self.backgrounds_folder)))

        backgrounds = []
        for background_file in bakground_files:
            background = scipy.ndimage.imread(background_file, flatten=True)
            for i in range(self.num_background_variations):
                scale = self.rnd.rand() + 0.5
                new_size = tuple(np.int32(np.array(background.shape[:2]) * scale))
                scaled_background = scipy.misc.imresize(background, new_size, interp='bilinear')
                backgrounds.append(normalize_pixels(scaled_background, invert=True))

        return backgrounds

    def rotate_image(self, image, rotation_sigma=15):
        angle = 0
        if rotation_sigma > 0 and self.rnd.rand() > 0.1:
            angle = np.random.randn() * rotation_sigma
            if abs(angle) > 0.1:
                image = scipy.ndimage.interpolation.rotate(image, angle=angle, order=1, mode='constant', cval=0)
                image[image < 0.05] = 0
        return image, angle

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

    def add_background(self, image):

        if self.rnd.rand() < self.background_probability and len(self.backgrounds) > 0:
            background = fast_choice(self.rnd, self.backgrounds)

            y = self.rnd.randint(max(1, background.shape[0] - image.shape[0]))
            x = self.rnd.randint(max(1, background.shape[1] - image.shape[1]))

            # Take a patch of the background image and apply random flips/rotations.
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

    def add_distortions(self, target_image):
        # Make a copy of the text image so that we can add distortions
        image = target_image.copy()

        # TODO: add more distortions here...
        image = self.add_noise(image)
        image = self.add_blur(image)
        image = self.add_background(image)

        return image

    # TODO: make more advanded text rendering with correct kerning, etc
    def render_string(self, string):

        text_image = Image.new('F', self.render_image_size, color=0)
        draw = ImageDraw.Draw(text_image)

        # Draw text at random position
        text_position = (np.random.randint(-self.image_padding, self.image_size[0]),
                         np.random.randint(0, self.image_size[1] + self.image_padding))
        font = self.rnd.choice(self.fonts)
        draw.text(text_position, string, 1, font=font)

        # Convert the image to an numpy array 
        text_image = np.array(text_image, dtype=np.float32)

        # Emboss text
        # http://stackoverflow.com/questions/2034037/image-embossing-in-python-with-pil-adding-depth-azimuth-etc
        ele = np.pi/2.2 # elevation in radians
        azi = np.pi/4.  # azimuth in radians
        dep = 10.       # depth (0-100)
        grad = np.gradient(text_image)
        grad_x, grad_y = grad
        gd = np.cos(ele)
        dx = gd*np.cos(azi)
        dy = gd**np.sin(azi)
        dz = np.sin(ele)
        grad_x = grad_x*dep/100
        grad_y = grad_y*dep/100
        leng = np.sqrt(grad_x**2 + grad_y**2 + 1.)
        uni_x = grad_x/leng
        uni_y = grad_y/leng
        uni_z = 1./leng
        a2 = 255*(dx*uni_x + dy*uni_y + dz*uni_z)
        a2 = a2.clip(0,255)
        text_image = a2

        # Add some random rotation
        text_image, angle = self.rotate_image(text_image)

        # Crop the image to the desired size
        target_image = text_image[self.image_padding:self.image_padding + self.image_size[0],
                                  self.image_padding:self.image_padding + self.image_size[1]]

        # Add random distortions
        image = self.add_distortions(target_image)

        # Normalizes the pixel values between 0 - 1.0.
        image = normalize_pixels(image, invert=False)

        # Rescale the target image to the target size if needed
        if self.image_size != self.target_size:
            target_image = np.float32(scipy.misc.imresize(target_image, self.target_size, interp='bilinear'))

        # Binarize the text image
        mask = target_image > 0.25
        target_image[mask] = 1
        target_image[np.logical_not(mask)] = 0

        return image, target_image


class StringImageBatchGenerator:
    """
    Generates batches of images containing random text.
    """

    def __init__(self,
                 string_generator,
                 string_renderer,
                 batch_size=32,
                 random_state=None):

        self.string_generator = string_generator
        self.string_renderer = string_renderer
        self.batch_size = batch_size
        self.rnd = random_state if random_state is not None else RandomState()

    def get_batch(self, batch_size):
        """
        Returns one batch of `batch_size` images and targets.
        """

        images = []
        targets = []

        for i in range(batch_size):
            string = self.string_generator.get_string()
            image, target_mask = self.string_renderer.render_string(string)

            targets.append(target_mask)
            images.append(image)

        images = np.expand_dims(images, axis=1)
        targets = np.expand_dims(targets, axis=1)

        return images, targets

    def generate(self, num_batches=None, raise_exceptions=False):
        """
        Generates `num_batches` images with the corresponding training targets.

        Args:
            num_batches: int or None
                Number of batches to generate.
                If None the generator will just keep running.
            raise_exceptions: bool
                Should exceptions be caught to make sure training does not fail.

        Yields:
            images: array
                Shape: (batch_size, image_height, image_width)
            targets: array
                Shape: (batch_size, target_height, target_width)
        """

        batch_count = 0

        while num_batches is None or batch_count < num_batches:
            try:
                images, targets = self.get_batch(self.batch_size)

                # Reload the fonts with different sizes at regular intervals
                batch_count += 1
                yield images, targets

            except Exception as ex:
                # To make sure training does not fail just because some exception, catch it here.
                # If there is an error, just generate a new batch...
                print("Data generator exception", ex, file=sys.stderr)
                if raise_exceptions:
                    raise
