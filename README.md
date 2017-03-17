# Slagkryssaren Credit card #


This repo contains the Slagkryssaren Credit Card Reader.
The purpose is to use Deep Learning to create a Credit Card reader which works on standard smart-phones.

## Getting started

- Start the docker image with `docker-compose up`
- In the log output, find a url similar to: _http://localhost:8888/?token=a765662297cd764843678564373b99de23e7687137_
- Open the link in a browser to open the Jupyter web interface

In the Jupyter interface there are two notebooks:

- _text-detector.ipynb_ contains the code that ties everything together, and is were you will try and build new models.
- _binary classification.ipynb_ contains a simple example on how to classify dots as red or blue based on their position.

### Jupyter - basic usage

Each code block is executed by marking it and pressing SHIFT + ENTER
