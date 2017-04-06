#ifndef KICKINSTRUCTION_H
#define KICKINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

class KickInstruction : public Instruction {

public:

  unsigned int strength;

  static void initFromCommand(Command cmd);
  virtual void halt();
  virtual bool progress();
};

#endif
