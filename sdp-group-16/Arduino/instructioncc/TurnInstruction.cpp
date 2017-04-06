#include "TurnInstruction.h"
#include <math.h>
#include <assert.h>

//#define DEBUG_PRINT_TURN

void TurnInstruction::initFromCommand(Command cmd) {

  TurnInstruction *turn = new TurnInstruction();
  turn->deg = byteArrToSignedInt(cmd.params, 0);
  turn->correctionsRemaining = byteArrToUnsignedShort(cmd.params, 2);
  turn->cmdID = cmd.id;

  #ifdef DEBUG_PRINT_TURN
  Serial.print(F("Turn "));
  Serial.println(turn->deg);
#endif

  appendInstruction(turn);
}

void TurnInstruction::halt(void) {
  motorMove(FL_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(FR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BL_MOTOR_IDX, 0, MOTOR_FLOAT);
#ifdef DEBUG_PRINT_TURN
  Serial.println(F("halt"));
#endif
}

int clicksToDeg(int clicks) {
  return (double) clicks * DEG_PER_CLICK / 4;
}

bool TurnInstruction::progress() {
  
  // if this is the first call to progress
  if (this->begun == false) {

    // if no turn required
    if (this->deg == 0) {
      if (this->cmdID > -1) {
        comms.sendInstructionComplete(this->cmdID);
        this->cmdID = -1;
      }
      return true;
    }

    this->begun = true;
    this->braking = false;
    this->startPosition = worldModel.rob.head;
    if (this->deg >= 0) this->destination = this->deg + worldModel.rob.head - ADVANCE_BRAKING;
    else this->destination = this->deg + worldModel.rob.head + ADVANCE_BRAKING;
    if (this->destination > 359) this->destination -= 360;
    else if (this->destination < 0) this->destination += 360;

    // correction for non linearity of clicks vs this amount
    int correction = (abs(this->deg) - 180) * (abs((double)this->deg)/90) * (CORRECTION_FACTOR);
    // number of clicks required to achieve the this
    this->totalClicksRequired = abs(4*((double)this->deg)/DEG_PER_CLICK) + correction;
    this->lastClicks = 0;
    
    // reset motor position counter
    resetMotorPositions();
    
#ifdef DEBUG_PRINT_TURN
    Serial.print(F("Begin turn "));
    Serial.print(this->deg);
    Serial.println(F(" deg)"));
#endif

    // fire up motors
    motorMove(FL_MOTOR_IDX, 100, (this->deg > 0) ? MOTOR_FWD : MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, 100, (this->deg > 0) ? MOTOR_BWD : MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, 100, (this->deg > 0) ? MOTOR_FWD : MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, 100, (this->deg > 0) ? MOTOR_BWD : MOTOR_FWD);
  }

  updateMotorPositions();
  this->cur = worldModel.rob.head;

  // sum of all motor clicks regardless of direction
  unsigned int positionsSum = abs(positions[FL_MOTOR_IDX]) + abs(positions[FR_MOTOR_IDX]) + abs(positions[BR_MOTOR_IDX]) + abs(positions[BL_MOTOR_IDX]);
  
  // if robot is braking
  if (this->braking) {
    
    if (positionsSum < this->lastClicks || this->deg < 3 || (millis() - this->timeAtBrake > 500)) {
      this->halt();

      // if robot has missed target and corrections are enabled
      float miss = this->totalClicksRequired - positionsSum;
      if (abs(miss) >= CORRECTION_TOLERANCE && this->correctionsRemaining > 0) {
        TurnInstruction *nextTurn = new TurnInstruction();
        if (this->deg >= 0) nextTurn->deg = clicksToDeg(miss);
        else nextTurn->deg = clicksToDeg(-miss);
        nextTurn->correctionsRemaining = this->correctionsRemaining - 1;
        nextTurn->cmdID = this->cmdID;
        insertInstruction(nextTurn, 1);
      } else {
        // let PC know we're done
        if (this->cmdID > -1) {
          comms.sendInstructionComplete(this->cmdID);
          this->cmdID = -1;
        } 
      }
      return true;
    }

    this->lastClicks = positionsSum;
    return false;
  }

  // Assess if robot has completed turn
  bool reachedDestination;
  if (this->deg > 0) {
    if ((this->startPosition + this->deg) < 360) reachedDestination = (this->cur >= this->destination) || (positionsSum >= this->totalClicksRequired);
    else reachedDestination = ((this->cur < 180) && (this->cur >= this->destination)) || (positionsSum >= this->totalClicksRequired);
  }
  else {
    if ((this->startPosition + this->deg) >= 0) reachedDestination = (this->cur <= this->destination) || (positionsSum >= this->totalClicksRequired);
    else reachedDestination = ((this->cur >= 180) && (this->cur <= this->destination)) || (positionsSum >= this->totalClicksRequired);
  }
  
  // check if complete
  if (reachedDestination) {
    
    this->braking = true;
    this->lastClicks = positionsSum;
    this->timeAtBrake = millis();

    motorMove(FL_MOTOR_IDX, -100, (this->deg > 0) ? MOTOR_FWD : MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, -100, (this->deg > 0) ? MOTOR_BWD : MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, -100, (this->deg > 0) ? MOTOR_FWD : MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, -100, (this->deg > 0) ? MOTOR_BWD : MOTOR_FWD);
    
#ifdef DEBUG_PRINT_TURN
    Serial.print(F("BRAKING at"));
    Serial.print(positionsSum);
    Serial.print(F(" clicks"));
#endif
  }

  return false;
}