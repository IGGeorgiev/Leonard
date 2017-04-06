#ifndef GRABINSTRUCTION_H
#define GRABINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

#define GRAB_DURATION 300
#define UNGRAB_DURATION 500
#define GRAB_HOLD_UP_PWM 50
#define GRAB_HOLD_DOWN_PWM 30

class GrabInstruction : public Instruction {
public:
  GrabInstruction();
  unsigned long startTime;
  bool ungrab;  // ungrab means lift grabber

  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
  bool hasBallForbidden();
};

#endif
