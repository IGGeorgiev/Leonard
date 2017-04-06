#ifndef WORLDMODEL_H
#define WORLDMODEL_H

#include "Arduino.h"

/* Point (integer) struct */

struct Point {
  int x;
  int y;
};

struct Position {
  Point coor;
  int head;
};

struct Vector {
  double x;
  double y;
};

void updateWorldModel(byte params[]);

class WorldModel {
public:
  Position rob;
  unsigned long sentTimestamp;  // in "strategy/vision system" time
  unsigned long receivedTimestamp;  // in "arduino" time 
};

extern WorldModel worldModel;

#endif
