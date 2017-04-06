#include "hardware.h"
#include "global.h"

#include "SDPArduino.h"
#include <Wire.h>

#define ADC0_LIGHT_THRESHOLD 66
#define ADC1_LIGHT_THRESHOLD 24

int8_t adc0;
int8_t adc1;

#include "compass.h"

#define ZERO_ROOM_1 119.4875 // Zero point in pitch room 1
#define ZERO_ROOM_2 121.8325 // Zero point in pitch room 2
#define Task_t 10          // Task Time in milli seconds
#define NO_SAMPLES 8 // Number of compass measurements

int dt=0;
unsigned long t;
int pitch_room = 1;
int compass_samples[NO_SAMPLES] = {};

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

void motorMove(int motorNum, int motorPower, enum MOTOR_DIR dir) {
   if (motorNum > 5 || motorNum < 0){
      Serial.println("Wrong motor number");
      return;
   }

   if (motorPower < 0) {
      motorPower = abs(motorPower);
      if (dir == MOTOR_FWD) dir = MOTOR_BWD;
      else if (dir == MOTOR_BWD) dir = MOTOR_FWD;

   }
   if (motorPower > 100)
      motorPower = 100;

   if (dir == MOTOR_FWD) motorForward(motorNum, motorPower);
   else if (dir == MOTOR_BWD) motorBackward(motorNum, motorPower);
   else if (dir == MOTOR_FLOAT) motorStop(motorNum);

}

/* COMPASS */

void setupCompass(int mode, int pitch) {
  compass_x_offset = 122.17;
  compass_y_offset = 230.08;
  compass_z_offset = 389.85;
  compass_x_gainError = 1.12;
  compass_y_gainError = 1.13;
  compass_z_gainError = 1.03;
  
  compass_init(2);
  compass_debug = mode;
  compass_offset_calibration(3);

  pitch_room = pitch;
}

// Remove maximum and minimum from subsequent compass readings to exclude outliers
int compassRemoveMaxMin() {
  int minimum = 361;
  int maximum = -1;
  int maxIndex = -1;
  int minIndex = -1;
  for (int i = 0; i < NO_SAMPLES; i++) {
    if (compass_samples[i] < minimum) {
      minimum = compass_samples[i];
      minIndex = i;
    }
    if (compass_samples[i] > maximum) {
      maximum = compass_samples[i];
      maxIndex = i;
    }
  }
  int average = 0;
  for (int i = 0; i < NO_SAMPLES; i++) {
    if (i != maxIndex || i != minIndex) {
      average = average + compass_samples[i];
    }
  }
  return (int) average / (NO_SAMPLES - 2);
}

int getHeading() {

  t = millis();
  float load;
  
  compass_scaled_reading();
  
  if (compass_debug == 1) {
    Serial.print(F("x = "));
    Serial.println(compass_x_scaled);
    Serial.print(F("y = "));
    Serial.println(compass_y_scaled);
    Serial.print(F("z = "));
    Serial.println(compass_z_scaled);
  }

  // compass_heading();
  for (int i = 0; i < NO_SAMPLES; i++) {
    compass_heading();
    compass_samples[i] = bearing;
  }

  bearing = compassRemoveMaxMin();


  if (compass_debug == 1) {
    Serial.print (F("Heading angle = "));
    Serial.print (bearing);
    Serial.println(F(" Degree"));
  
    dt = millis()-t;
    load = (float) dt/Task_t;
    Serial.print (F("Load on processor = "));
    Serial.print(load);
    Serial.println(F("%"));
    
    delay(3000); 
  }

  int angle = -1;
  if (pitch_room == 1) {
    angle = (bearing - ZERO_ROOM_1);
  } else if (pitch_room == 2) {
    angle = (bearing - ZERO_ROOM_2);
  }

  angle = (angle < 0) ? angle + 360 : angle;
  angle = 359 - angle;

  // Debug statement, delete after calibration
  // Serial.print(F("Angle: "));
  // Serial.println(angle);

  return angle;
}


/* GRABBER */


void updateLightSensorValue(int sensor) {
  Wire.beginTransmission(LIGHT_SENSOR_SLAVE_ADDRESS);
  Wire.write(sensor); // Tell light sensor which channel to read
  Wire.endTransmission();
  delay(50);
  Wire.requestFrom(LIGHT_SENSOR_SLAVE_ADDRESS, 1);
  if (sensor == ADC0_CODE) {
    adc0 = (int8_t) Wire.read();
  } else {
    adc1 = (int8_t) Wire.read();
  }
}

uint8_t separateLightSensorValue(int8_t value) {
  // First bit is the valid bit
  if ((uint8_t) value >> 7 == 1) {
    // Return unsigned remaining seven bits
    return  (uint8_t) ((uint8_t) value << 1) >> 1;
  } else {
    // Error code for invalid bits
    return 0;
  }
}

bool hasBall() {  
  updateLightSensorValue(ADC0_CODE);
  updateLightSensorValue(ADC1_CODE);
  if (separateLightSensorValue(adc0) > ADC0_LIGHT_THRESHOLD && separateLightSensorValue(adc1) > ADC1_LIGHT_THRESHOLD) {
    return true;
  }
  return false;
}

/* SOLENOID */

void kick (){
  pinMode(A0,OUTPUT);
  digitalWrite(A0,HIGH);
 }

void turnOffSolenoid(){
  pinMode(A0,OUTPUT);
  digitalWrite(A0,LOW);
 }

 /* RESET */
 
void reset() {
  asm volatile ("  jmp 0");
}
