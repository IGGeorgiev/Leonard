#ifndef RECEIVEINSTRUCTION_H
#define RECEIVEINSTRUCTION_H

#include "hardware.h"
#include "Instruction.h"

class ReceiveInstruction : public Instruction {
private:
  unsigned long timeout; // ms
  unsigned long ungrabCompleteTime;
  bool ungrabComplete;
  bool grabbed;
  int ballSeenCounter;
public:
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
  bool hasBallForbidden();
};

#endif
