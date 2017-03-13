#!/bin/sh
# Get local path to repo
dir='pwd'

# Setup video feed driver
$("./xawtv-setup.sh")

# build and run project
ant

export LD_LIBRARY_PATH="$(pwd)/libs/"
# Load pitch configuration
if [ "$1" -eq "2" ]; then
    java -cp ./out/production/SDP:libs/jssc.jar:libs/junit-4.12.jar:libs/opencv-320.jar:libs/v4l4j.jar strategy.Strategy 2
else
    java -cp ./out/production/SDP:libs/jssc.jar:libs/junit-4.12.jar:libs/opencv-320.jar:libs/v4l4j.jar strategy.Strategy 1
fi
