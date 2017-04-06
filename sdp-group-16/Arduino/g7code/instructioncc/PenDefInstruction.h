#ifndef PENDEFINSTRUCTION_H
#define PENDEFINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

extern int penDefTarget;
void updatePenDefTargetFromCommand(Command cmd);

enum PEN_DEF_STATE {
  STATE_STATIONARY,
  STATE_POS,
  STATE_NEG
};

class PenDefInstruction : public Instruction {
public:
  virtual void halt(void);
  virtual bool progress();
  bool brake(void);
  static void initFromCommand(Command cmd);
  
private:
  PEN_DEF_STATE state;
  
  // braking
  bool braking;
  int brakeLastClicks;
  unsigned long brakeLastClicksTime;
};

#endif
