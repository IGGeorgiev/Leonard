#ifndef GETBALLINSTRUCTION_H
#define GETBALLINSTRUCTION_H

#include "global.h"
#include "hardware.h"
#include "GoXYInstruction.h"
#include "GrabInstruction.h"

class GetBallInstruction : public GoXYInstruction {
private:
  
  bool grabberOpened;
  GrabInstruction *openGrabberInstruction;
  bool grabbedBall;
  bool goxyComplete;
  int ballSeenCounter;

  
public:
  virtual void halt(void);
  virtual bool progress();
  static void init() ;
  static void initFromCommand(Command cmd);
  bool hasBallForbidden();
};


#endif
