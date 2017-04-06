#ifndef PREPKICKINSTRUCTION_H
#define PREPKICKINSTRUCTION_H

#include "FingerInstruction.h"
#include "global.h"

class PrepKickInstruction : public FingerInstruction {
  
public:
  virtual void halt(void);
  static void initFromCommand(Command cmd);
  bool positionAcceptable(int pos, unsigned int stren, bool moving);
  bool progress(void);
  int preparedForKickOfStrength(int pos);
};

int prepPos(unsigned int stren);


#endif
