# Getting started with marker detection #

## Requirements: ##
  * [Crystax NDK r4](http://www.crystax.net/android/ndk-r4.php) (Other versions might work on Linux, on windows this seems to be the only working NDK for this project.)
  * The DroidAR Project
  * [Cygwin](http://www.cygwin.com/) (Download the Setup [here](http://cygwin.com/setup.exe))


## Getting Started: ##
  * Get the project files from http://droidar.googlecode.com/svn/trunk/ARMarker

  * Get the OpenCV project source code from https://github.com/billmccord/OpenCV-Android.

  * Paste source folders _cv_ and _cxcore_ into the _jni_ folder of the ARMarker project. (The other files are not needed. If you want to use other features from OpenCV you might need to edit the _Android.mk_ file in the jni and add other files too.)

  * Load the external tool configuration "Build AR.launch" into eclipse and edit the environment variables to match your cygwin root folder and your NDK root folder. The cygwin path is a windows path and the ndkdir is a unix path. Keep this in mind when using spaces.

  * Compile the library using the external tool configuration. After the execution there should be a libs folder in your project with a _libopencv.so_ file. This can take longer than 5 minutes, so you might want to start doing something else while the code is compiling.

  * Set the DroidAR project as a library project for the ARMarker project.

  * Create your own project which has both projects as library projects. For an example of such a project see the MarkerDemos project in the repository.