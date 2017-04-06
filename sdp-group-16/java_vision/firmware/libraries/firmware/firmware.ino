#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>
#include <Arduino.h>
#include <I2CPort.h>

//Kickers in front
#define FRONT 3
#define RIGHT 0
#define BACK 1
#define LEFT 2

#define KICKERS 5
#define GRABBERS 4

#define KICKERDELAY 10

boolean requestStopKick = 0;
boolean kickerStatus = 0;

int zeroPosition;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6
#define PRINT_DELAY 200

// Initial motor position is 0.
int positions[ROTARY_COUNT] = {0};

int run = 0;

SerialCommand sCmd;

void loop(){
  sCmd.readSerial();
}


void dontMove(){
  motorControl(FRONT, 0);
  motorControl(BACK, 0);
  motorControl(LEFT, 0);
  motorControl(RIGHT, 0);
}

void spinmotor(){
  int motor = atoi(sCmd.next());
  int power = atoi(sCmd.next());
  motorForward(motor, power);
}

void motorControl(int motor, int power){
  if (power == 0){
    motorStop(motor);
  } else if (power > 0){
    motorForward(motor, power);
  } else {
    motorBackward(motor, -power);
  }
}

void rationalMotors(){
  int front = atoi(sCmd.next());
  int back  = atoi(sCmd.next());
  int left  = atoi(sCmd.next());
  int right = atoi(sCmd.next());
  motorControl(FRONT, front);
  motorControl(BACK, back);
  motorControl(LEFT, left);
  motorControl(RIGHT, right);
}

void pingMethod(){
  Serial.println("pang");
}

void grabberStatus() {
  Serial.println("hello i am the tacho counts for the NXT motor.");
}

void kicker(){
  int type = atoi(sCmd.next());
  if (type == 1){
      motorForward(KICKERS, 255);
  } else {
      motorStop(KICKERS);
  }
}

void completeHalt(){
  motorAllStop();
  motorControl(FRONT, 0);
  motorControl(BACK, 0);
  motorControl(LEFT, 0);
  motorControl(RIGHT, 0);
}

void grabber(){
  int type = atoi(sCmd.next());
  if(type == 0){
    motorStop(GRABBERS);
  } else if (type == 1){
    motorBackward(GRABBERS, 60);
  } else {
    motorForward(GRABBERS, 35);
  }
}

void setup(){
  Wire.begin();
  sCmd.addCommand("f", dontMove); 
  sCmd.addCommand("h", completeHalt); 
  sCmd.addCommand("motor", spinmotor); 
  sCmd.addCommand("r", rationalMotors);
  sCmd.addCommand("ping", pingMethod); 
  sCmd.addCommand("kick", kicker);
  sCmd.addCommand("grab", grabber);
  sCmd.addCommand("grabberStatus", grabberStatus);

  SDPsetup();
  helloWorld();
}

void printMotorPositions() {
  Serial.print("Motor positions: ");
  for (int i = 0; i < ROTARY_COUNT; i++) {
    Serial.print(positions[i]);
    Serial.print(' ');
  }
  Serial.println();
  delay(PRINT_DELAY);  // Delay to avoid flooding serial out
}


