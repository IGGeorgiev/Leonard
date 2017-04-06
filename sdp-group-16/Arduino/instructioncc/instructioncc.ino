#include <SoftwareSerial.h>
#include <Wire.h>
#include <stdlib.h>
#include <math.h>

#include "hardware.h"
#include "Comms.h"
#include "Instruction.h"
#include "GrabInstruction.h"
#include "WorldModel.h"
#include "MemoryFree.h"
#include "SDPArduino.h"

unsigned long memPrintTimer;
Comms comms;

void setup() {

  SDPsetup();

  // Compass setup
  // setupCompass(2, 1);
  
  // set instructions array to NULL pointers
  deleteAllInstructions();

  memPrintTimer = millis();
  
  // Solenoid setup
  pinMode(A0,OUTPUT);
  
  // let PC know we've started up and to send commands with ID starting at 1
  comms.sendArdReset();
  
  Serial.println(F("READY")) ;
}

void loop() {
  
  // calls progress method on instruction at index 0
  progressInstruction();
  
  comms.readSerial();
  
  // check memory level every second
  if (millis() - memPrintTimer > 1000) {
    int memAvail = freeMemory();
    if (memAvail < 512) {
      Serial.println(F("Low Memory "));
      Serial.println(freeMemory());
    } 
   memPrintTimer = millis();
  }
  
}


/* Helper methods */

// converts single byte to int
int byteArrToUnsignedShort(byte arr[], int firstByteIdx) {
  return arr[firstByteIdx];
}

// given an array of bytes (most significant first), takes 2 bytes to 
// form an unsigned integer
unsigned int byteArrToUnsignedInt(byte arr[], int firstByteIdx) {
  return arr[firstByteIdx] << 8 | arr[firstByteIdx+1];
}

// given an array of bytes (most significant first), takes 4 bytes to 
// form an unsigned integer
unsigned long byteArrToUnsignedLong(byte arr[], int fbi) {
  return (unsigned long)arr[fbi] << 24 | (unsigned long)arr[fbi+1] << 16 | (unsigned long)arr[fbi+2] << 8 | (unsigned long)arr[fbi+3];
}

// given an array of bytes (most significant first), takes 2 bytes to 
// form a signed integer
int byteArrToSignedInt(byte arr[], int firstByteIdx) {
  // cast to signed char so that int will interpret it in 2s compliment
  return static_cast<signed char>(arr[firstByteIdx]) << 8 | arr[firstByteIdx+1];
}

// Vectors describe directions (headings) 
Vector makeVector(double x, double y) {
  Vector v;
  v.x = x;
  v.y = y;
  return v; 
}

// Points describe coordinates
Point makePoint(int x, int y) {
  Point p;
  p.x = x;
  p.y = y;
  return p; 
}

Position makePosition(int x, int y, int h) {
  Position p;
  p.coor = makePoint(x, y);
  p.head = h;
  return p; 
}


Position makePosition(Point point, int h) {
  Position p;
  p.coor = point;
  p.head = h;
  return p; 
}

double toRad(int deg) {
  return ((double) deg/180.0)*M_PI;
}

int toDeg(double rad) {
  return (int) (rad * 180/M_PI); 
}

// Returns angle difference in degrees between two vectors
int vectorAngle(Vector u, Vector v) {
  double angle = -(atan2(v.y, v.x) - atan2(u.y, u.x));
  return toDeg(angle);
}

// converts a heading in degrees to a vector
Vector headToVector(int head) {
  Vector v = makeVector(cos(toRad(head)), -sin(toRad(head)));
  return v;
}

// distance between two ppoints
int pointToPointDistance(Point a, Point b) {
  return sqrt(pow(a.x-b.x, 2) + pow(a.y-b.y,2));
}