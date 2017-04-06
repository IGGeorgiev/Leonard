#ifndef PAUSEINSTRUCTION_H
#define PAUSEINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

class PauseInstruction : public Instruction {
public:
  unsigned long pause; // ms
  
public:
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
  bool hasBallForbidden();
};

#endif
