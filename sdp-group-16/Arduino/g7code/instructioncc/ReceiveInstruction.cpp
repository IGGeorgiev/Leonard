#include "ReceiveInstruction.h"
#include "GrabInstruction.h"
#include "global.h"

#define MIN_OPEN_TIME 100 // ms

void ReceiveInstruction::initFromCommand(Command cmd) {
  ReceiveInstruction *receiveIns = new ReceiveInstruction();
  receiveIns->timeout = byteArrToUnsignedLong(cmd.params, 0);
  receiveIns->cmdID = cmd.id;
  appendInstruction(receiveIns);
}

bool ReceiveInstruction::hasBallForbidden() {
  return true;
}

void ReceiveInstruction::halt(void) {
  greenMotorMove(GREEN_GRABBER_IDX, 0, MOTOR_FLOAT);
}

bool ReceiveInstruction::progress() {
  
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    this->grabbed = false;
    this->ballSeenCounter = 0;
    // put ungrab instruction ahead of this
    GrabInstruction *ungrab = new GrabInstruction();
    ungrab->ungrab = true;
    insertInstruction(ungrab, 0);
    this->ungrabComplete = false;
    return false;
  }
  
  if (!this->ungrabComplete) {
    this->ungrabComplete = true;
    this->ungrabCompleteTime = millis();
  }
  
  if (this->grabbed) {
    // let PC know we're done and if we got ball
    if (this->cmdID > -1)
      comms.sendInstructionCompleteParam(this->cmdID, hasBall());
    return true;
  }
  
  if (millis() - ungrabCompleteTime < MIN_OPEN_TIME)
    return false;
  
  // if now + time to grab exceeds timeout, or sonar has detected ball
  bool timeoutReached = (this->timeout > 0 && (millis() + GRAB_DURATION - this->startTime >= this->timeout));
  if (timeoutReached || ballInReach(&this->ballSeenCounter, 2)) {
    this->grabbed = true;
    GrabInstruction *grab = new GrabInstruction();
    grab->ungrab = false;
    insertInstruction(grab, 0);
    return false;
  }
  
  return false;
}

