#ifndef TURNINSTRUCTION_H
#define TURNINSTRUCTION_H

#include "global.h"
#include "WorldModel.h"
#include "hardware.h"
#include "Instruction.h"

#define CORRECTION_TOLERANCE 10 // clicks
#define ADVANCE_BRAKING 0 // degrees
#define DEG_PER_CLICK 1.8
#define CORRECTION_FACTOR 0.6

class TurnInstruction : public Instruction {
public:

  // arguments
  int deg; // turn amount in deg
  unsigned short correctionsRemaining;

  // braking
  bool braking;
  long timeAtBrake;
  int lastClicks;

  // positions
  int startPosition;
  int cur;
  int destination;

  // clicks
  unsigned int totalClicksRequired;
  
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);

};

#endif
