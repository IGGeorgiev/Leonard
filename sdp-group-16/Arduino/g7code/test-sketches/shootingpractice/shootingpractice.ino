

#include "SDPArduino.h"
//#include <Serial.h>
#include <SoftwareSerial.h>
#include <stdlib.h>
#include <Wire.h>
#include "SerialCommand.h"
#include "RotaryLib.h"

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6

void updateMotorPositions() ;

long int positions[ROTARY_COUNT] ={
  0} 
;

void setup() {

  SDPsetup();  
  Serial.begin(115200) ;
  Wire.begin();
  motorAllStop();
  updateMotorPositions() ;
  memset(positions , 0 , sizeof(positions)) ;
}

void loop() {
  
  rotateFinger();
  
  delay(10000);
  
  return;

  // kick, incrementing winder pos by 30 deg each time
  for (int i = 0 ; i < 10 ; i++) {
    kick(-i * 45);
    delay(5000);
  }  

  delay(10000);
}

/* FINGER (it 'flicks' kicker) constants */
#define FINGER_MOTOR_IDX 0
#define FINER_ROT_ENC_IDX 0
#define FINGER_CLICK_PER_ROT 24
// when adjusting motor position, delay (ms) between adjustments
#define FINGER_SAMPLE_DELAY 100

/* WINDER (it winds elastic band) constants */
#define WINDER_MOTOR_IDX 5
#define WINDER_ROT_ENC_IDX 5
// looking square on at LHS of robot
// default (0 deg) winder pos is pointing leftward
#define WINDER_CLICK_PER_ROT 180
// tolerance when setting angle
#define WINDER_POS_TOL 10
// threshholds (in degrees) at which motor should be slowed
#define WINDER_HALF_SPEED_THRESH 20
//#define WINDER_QUART_SPEED_THRESH 6
#define WINDER_ZERO_SPEED_THRESH 4
// when adjusting motor position, delay (ms) between adjustments
#define WINDER_SAMPLE_DELAY 50

// sets winder and rotates finger
void kick(int winderPos) {

  setWinder(winderPos);

  // performs full 360 rotation of finger
  //rotateFinger();
}


/* FINGER methods */

// returns value in degrees
int fingerPos() {
  updateMotorPositions();
  return positions[FINGER_MOTOR_IDX] * (360/FINGER_CLICK_PER_ROT);
}

// brings finger position back into [0,359] range
void decrementFingerPos() {
  positions[FINGER_MOTOR_IDX] = positions[FINGER_MOTOR_IDX] % FINGER_CLICK_PER_ROT;
}

// performs a full (360 deg) turn of finger
void rotateFinger() {

  int start = fingerPos();
  int target = 360;

  motorMove(FINGER_MOTOR_IDX, 100);

  Serial.println("Finger motor full speed");

  // keep motor powered until over 360
  while (fingerPos() < target)
    delay(FINGER_SAMPLE_DELAY);

  // stop motor
  motorMove(FINGER_MOTOR_IDX, 0);

  Serial.println("Finger motor stop");

  decrementFingerPos();
}


/* WINDER methods */

// returns value in degrees
int winderPos() {
  updateMotorPositions();
  return positions[WINDER_MOTOR_IDX] * (360/WINDER_CLICK_PER_ROT);
}

// sets winder motor to a target angle
void setWinder(int targ) {
  
  Serial.print("Target ");
  Serial.println(targ);
  
  // remaining degrees to travel
  int rem = targ - winderPos();
  
  // tuning only
  bool lastDir = (rem > 0 ? 1 : -1);
  
  int remAtLastSample = rem;
  int rotSinceLastSample = -1;
  
  while (rem < -20) {

    rem = targ - winderPos();
    
//    Serial.print("setWinder(");
//    Serial.print(targ);
//    Serial.print(") ");
//    Serial.print(" pos ");
//    Serial.print(winderPos());
//    Serial.print(" rem ");
//    Serial.print(rem);
//    Serial.println("");

    motorMove(WINDER_MOTOR_IDX, -75);
    
    delay(10);
  }

  Serial.print("on brake start, pos is ");
  Serial.print(winderPos());
  Serial.println("");

  motorMove(WINDER_MOTOR_IDX, 100);
  delay(200);
  motorMove(WINDER_MOTOR_IDX, 0);
  
  rem = targ - winderPos();

  Serial.print("on brake completion, pos is ");
  Serial.print(winderPos());
  Serial.println("");

  delay(500);

  Serial.print("0.5s after brake, pos is ");
  Serial.print(winderPos());
  Serial.println("");

  return;

  // while position is out of tolerance or motor not stopped
  while (abs(rem) > WINDER_POS_TOL || rotSinceLastSample != 0) {

    // update remaining and calculate how far motor turned since last loop (sample)
    rem = targ - winderPos();
    rotSinceLastSample = rem - remAtLastSample;

    // TODO:
    int motorSpeedAbs = 50;

    // if motor needs to be slowed (or even stopped) as target is approaching
    if (abs(rem) <= WINDER_ZERO_SPEED_THRESH) {
      motorMove(WINDER_MOTOR_IDX, -100);
      delay(10);
      motorMove(WINDER_MOTOR_IDX, 0);
      Serial.print("BRAKE");
      delay(10000);
    }

    //    else if (abs(rem) <= WINDER_QUART_SPEED_THRESH) 
    //      motorSpeedAbs = 25;
    else if (abs(rem) <= WINDER_HALF_SPEED_THRESH) 
      motorSpeedAbs = 40;

    // sign motorSpeed and send to motor
    int motorSpeed = motorSpeedAbs * (rem > 0 ? 1 : -1);
    motorMove(WINDER_MOTOR_IDX, motorSpeed);

    // tuning only
    int newDir = (motorSpeed > 0 ? 1 : -1);
    if (lastDir != newDir)
      Serial.println("NEEDS TUNING - Had to reverse direction to get in tolerance");
    lastDir = newDir;

    // keep track of rem to compare next sample, to determine if motor has stopped
    remAtLastSample = rem;

    // debug info
    Serial.print("setWinder(");
    Serial.print(targ);
    Serial.print(") ");
    Serial.print(" pos ");
    Serial.print(winderPos());
    Serial.print(" rem ");
    Serial.print(rem);
    Serial.print(" rotSinceLastSample ");
    Serial.print(rotSinceLastSample);
    Serial.print(" motorSpeed ");
    Serial.print(motorSpeed);
    Serial.print("\n");

    delay(WINDER_SAMPLE_DELAY);
  }

  // ensure motor has been stopped
  motorMove(WINDER_MOTOR_IDX, 0);

  Serial.println("Winder set");
}


/* Motor control & positions */

void updateMotorPositions() {
  // Request motor position deltas from rotary slave board
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_COUNT);

  // Update the recorded motor positions
  int i ;
  for (i = 0; i < ROTARY_COUNT; i++) {
    positions[i] += (int8_t) Wire.read();  // Must cast to signed 8-bit type
  }
}


void printMotorPositions() {
  Serial.print("Motor positions: ");
  int i ;
  for ( i = 0; i < ROTARY_COUNT; i++) {
    Serial.print(positions[i]);
    Serial.print(' ');
  }
  Serial.println();
}

// translates to motorForward or motorBackward based on speed sign
void motorMove(int motorNo, int speed) {
  if (speed > 0)
    motorForward(motorNo, speed);
  else if (speed < 0)
    motorBackward(motorNo, -speed); 
  else
    motorStop(motorNo);
}



