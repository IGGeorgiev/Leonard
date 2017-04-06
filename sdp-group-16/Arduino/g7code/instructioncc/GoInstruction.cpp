#import "GoInstruction.h"

void GoInstruction::initFromCommand(Command cmd) {
  GoInstruction *fwd = new GoInstruction();
  fwd->cmdID = cmd.id;
  appendInstruction(fwd);
}

void GoInstruction::halt(void) {
  greenMotorMove(GREEN_LH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_RH_IDX, 0, MOTOR_FLOAT);
}

bool GoInstruction::progress() {
  
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    greenMotorMove(GREEN_LH_IDX, 100, MOTOR_FWD);
    greenMotorMove(GREEN_RH_IDX, 100, MOTOR_FWD);
  }
  
  
  
  return false;
}

