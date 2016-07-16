FROM tensorflow/tensorflow:nightly

ENV KERAS_BACKEND "tensorflow"

RUN pip install keras pillow
