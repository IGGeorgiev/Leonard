#!/bin/bash

# You need a directory structure of ./SDP/setup_openCV.sh ./openCV/opencv-3.2.0 ./openCV/opencv-contrib-master

cd ../openCV/opencv-3.2.0/
mkdir build
cd build

cmake -D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib-master/modules -D BUILD_PERF_TESTS=OFF -D BUILD_EXAMPLES=OFF -D WITHLAPACK=OFF -D BUILD_TESTS=OFF -D CMAKE_INSTALL_PREFIX=/afs/inf.ed.ac.uk/user/s14/s1410984/.local ..
