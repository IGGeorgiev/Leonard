#ifndef REVSEREINSTRUCTION_H
#define REVSEREINSTRUCTION_H

#include "Instruction.h"
#include "WorldModel.h"

class ReverseInstruction : public Instruction {
public:
  int dist;  

  // braking
  bool braking;
  int brakeLastClicks;
  unsigned long brakeLastClicksTime;
  
public:
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
protected:
  bool brake(void);
  
};


#endif
