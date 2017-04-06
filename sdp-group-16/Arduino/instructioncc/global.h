#ifndef GLOBAL_H
#define GLOBAL_H

#include "Comms.h"
#include "WorldModel.h"

/* Comms */

class Comms;

extern Comms comms;

/* Helper Functions */

double toRad(int deg);
int toDeg(double rad);
int vectorAngle(Vector u, Vector v);
Vector headToVector(int head);
Vector makeVector(double x, double y);
Point makePoint(int x, int y);
Position makePosition(int x, int y, int h);
Position makePosition(Point point, int h);
int pointToPointDistance(Point a, Point b);

int byteArrToUnsignedShort(byte arr[], int firstByteIdx);
int byteArrToSignedInt(byte arr[], int firstByteIdx);
unsigned int byteArrToUnsignedInt(byte arr[], int firstByteIdx);
unsigned long byteArrToUnsignedLong(byte arr[], int firstByteIdx);

#endif
