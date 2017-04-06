#include <SoftwareSerial.h>
#include <Wire.h>

#include "SDPArduino.h"


void setup() {
  
  SDPsetup();
  Serial.begin(115200) ;
  
  Serial.println(F("READY")) ;
}

void loop() {
  updateMotorPositions();
  printMotorPositions();
  delay(200);
}

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6

long int positions[ROTARY_COUNT] = {0};

void motorMove(int motorNo, int speed) {
  if (speed > 0)
    motorForward(motorNo, speed);
  else if (speed < 0)
    motorBackward(motorNo, -speed);
  else
    motorStop(motorNo);
}

void updateMotorPositions() {
  // Request motor position deltas from rotary slave board
  
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_COUNT);
  
  // Update the recorded motor positions
  int i ;
  
  for (i = 0; i < ROTARY_COUNT; i++) {
    positions[i] += (int8_t) Wire.read();  // Must cast to signed 8-bit type
  }
}

void resetMotorPositions() {
  updateMotorPositions() ;
  memset(positions , 0 , sizeof(positions)) ;
}

void printMotorPositions() {
  Serial.print("Motor positions: ");
  int i ;
  for ( i = 0; i < ROTARY_COUNT; i++) {
    Serial.print(positions[i]);
    Serial.print(" ");
  }
  Serial.println("");
}
