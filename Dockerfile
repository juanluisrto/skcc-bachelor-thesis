FROM tensorflow/tensorflow:1.7.1-py3

ENV KERAS_BACKEND "tensorflow"
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

COPY Pipfile ./
COPY Pipfile.lock ./

RUN pip install "pipenv==8.3.2" && pipenv install --system
RUN jupyter nbextension enable --py widgetsnbextension
