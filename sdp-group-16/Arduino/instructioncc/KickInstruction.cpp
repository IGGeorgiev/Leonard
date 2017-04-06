#include "KickInstruction.h"

void KickInstruction::initFromCommand(Command cmd) {
  KickInstruction *kick = new KickInstruction();
  kick->strength = byteArrToUnsignedShort(cmd.params, 0);
  kick->cmdID = cmd.id;
  appendInstruction(kick);
}

void KickInstruction::halt(void) {
  turnOffSolenoid();
}

bool KickInstruction::progress() {
  kick(); // turns on solenoid
  delay(this->strength);
  this->halt();
  if (this->cmdID > -1) {
  	comms.sendInstructionComplete(this->cmdID);
  	this->cmdID = -1;
  }
  return true;
}
