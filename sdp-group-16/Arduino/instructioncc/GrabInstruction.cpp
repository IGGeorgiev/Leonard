#include "GrabInstruction.h"
#include "global.h"

// uncomment to print debug info
//#define DEBUG_GRAB

// static method which creates an instance of GrabInstruction
// with params from a Command, and adds instruction to queue
void GrabInstruction::initFromCommand(Command cmd) {
  GrabInstruction *grab = new GrabInstruction();
  grab->ungrab = (byteArrToUnsignedShort(cmd.params, 0) == 1);
  grab->cmdID = cmd.id;
  appendInstruction(grab);
}

// hasBall() is not allowed to be called during a grab instruction
bool GrabInstruction::hasBallForbidden() {
  return true;
}

// default constructor
GrabInstruction::GrabInstruction() {
  this->cmdID = -1;
}

void GrabInstruction::halt(void) {
  // cut power to the grabber motor
  motorMove(GRABBER_MOTOR_IDX, 0, MOTOR_FLOAT);
}

bool GrabInstruction::progress() {
  
  // if first time progress() has been called
  if (this->begun == false) {
    if (this->ungrab) delay(150);
    this->begun = true;
    this->startTime = millis();
    // power the grabber motor forward or backward, depending on if this instruction
    // if a "grab" or "ungrab" instruction
    motorMove(GRABBER_MOTOR_IDX, (this->ungrab) ? 100 : -50, MOTOR_BWD);
  }
  
  // if this is an "ungrab" instruction (lift grabber up)
  if (this->ungrab) {

    // check for completion
    if (millis() - this->startTime > UNGRAB_DURATION) {
      motorMove(GRABBER_MOTOR_IDX, GRAB_HOLD_UP_PWM, MOTOR_BWD);
      // let PC know we're done
      if (this->cmdID > -1) {
        comms.sendInstructionComplete(this->cmdID);
        this->cmdID = -1;
      }
      return true;
    }

  } 
  // if this is a "grab" instruciton (drive grabber down)
  else {

    // check for completion
    if (millis() - this->startTime > GRAB_DURATION) {
      motorMove(GRABBER_MOTOR_IDX, GRAB_HOLD_DOWN_PWM, MOTOR_FWD);
      
      // let PC know we're done and if we still have ball
      if (this->cmdID > -1) {
        comms.sendInstructionCompleteParam(this->cmdID, hasBall());
#ifdef DEBUG_GRAB
        // Serial.print(F("grab with ID complete "));
        // Serial.print(hasBall());
#endif
        this->cmdID = -1;
      }
      
      return true;
    }
  }
  
  return false;
}

