#include "PauseInstruction.h"
#include "global.h"

void PauseInstruction::initFromCommand(Command cmd) {
}

void PauseInstruction::halt(void) {
}

bool PauseInstruction::hasBallForbidden() {
  return true;
}

bool PauseInstruction::progress() {
  
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    return false;
  }
  
  // if pause time has elapsed
  if (millis() - this->startTime >= this->pause) {
  	if (this->cmdID > -1)
  	  comms.sendInstructionCompleteParam(this->cmdID, hasBall());
  	return true;
  }

  return false;
}

