#ifndef GOXYINSTRUCTION_H
#define GOXYINSTRUCTION_H

#include "WorldModel.h"
#include "global.h"
#include "hardware.h"
#include "Instruction.h"
#include "TurnInstruction.h"
#include <math.h>

#define TARGET_RADIUS (7)
#define CORRECTION_STOP (10)
#define CORRECTION_START (20)
#define ANGLE_TO_CONSIDER_TURNING (45)
#define MIN_ANGLE_FOR_TURN (10)

enum MOVE_DIRECTION {
  FORWARD = 0,
  RIGHT = 1,
  BACKWARD = 2,
  LEFT = 3
};

class GoXYInstruction : public Instruction {

public:
  // positions
  Position src;
  Position targ;
  Position cur;

  // course correction
  bool correcting;
  bool newWM;
  long timestampWM;
  int correction;
  long timeAtCorrection;
  
  // braking
  long timeAtBrake;
  bool braking;
  unsigned int brakeLastClicks;

  // direction
  enum MOVE_DIRECTION moveDirection;
  
  virtual void halt(void);
  virtual bool progress();
  static void initFromCommand(Command cmd);
  
protected:
  bool reachedDestination();
  bool reachedCorrection();
  int angleCurToTarg();
  int directionOfMovement();
  void latestPosition();
  bool brake();
  void moveHalfMotors();
  void moveAllMotors();
};

#endif
