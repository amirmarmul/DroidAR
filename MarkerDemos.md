# The MarkerDemos project #

This small demo demonstrates how an example application using marker detection could look like.

The expected behavior is as follows:
  * A button appears.
  * When clicked the camera will start and a live preview is seen.
  * If a marker with the ID 0 is detected  a white hexagon will be placed on top of the marker and 3 colored objects will be rotating somewhere around the center of the marker.

This is a very basic example, but the source should explain the general idea of how to use the ARMarkers library in combination with DroidAR.

### How to run the project ###

  * First get both the DroidAR and ARMarker projects and set them up as [described](http://code.google.com/p/droidar/wiki/Markerdetection).
  * Download the MarkerDemos project from [here](http://code.google.com/p/droidar/source/browse/#svn%2Ftrunk%2FMarkerDemos) and set both the DroidAR and ARMarker projects as libraries for the MarkerDemos project.
  * Run the project on an android device with a camera.
  * Either generate the 0-marker using the MarkerGenerator, which is a java applet, or download the marker from the [Downloads section](http://code.google.com/p/droidar/downloads/list).