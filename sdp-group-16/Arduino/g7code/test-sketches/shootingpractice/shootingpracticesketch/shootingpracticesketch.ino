#include "SDPArduino.h"
#include <Wire.h>

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6

#define CLICKS_PER_ROTATION 24

// Initial motor position is 0.
int positions[ROTARY_COUNT] = {0};

// translates to motorForward or motorBackward based on speed sign
void motorMove(int motorNo, int speed) {
   if (speed > 0)
      motorForward(motorNo, speed);
   else if (speed < 0)
      motorBackward(motorNo, -speed); 
   else
     motorStop(motorNo);
}

void setup(){
  SDPsetup();
  Wire.begin();  // Master of the I2C bus
}

void loop() {
  
  Serial.println("Loop");
  
  MotorMove(2, 100);
 
  while (positions[0] < CLICKS_PER_ROTATION) {
    delay(100);
    Serial.print("Motor position ");
    Serial.print(positions[0]);
    Serial.print("/n");
    updateMotorPositions();
  }
  
  MotorMove(2, 0);
  positions[0] = positions[0] % CLICKS_PER_ROTATION;
  
  delay(2000);
  
}

void updateMotorPositions() {
  // Request motor position deltas from rotary slave board
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_COUNT);
  
  // Update the recorded motor positions
  for (int i = 0; i < ROTARY_COUNT; i++) {
    positions[i] += (int8_t) Wire.read();  // Must cast to signed 8-bit type
  }
}
