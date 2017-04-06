#ifndef KICKINSTRUCTION_H
#define KICKINSTRUCTION_H

#include "hardware.h"
#include "FingerInstruction.h"

class KickInstruction : public FingerInstruction {
public:
  static void initFromCommand(Command cmd);
  virtual bool positionAcceptable(int pos, unsigned int stren, bool moving);
  int preparedForKickOfStrength(int pos);
};

#endif
