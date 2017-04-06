#include "SDPArduino.h"
#include <Wire.h>
int i = 0;

void setup() {
  SDPsetup();

  serial_test();
}

void serial_test() {
  helloWorld();
  while (!Serial.find("H"));
  Serial.println("Happy Birthday!");
}

void loop(){
}
