#ifndef GOXYINSTRUCTION_H
#define GOXYINSTRUCTION_H

#include "Instruction.h"
#include "WorldModel.h"

#define ROBOT_SPEED (3.0/60.0)          // cm / ms


enum COMPLETION_BEHAVIOR {
  COMPLETION_BRAKE,
  COMPLETION_FLOAT,
  COMPLETION_CONTINUE
};

class GoXYInstruction : public Instruction {
public:
  // positions
  Position src;
  Point targ;
  bool positionUnknown;
  
  // course correction
  unsigned long lastCompletedCC;
  int rearClicksRequired;
  
  // completion
  bool useProjectedForCompl;
  enum COMPLETION_BEHAVIOR completionBehaviour;
  
  // braking
  bool braking;
  int brakeLastClicks;
  unsigned long brakeLastClicksTime;
  
public:
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
  void retarg(byte params[]);
  
protected:
  Position projectedPosition();
  Position latestPosition();
  bool brake();
  
};

Point projectedFuturePoint(Position start, int distance);
bool needsCourseCorrect(Position cur, Point ball, int *turnRequired);


#endif
