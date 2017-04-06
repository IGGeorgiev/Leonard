/*
 * Master board sample code to be used in conjuction with the rotary encoder
 * slave board and sample code.
 * This sketch will keep track of the rotary encoder positions relative to
 * the origin. The origin is set to the position held when the master board
 * is powered.
 *
 * Rotary encoder positions are printed to serial every 200ms where the
 * first result is that of the encoder attached to the port at 11 o'clock
 * on the slave board (with the I2C ports at at 12 o'clock). The following
 * results are in counter-clockwise sequence.
 *
 * Author: Chris Seaton, SDP Group 7 2015
 */

#include "SDPArduino.h"
#include <Wire.h>
int i = 0;

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6
#define PRINT_DELAY 200

// Initial motor position is 0.
int positions[ROTARY_COUNT] = {0};

void setup() {
  SDPsetup();
  digitalWrite(8, HIGH);  // Radio on
  Serial.begin(115200);  // Serial at given baudrate
  Wire.begin();  // Master of the I2C bus
}

void loop() {
  updateMotorPositions();
  printMotorPositions();
  delay(100);
}

void updateMotorPositions() {
  // Request motor position deltas from rotary slave board
  Wire.requestFrom(ROTARY_SLAVE_ADDRESS, ROTARY_COUNT);
  
  // Update the recorded motor positions
  int i;
  for (i = 0; i < ROTARY_COUNT; i++) {
    positions[i] += (int8_t) Wire.read();  // Must cast to signed 8-bit type
    Serial.println(positions[i]);
  }
}

void printMotorPositions() {
  Serial.print("Motor positions: ");
  int i;
  for (i = 0; i < ROTARY_COUNT; i++) {
    Serial.print(positions[i]);
    Serial.print(' ');
  }
  Serial.println();
  delay(PRINT_DELAY);  // Delay to avoid flooding serial out
}
