#include "PrepKickInstruction.h"
#include "GrabInstruction.h"
#include "FingerInstruction.h"

// used for static checking if prepared
#define PREP_POS_WIDTH 3

// returns "lowest" position such that we're prepared for kick of given strength
int prepPos(unsigned int stren) {
  if (stren == 0)
    return 19;
  else if (stren == 1)
    return 11;
  else if (stren == 2)
    return 4;
  
  Serial.println(F("Error: kick must be [0,2]"));
  return 4;
}

// returns index of kick strength or -1
int PrepKickInstruction::preparedForKickOfStrength(int pos) {
  if (this->positionAcceptable(pos, 0, false))
    return 0;
  else if (this->positionAcceptable(pos, 1, false))
    return 1;
  else if (this->positionAcceptable(pos, 2, false))
    return 2;
  return -1;
}

void PrepKickInstruction::initFromCommand(Command cmd) {
  PrepKickInstruction *prep = new PrepKickInstruction();
  prep->strength = byteArrToUnsignedShort(cmd.params, 0);
  prep->cmdID = cmd.id;
  appendInstruction(prep);
}

bool PrepKickInstruction::positionAcceptable(int pos, unsigned int stren, bool moving) {
  int pp = prepPos(stren);
  // distance pp is ahead of pos b
  return (distanceAhead(pp, pos) <= ((moving) ? 0 : PREP_POS_WIDTH));
}

void PrepKickInstruction::halt(void) {
  FingerInstruction::halt();
  greenMotorMove(GREEN_GRABBER_IDX, 0, MOTOR_FLOAT);
}

bool PrepKickInstruction::progress(void) {
  
  // catch first progress call to "pull in" grabber
  if (!this->begun) {
    // let FingerInstruction set begun flag
    greenMotorMove(GREEN_GRABBER_IDX, -100, MOTOR_FWD);
  }
  
  bool complete = FingerInstruction::progress();
  
  if (complete)
    greenMotorMove(GREEN_GRABBER_IDX, 0, MOTOR_FLOAT);
    
   return complete;
}

