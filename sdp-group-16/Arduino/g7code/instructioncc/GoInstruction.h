#ifndef GOINSTRUCTION_H
#define GOINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"
#include "global.h"

class GoInstruction : public Instruction {
public:
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
};

#endif
