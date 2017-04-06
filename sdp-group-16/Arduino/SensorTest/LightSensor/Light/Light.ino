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

#include <Wire.h>
int i = 0;

#define LIGHT_SENSOR_SLAVE_ADDRESS 0x39
#define COUNT 1
#define PRINT_DELAY 200
#define ADC0_CODE 0x43
#define ADC1_CODE 0x83

// Initial motor position is 0.
int8_t adc0;
int8_t adc1;

void setup() {
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
  digitalWrite(8, HIGH);  // Radio on
  Serial.begin(115200);  // Serial at given baudrate
  Wire.begin();  // Master of the I2C bus
}

void loop() {
  Serial.println("---");
  delay(10000);

  updateSensorValue(ADC0_CODE);
  updateSensorValue(ADC1_CODE);
  Serial.print("Light sensor values: ");
  Serial.print(separateValue(adc0));
  Serial.print(" ");
  Serial.println(separateValue(adc1));
  
}

void updateSensorValue(int sensor) {
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

uint8_t separateValue(int8_t value) {
  // First bit is the valid bit
  if ((uint8_t) value >> 7 == 1) {
    // Return unsigned remaining seven bits
    return  (uint8_t) ((uint8_t) value << 1) >> 1;
  } else {
    // Error code for invalid bits
    return 0;
  }
}

