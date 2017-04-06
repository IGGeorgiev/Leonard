# Vision System Guide

## Outline

So the Vision System is a pipeline of several stages:

* Detection of the image
  * Provides a BufferedImage

* Spot Analysis
  * Detects all the spots of all the different colors.
  * Passes on a HashMap of colors to ArrayLists of spots
