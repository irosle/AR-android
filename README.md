AR-android
==========

Augmented reality project.

This project is a first attempt at a simple agmented reality library that simple places overlay layout onto the camera.

So far include 
ARFragment - this handles the life span of the ARView
ARView - with handles most of the tasks.
AROverlay - this can be created with any layout res id and a lat lng. Once added to the ARView will be down when the phone is directed at it.


TO-DO

onResume still not propally implemented.
orintation change still needs improvment
still need to implement a range of getters and setters
views change shape at right side of screen, parent view needs to ignore it's parent size to fix this.
Altitude
