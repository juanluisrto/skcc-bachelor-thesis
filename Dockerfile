FROM tensorflow/tensorflow:nightly-py3

ENV KERAS_BACKEND "tensorflow"

RUN pip3 install keras pillow ipywidgets
RUN jupyter nbextension enable --py widgetsnbextension
RUN pip3 install h5py
