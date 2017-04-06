#include "SDPArduino.h"
#include <Wire.h>


void setup(){
  SDPsetup();
  Wire.begin();
  
  Serial.begin(115200);
  
  pinMode(3, OUTPUT);
  pinMode(A3, INPUT);
  
}

/* Sonar */
#define SPEED_OF_SOUND_MM_MICROSECOND 0.3432 // mm / microsecond
#define MAX_SONAR_TRIG_FREQ 20 // in ms
#define SONAR_GRABBER_MAX_DIST 200 // mm
unsigned long lastSonarSample[13] = {0};  // indexed by trigPin

// NOTE: this method takes up to ~6*maxDist microseconds = ~0.006*maxDist ms
// all in mm
int sonarDistance(int trigPin, int echoPin, int maxDist) {
  
  // if last trig sent was less than MAX_SONAR_TRIG_FREQ ago
  if (millis() - lastSonarSample[trigPin] < MAX_SONAR_TRIG_FREQ)
    return -1;
  
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

void loop()
{
  
  int d = sonarDistance(3, A3, 200);
  Serial.print((d > 42 && d < 100) ? "BALL  " : "____  ");
  Serial.println(d);

  delay(200);  
}
