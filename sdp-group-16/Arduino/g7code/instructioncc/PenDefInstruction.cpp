#include "PenDefInstruction.h"

#define CLICKS_PER_CM (180.0/26.5)
#define PEN_DEF_MOTOR_SPEED 100
#define PEN_DEF_MAX_CLICKS 230 // plus or minus

//#define DEBUG_PENDEF

int penDefTarget;

void updatePenDefTargetFromCommand(Command cmd) {
  penDefTarget = byteArrToSignedInt(cmd.params, 0);
}

void PenDefInstruction::initFromCommand(Command cmd) {
  PenDefInstruction *penDef = new PenDefInstruction();
  appendInstruction(penDef);
}

void PenDefInstruction::halt(void) {
  greenMotorMove(GREEN_LH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_RH_IDX, 0, MOTOR_FLOAT);
}

bool PenDefInstruction::brake(void) {

  updateMotorPositions();
  
  int clicks = positions[ROT_LH_MOTOR_IDX] + positions[ROT_RH_MOTOR_IDX];
  
  // first call to brake()
  if (!this->braking) {
    greenMotorMove(GREEN_LH_IDX, 100, MOTOR_BRAKE);
    greenMotorMove(GREEN_RH_IDX, 100, MOTOR_BRAKE);
    this->braking = true;
    this->brakeLastClicksTime = millis();
    this->brakeLastClicks = clicks;
    return false;
  } 
  
  // new clicks arrived, reset timer
  if (abs(clicks) > abs(this->brakeLastClicks)) {
    this->brakeLastClicks = clicks;
    this->brakeLastClicksTime = millis();
    return false;
  }
  
  // if no clicks in last 50ms
  if (millis() - this->brakeLastClicksTime > 50) {
    // float motors
    this->halt();
    this->braking = false;
    return true; 
  }
  
  return false;
}

bool PenDefInstruction::progress() {
  
  updateMotorPositions();
  
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    this->state = STATE_STATIONARY;
    penDefTarget = 0;
    positions[ROT_LH_MOTOR_IDX] = 0;
    positions[ROT_RH_MOTOR_IDX] = 0;
  }
  
  // if braking
  if (this->braking) {
    // if brake complete
    if(this->brake())
      this->state = STATE_STATIONARY;
    // brake not complete
    else
      return false;
  }
  
  // figure out where we are and where we need to be
  int totalClicks = positions[ROT_LH_MOTOR_IDX] + positions[ROT_RH_MOTOR_IDX];
  int clicksRequired = penDefTarget * CLICKS_PER_CM * 2;
  
  // limit to goal edges
  if (clicksRequired > 0)
    clicksRequired = min(clicksRequired, PEN_DEF_MAX_CLICKS);
  else
    clicksRequired = max(clicksRequired, -PEN_DEF_MAX_CLICKS);
  
  int distFromTarg = (totalClicks - clicksRequired) / (CLICKS_PER_CM * 2);
  
  PEN_DEF_STATE newState = STATE_STATIONARY;
  
  // determine which state we should be in
  if (abs(distFromTarg) < 5)
    newState = STATE_STATIONARY;
  else if (totalClicks < clicksRequired)
    newState = STATE_POS;
  else
    newState = STATE_NEG;
  
  // if we're already in the desired state
  if (this->state == newState)
    return false;
  
  // if we're currently moving, to enter any other state, we first need to become stationary
  if (this->state == STATE_NEG || this->state == STATE_POS) {
    this->brake();
    return false;
  }
  
  // match the motors to the requested state
  switch (newState) {
    case STATE_STATIONARY:
      greenMotorMove(GREEN_LH_IDX, 0, MOTOR_FLOAT);
      greenMotorMove(GREEN_RH_IDX, 0, MOTOR_FLOAT);
      break;
    case STATE_POS:
      greenMotorMove(GREEN_LH_IDX, PEN_DEF_MOTOR_SPEED, MOTOR_FWD);
      greenMotorMove(GREEN_RH_IDX, PEN_DEF_MOTOR_SPEED, MOTOR_FWD);
      break;
    case STATE_NEG:
      greenMotorMove(GREEN_LH_IDX, PEN_DEF_MOTOR_SPEED, MOTOR_BWD);
      greenMotorMove(GREEN_RH_IDX, PEN_DEF_MOTOR_SPEED, MOTOR_BWD);
      break;
  }
  
#ifdef DEBUG_PENDEF
  Serial.print(F("Changing state to "));
  Serial.println(newState);
  Serial.println(STATE_STATIONARY);
  Serial.print("clicksReq ");
  Serial.println(clicksRequired);
#endif

  this->state = newState;
  
  return false;
}

