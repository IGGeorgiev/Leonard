#ifndef TURNINSTRUCTION_H
#define TURNINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

class TurnInstruction : public Instruction {
public:
  float deg;
  unsigned long brakeTime;
  bool braking;
  int correctionsRemaining;
  
  int clicksAtBrake;
  int lastClicks;
  unsigned long lastClickTime;
  
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
};

void addClicksSinceLastProgress(int clicks, int timeSinceHead);
void shiftClicksTimeArray(int left);
float speedFromClicksTimeArray(long unsigned curTime);

#endif
