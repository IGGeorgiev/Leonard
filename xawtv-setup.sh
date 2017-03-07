#!/bin/sh
v4lctl bright 10%
v4lctl hue 20%
v4lctl contrast 30%
v4lctl color 40%
v4lctl setattr 'whitecrush lower' 50%
v4lctl setattr 'whitecrush upper' 60%
v4lctl setattr 'uv ratio' 70%
v4lctl setattr 'coring' 80%
v4lctl setattr 'chroma agc' off
v4lctl setattr 'color killer' on
v4lctl setattr 'comb filter' off
v4lctl setattr 'auto mute' on
v4lctl setattr 'luma decimation filter' off
v4lctl setattr 'agc crush' on
v4lctl setattr 'vcr hack' off
v4lctl setattr 'full luma range' on
