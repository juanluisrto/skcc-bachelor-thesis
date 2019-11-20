import numpy as np
import scipy
import scipy.misc
from PIL import Image, ExifTags

EXIF_TAGS = {name: id for id, name in ExifTags.TAGS.items()}
EXIF_ORIENTATION_TO_ROT = {3: 2, 6: 3, 8: 1}


def resize_image_to_square(image, square_side):
    """
    Resize an image to a square using bilinear interpolation.
    The pixel values will be in the range 0-1.0 (float32).

    Args:
        image: 2D numpy array
            Shape: (H, W)
    Returns:
        pixels: 2D numpy array, float32
            Shape: (square_side, square_side)
            Pixel value range: 0-1.0
    """
    resized_image = scipy.misc.imresize(image, (square_side, square_side), interp='bilinear')
    resized_image = np.float32(resized_image)
    resized_image /= 255.0
    return resized_image


def normalize_pixels(pixels, invert=True):
    """
    Prepares an image for input to a NN model.
    First normalize the image by removing the color channel
    and make the pixel values in the range 0-1.0.
    Where 1.0 is black (text) and 0 white (background).

    Args:
        pixels: 3D numpy array
            Shape: (H, W, C) or (H, W)
        invert: bool
            Should the pixels be inverted? pixel = 1 - pixel.
    Returns:
        pixels: 2D numpt array, float32
            Shape: (H, W)
    """

    assert pixels.ndim == 3 or pixels.ndim == 2

    if pixels.ndim == 3 or pixels.ndim == 4:
        pixels = pixels.sum(axis=2)

    pixels = np.float32(pixels)

    # Normalize pixel range to 0-1.0
    pixels -= pixels.min()
    pixel_max = pixels.max()
    if pixel_max != 0:
        pixels /= pixel_max

    if invert:
        pixels = 1.0 - pixels

    return pixels


def normalize_images(images, invert=True):
    """
    Applies normalize_pixels() over an iterator of images.
    """

    norm_images = [normalize_pixels(pixels, invert) for pixels in images]
    return norm_images


def load_image(file_path):
    """
    Loads an image and orients it if there are Exif meta data.

    Args:
        file_path: str

    Returns:
        image_array: ndarray
            (H, W, C) or (H, W)
    """

    image = Image.open(file_path)

    rot = 0

    if hasattr(image, '_getexif') and image._getexif():
        image_exif = dict(image._getexif().items())
        orientation_id = EXIF_TAGS['Orientation']
        if orientation_id in image_exif:
            orientation = image_exif[orientation_id]
            if orientation in EXIF_ORIENTATION_TO_ROT:
                rot = EXIF_ORIENTATION_TO_ROT[orientation]

    image = image.convert('RGB')
    image_array = np.array(image)

    if rot != 0:
        image_array = np.rot90(image_array, k=rot)

    return image_array
