#include "FingerInstruction.h"

//#define DEBUG_PRINT_FINGER

void FingerInstruction::halt(void) {
#ifdef DEBUG_PRINT_FINGER
  Serial.println(F("FI stop"));
#endif
  greenMotorMove(GREEN_FINGER_IDX, 0, MOTOR_FLOAT);
}

bool FingerInstruction::brakeFinger(void) {
  
  updateMotorPositions();
  
  int clicks = positions[ROT_FINGER_IDX];
  
  // first call to brake()
  if (!this->braking) {
    greenMotorMove(GREEN_FINGER_IDX, 100, MOTOR_BRAKE);
    this->braking = true;
    this->brakeLastClicksTime = millis();
    this->brakeLastClicks = clicks;
#ifdef DEBUG_PRINT_FINGER
    Serial.println(F("Begin brake"));
#endif
    return false;
  } 
  
  // new clicks arrived, reset timer
  if (abs(clicks) > abs(this->brakeLastClicks)) {
    this->brakeLastClicks = clicks;
    this->brakeLastClicksTime = millis();
#ifdef DEBUG_PRINT_FINGER
    Serial.println(F("new clicks"));
#endif
    return false;
  }
  
  // if no clicks in last x ms
  if (millis() - this->brakeLastClicksTime > 500) {
    // float motors
    this->halt();
#ifdef DEBUG_PRINT_FINGER
    Serial.println(F("done"));
#endif
    return true; 
  }
  
  return false;
}

bool FingerInstruction::postBrakeProgress() {
  return true;
}

// how far ahead of a is b
int distanceAhead(int a, int b) {
  return positiveModulo((b - a), 24);
}

int positiveModulo(int n, int m) {
  return (n % m + m) % m;
}

bool FingerInstruction::progress() {
  
  updateMotorPositions();
  
  // if this is the first call to progress()
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    this->braking = false;
    
    this->initialPosition = positions[ROT_FINGER_IDX];

    // check if static position is acceptable 
    if (this->positionAcceptable(positions[ROT_FINGER_IDX], this->strength, false))
      return true;
      
    greenMotorMove(GREEN_FINGER_IDX, 100, MOTOR_FWD);
  }
  
  if (this->braking)
    return this->brakeFinger();
  
  if (this->positionAcceptable(positions[ROT_FINGER_IDX], this->strength, true))
    return this->brakeFinger();
  
  return false;
}
