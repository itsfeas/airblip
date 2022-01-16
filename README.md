## Introduction
In our modern world, people are more connected than ever. The value of information continues to grow, yet we are limited by hardware and our environment. Not all devices have antennae, and not always are radio-wave communications the best for the job.

For this, we created Airblip, a system allowing for data transfer between different devices through sound.

## What it does
Our current implementation of Airblip allows for the transfer of bytes between Android phones and PCs. As a proof-of-concept, Airblip shows that it is in fact possible to transfer data through sound alone. 
The pair of apps allows the exchange of string messages from one device to another through sound.

## How we built it
We built it by implementing a python app that receives data to take advantage of powerful external microphones. The sender or transmitter app was built for android devices using java and android studio.

## Challenges we ran into
Because we were dealing with analog signals, we ran into many issues with quality. Noise created considerable problems, problems which were eventually solved with the use of band pass filters, convolution, and Fourier transfers. 


## Accomplishments that we're proud of
Our main accomplishment is nailing down the algorithms and strategy of encoding, transmitting, receiving and decoding data through sound. We are also proud to have built a mobile app with little app development experience. 

## What we learned
We had many lessons in new tools such as Android Studio. We also learned to quickly pivot between different technologies to try different solutions.


## What's next for Airblip
The next step is a move to a phone exclusive app for both sending and receiving. We would also like to push the transmission frequencies to higher in order to cloak it from human ears.
Copy and add/edit whatever you want
