#ifndef GLOBAL_H
#define GLOBAL_H

#include "WorldModel.h"
#include "hardware.h"
#include "Comms.h"

/* General Commands */

void resetCommand();
void stopCommand();
void abortCommand();


/* World Model */

extern WorldModel worldModel;


/* Comms */

class Comms;

extern Comms comms;


/* Helper Functions */

double toRad(double deg);
double toDeg(double rad);
double vectorAngle(Vector u, Vector v);
Vector makeVector(double x, double y);
void printVector(Vector v);
Point makePoint(int x, int y);
void printPoint(Point p);
void printPosition(Position p);
Position makePosition(int x, int y, int h);
Position makePosition(Point point, int h);
int pointToPointDistance(Point a, Point b);

int byteArrToUnsignedShort(byte arr[], int firstByteIdx);
int byteArrToSignedInt(byte arr[], int firstByteIdx);
unsigned int byteArrToUnsignedInt(byte arr[], int firstByteIdx);
unsigned long byteArrToUnsignedLong(byte arr[], int firstByteIdx);


/* Print Functions */

void println(char *c);
void println(const char *c);
void println(__FlashStringHelper *c);
void println(int i);
void println(long l);
void println(unsigned long l);
void println(double d);
void println(float f);
void println(bool b);

void print(char *c);
void print(const char *c);
void print(__FlashStringHelper *c);
void print(int i);
void print(long l);
void print(unsigned long l);
void print(double d);
void print(float f);
void print(bool b);

#endif
