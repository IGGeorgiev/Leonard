#include "hardware.h"
#include "global.h"

#include <Wire.h>

void hardwareSetup() {

 //Initial set up for arduino connected to the power board.
 pinMode(2,INPUT);
 pinMode(3,OUTPUT);
 pinMode(4,INPUT);
 pinMode(5,OUTPUT);
 pinMode(6,OUTPUT);
 pinMode(7,INPUT);
 pinMode(8,OUTPUT);
 pinMode(9,OUTPUT);
 pinMode(10,INPUT);
 pinMode(11,INPUT);
 pinMode(12,INPUT);
 pinMode(13,INPUT);
 pinMode(A0,INPUT);
 pinMode(A1,INPUT);
 pinMode(A2,INPUT);
 pinMode(A3,INPUT);
 digitalWrite(8,HIGH); //Pin 8 must be high to turn the radio on!
 Serial.begin(115200); // Serial rate the radio is configured to.
 Wire.begin(); //Makes arduino master of the I2C line.
 
 // setup digital I/O pins
 pinMode(SONAR_GRABBER_TRIG, OUTPUT);
 pinMode(SONAR_GRABBER_ECHO, INPUT);
 pinMode(REED_SWITCH_GRABBER, INPUT);
 
 greenMotorAllStop();
 resetMotorPositions();
}

/* MOTORS */

long int positions[ROTARY_COUNT] = {0};

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
 print("Motor positions: ");
 int i ;
 for ( i = 0; i < ROTARY_COUNT; i++) {
   print(positions[i]);
   print(" ");
 }
 println("");
}

void greenMotorAllStop() {
 greenMotorMove(1, 0, MOTOR_FLOAT);
 greenMotorMove(2, 0, MOTOR_FLOAT);
 greenMotorMove(3, 0, MOTOR_FLOAT);
 greenMotorMove(4, 0, MOTOR_FLOAT);

 greenMotorMove(5, 0, MOTOR_FLOAT);
 greenMotorMove(6, 0, MOTOR_FLOAT);
 greenMotorMove(7, 0, MOTOR_FLOAT);
 greenMotorMove(8, 0, MOTOR_FLOAT);
}

// motorNo in range [1,8]
// motorPower in range [0,100]
// dir: 0 float, 1 fwd, 2 bckw, 3 brake
void greenMotorMove(int motorNum, int motorPower, enum MOTOR_DIR dir) {
 
 if (motorNum > 8 || motorNum < 1)
   return;
 
 // 2 motor boards in use: 1-4 have seperate address to 5-8
 int address;
 if (motorNum <= 4)
   address = GREEN_ADDRESS_1_4;
 else {
   address = GREEN_ADDRESS_5_8;
   motorNum -= 4;
 }

 if (motorPower < 0) {
   motorPower = abs(motorPower); 
   if (dir == MOTOR_FWD) dir = MOTOR_BWD;
   else if (dir == MOTOR_BWD) dir = MOTOR_FWD;
 }
 if (motorPower > 100)
   motorPower = 100;
 
 Wire.beginTransmission(address);
 Wire.write((motorNum*2)-1);
 Wire.write(dir);
 Wire.write(int(motorPower*2.55));
 Wire.endTransmission();
}

/* SONAR */

#define SPEED_OF_SOUND_MM_MICROSECOND 0.3432 // mm / microsecond
#define MAX_SONAR_TRIG_FREQ 30 // in ms
#define SONAR_GRABBER_MAX_DIST 200 // mm
unsigned long lastSonarSample[13] = {0};  // indexed by trigPin

// NOTE: this method takes up to ~6*maxDist microseconds = ~0.006*maxDist ms
// all in mm
int sonarDistance(int trigPin, int echoPin, int maxDist) {
 
 // if last trig sent was less than MAX_SONAR_TRIG_FREQ ago
 if (millis() - lastSonarSample[trigPin] < MAX_SONAR_TRIG_FREQ)
   return -2;
 
 long duration, distance;
 int timeout = (2 * maxDist) / SPEED_OF_SOUND_MM_MICROSECOND;
 
 digitalWrite(trigPin, LOW);
 delayMicroseconds(2);
 digitalWrite(trigPin, HIGH);
 delayMicroseconds(10);
 digitalWrite(trigPin, LOW);
 
 // in microseconds
 duration = pulseIn(echoPin, HIGH, timeout);
 // in mm
 distance = (duration/2) * SPEED_OF_SOUND_MM_MICROSECOND;
 
 lastSonarSample[trigPin] = millis();
 
 // out of range
 if (distance >= maxDist || distance <= 0)
   return -1;
 // in range
 else
   return distance;
}

// precondition: grabber up/open
bool ballInReach(int *counter, int countsRequired) {

 int grabberSonarDist = sonarDistance(SONAR_GRABBER_TRIG, SONAR_GRABBER_ECHO, SONAR_GRABBER_MAX_DIST);
 bool inRange = (grabberSonarDist > 42 && grabberSonarDist < 100);
 bool ignored = (grabberSonarDist == -2);
 
 if (inRange) {
   (*counter)++;
   // Serial.print("Ball seen ");
   // Serial.print(grabberSonarDist);
   // Serial.print(" ctr ");
   // Serial.println(*counter);
 } else if (!inRange && !ignored) {
   *counter = 0;
 }

 return (*counter >= countsRequired);
}

/* GRABBER */

// precondition: grabber must have been "pulled" shut before call
bool hasBall() {

 return !digitalRead(REED_SWITCH_GRABBER);

}

