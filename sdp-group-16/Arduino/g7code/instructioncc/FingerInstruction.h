#ifndef FINGERINSTRUCTION_H
#define FINGERINSTRUCTION_H

#include "global.h"
#include "hardware.h"
#include "Instruction.h"

class FingerInstruction : public Instruction {
public:
  
  unsigned int strength;
  int initialPosition;
  
  // braking
  bool braking;
  int brakeLastClicks;
  unsigned long brakeLastClicksTime;
  unsigned long brakeCompleteTime;
  
public:
  virtual void halt(void);
  bool brakeFinger(void);
  virtual bool postBrakeProgress();
  virtual bool progress();
  virtual bool positionAcceptable(int pos, unsigned int stren, bool moving) = 0;
};

int distanceAhead(int a, int b);
int positiveModulo(int n, int m);

#endif
