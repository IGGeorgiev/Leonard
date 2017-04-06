#ifndef INSTRUCTION_H
#define INSTRUCTION_H

#include "global.h"
#include "WorldModel.h"
#include <stdlib.h>
#include "hardware.h"
#include "Comms.h"


/* Instruction class (abstract) */

class Instruction {
public:
  Instruction();
  static void initFromCommand(Command cmd);
  virtual void halt(void) = 0;  // stops/cancels
  virtual bool progress(void) = 0;  // true means done/complete
  virtual bool hasBallForbidden();
  
protected:
  bool begun;
  unsigned long startTime;  // time (using millis()) at which instruction begun
public:
  int cmdID;
  
};


/* Instructions array methods */

#define INSTRUCTIONS_SIZE 10
extern Instruction *instructions[INSTRUCTIONS_SIZE];

void progressInstruction();
void appendInstruction(Instruction *ins);
void prependInstruction(Instruction *ins);
void insertInstruction(Instruction *ins, int index);
void deleteAllInstructions();
void deleteFirstInstruction();

/* Helper Methods */

// void resetMotorPositions() ;
// void updateMotorPositions();
// void printMotorPositions();
// void motorMove(int motorNo, int speed);

bool isLeft(Point a, Point b, Point c);
int distanceFromLine(Point pa, Point pb, Point pc);

#endif
