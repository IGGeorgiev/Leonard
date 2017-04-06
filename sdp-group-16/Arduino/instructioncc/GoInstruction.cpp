#include "GoInstruction.h"

void GoInstruction::initFromCommand(Command cmd) {
  GoInstruction *fwd = new GoInstruction();
  fwd->cmdID = cmd.id;
  appendInstruction(fwd);
}

void GoInstruction::halt(void) {
  motorMove(FL_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(FR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BL_MOTOR_IDX, 0, MOTOR_FLOAT);
}

bool GoInstruction::progress() {
  
  if (this->begun == false) {
    this->begun = true;
    motorMove(GRABBER_MOTOR_IDX, 100, MOTOR_BWD);
    motorMove(FL_MOTOR_IDX, 100, MOTOR_FWD);
    motorMove(FR_MOTOR_IDX, 100, MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, 100,MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, 100,MOTOR_BWD);
    return false;
  }

  if (millis() - this->startTime > 750) {
    this->halt();
    if (this->cmdID > -1) {
      comms.sendInstructionComplete(this->cmdID);
      this->cmdID = -1;
    }
    return true;
  }
  return false;
}

