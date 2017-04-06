#include "GoXYInstruction.h"

//#define DEBUG_PRINT_GOXY
//#define DEBUG_PRINT_CC

/*
 * Creates a turn instruction from tagret coordinaes, heading and the rotation required
 */
TurnInstruction *createTurnInstruction(int rotationRequired, int tx, int ty, int th, int sh) {
  TurnInstruction *turn = new TurnInstruction();
  turn->correctionsRemaining = 0;

  // Calculate degrees
  if ((tx == -1) && (ty == -1)) {
    if (th == -1) turn->deg = 0; // if do not care heading
    else {
      turn->deg = (th - sh);
      if (turn->deg < -180) turn->deg += 360;
      else if (turn->deg > 180) turn->deg -= 360;
    }
  }
  else turn->deg = rotationRequired;
 
  return turn;
}

/*
 * Based on the direction to front, calculates the move direction and 
 * the rotation required to turn there
 */
int calculateDirectionAndTurn(GoXYInstruction *goxy, int directionToFront) {
  if ((-45 <= directionToFront) && (directionToFront < 45)) {
    goxy->moveDirection = FORWARD;
    return directionToFront;
  }
  else if ((45 <= directionToFront) && (directionToFront < 135)) {
    goxy->moveDirection = RIGHT;
    return directionToFront - 90;
  }
  else if ((-135 <= directionToFront) && (directionToFront < -45)) {
    goxy->moveDirection = LEFT;
    return directionToFront + 90;
  }
  else {
    goxy->moveDirection = BACKWARD;
    int rotationRequired = directionToFront - 180;
    if (rotationRequired <= -90) rotationRequired += 360;
    return rotationRequired;
  }
}

void GoXYInstruction::initFromCommand(Command cmd) {
 
  int sx = worldModel.rob.coor.x;
  int sy = worldModel.rob.coor.y;
  int sh = worldModel.rob.head;
  int tx = byteArrToSignedInt(cmd.params, 6);
  int ty = byteArrToSignedInt(cmd.params, 8);
  int th = byteArrToSignedInt(cmd.params, 10);
  
  GoXYInstruction *goxy = new GoXYInstruction();
  
  goxy->src.head = sh;
  goxy->src.coor.x = sx;
  goxy->src.coor.y = sy;
  goxy->cur.head = sh;
  goxy->cur.coor.x = sx;
  goxy->cur.coor.y = sy;
  goxy->targ.head = th;
  goxy->targ.coor.x = tx;
  goxy->targ.coor.y = ty;
  goxy->cmdID = cmd.id;

  // If already at destination, consider just turn
  if (goxy->reachedDestination()) {
    tx = -1;
    ty = -1;
    goxy->targ.coor.x = -1;
    goxy->targ.coor.y = -1;
  }

  // Calculate direction to go and turn to execute first
  Vector srcToTarg = makeVector(tx - sx, ty - sy);
  Vector headVector = headToVector(sh);
  int directionToFront = vectorAngle(headVector, srcToTarg);
  int rotationRequired = calculateDirectionAndTurn(goxy, directionToFront);
  TurnInstruction *turn1 = createTurnInstruction(rotationRequired, tx, ty, th, sh);
  if (abs(turn1->deg) > 0) appendInstruction(turn1);
  else delete turn1;
  appendInstruction(goxy);

#ifdef DEBUG_PRINT_GOXY
  Serial.print(F("Turn1: "));
  Serial.println(turn1->deg);
  Serial.print(F("Going in direction "));
  Serial.print(goxy->moveDirection);
  Serial.print(F(" because direction to front: "));
  Serial.println(directionToFront);
  Serial.print(F("srcToTarg: "));
  Serial.print(srcToTarg.x);
  Serial.print(F(" "));
  Serial.println(srcToTarg.y);
  Serial.print("headVector: ");
  Serial.print(headVector.x);
  Serial.print(F(" "));
  Serial.println(headVector.y);
#endif
}

// If robot is within the radius of the target, GoXY is completed
bool GoXYInstruction::reachedDestination() {
  int srcToTarg = pointToPointDistance(this->cur.coor, this->targ.coor);
  return (srcToTarg < TARGET_RADIUS);
}

// If the robot is heading towards the target, correction is completed
bool GoXYInstruction::reachedCorrection() {
  int direction = this->angleCurToTarg();
	return abs(direction) < CORRECTION_STOP;
}

// Returns the difference between its currently moving direction and the direction of the target
int GoXYInstruction::angleCurToTarg() {
  Vector headVector = headToVector(this->directionOfMovement());
  Vector curToTarg = makeVector(this->targ.coor.x - this->cur.coor.x, this->targ.coor.y - this->cur.coor.y);
  return vectorAngle(headVector, curToTarg);
}

/*
 * Returns the angle in which the robot is moving
 */
int GoXYInstruction::directionOfMovement() {
  int dir;
  if (this->moveDirection == FORWARD) dir = this->cur.head;
  else if (this->moveDirection == RIGHT) dir = this->cur.head + 90;
  else if (this->moveDirection == BACKWARD) dir = this->cur.head + 180;
  else if (this->moveDirection == LEFT) dir = this->cur.head + 270;
  dir += 360;
  return dir % 360;
}

// Updates the position of the robot if a new world model is received
void GoXYInstruction::latestPosition() {
  if (this->timestampWM != worldModel.sentTimestamp) {
    this->cur = worldModel.rob;
    this->timestampWM = worldModel.sentTimestamp;
    this->newWM = true;
  }
  else this->newWM = false;
}

// Motors during course correction
void GoXYInstruction::moveHalfMotors() {
  if (this->moveDirection == FORWARD) {
    motorMove(FL_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_FWD);
    motorMove(FR_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_BWD);
  }
  else if (this->moveDirection == RIGHT) {
    motorMove(FL_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_FWD);
    motorMove(FR_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_BWD);
    motorMove(BR_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_FWD);
  }
  else if (this->moveDirection == BACKWARD) {
    motorMove(FL_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_BWD);
    motorMove(BR_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_FWD);
    motorMove(BL_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_FWD);
  }
  else if (this->moveDirection == LEFT) {
    motorMove(FL_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, (this->correction < 0) ? 100 : 50, MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_FWD);
    motorMove(BL_MOTOR_IDX, (this->correction < 0) ? 50 : 100, MOTOR_BWD);
  }
}

// Motors during non course correcting state
void GoXYInstruction::moveAllMotors() {
  if (this->moveDirection == FORWARD) {
    motorMove(FL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(FR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
  }
  else if (this->moveDirection == RIGHT) {
    motorMove(FL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(FR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(BR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(BL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
  }
  else if (this->moveDirection == BACKWARD) {
    motorMove(FL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(BR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(BL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
  }
  else if (this->moveDirection == LEFT) {
    motorMove(FL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
    motorMove(FR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(BR_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_FWD);
    motorMove(BL_MOTOR_IDX, (this->braking) ? -100 : 100, MOTOR_BWD);
  }
}

void GoXYInstruction::halt(void) {
  motorMove(FL_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(FR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BR_MOTOR_IDX, 0, MOTOR_FLOAT);
  motorMove(BL_MOTOR_IDX, 0, MOTOR_FLOAT);
#ifdef DEBUG_PRINT_GOXY
  Serial.println(F("halt"));
#endif
}

bool GoXYInstruction::brake(void) {

  unsigned int clicks;

  // Update clicks according to the direction
  if (this->moveDirection == FORWARD) clicks = positions[FL_MOTOR_IDX] + positions[FR_MOTOR_IDX] - positions[BR_MOTOR_IDX] - positions[BL_MOTOR_IDX];
  else if (this->moveDirection == RIGHT) clicks = positions[FL_MOTOR_IDX] - positions[FR_MOTOR_IDX] - positions[BR_MOTOR_IDX] + positions[BL_MOTOR_IDX];
  else if (this->moveDirection == BACKWARD) clicks = - positions[FL_MOTOR_IDX] - positions[FR_MOTOR_IDX] + positions[BR_MOTOR_IDX] + positions[BL_MOTOR_IDX];
  else if (this->moveDirection == LEFT) clicks = - positions[FL_MOTOR_IDX] + positions[FR_MOTOR_IDX] + positions[BR_MOTOR_IDX] - positions[BL_MOTOR_IDX];

  // first call to brake()
  if (!this->braking) {
    this->braking = true;
    this->brakeLastClicks = clicks;
    this->moveAllMotors();
    this->timeAtBrake = millis();
    return false;
  } 
  
  // if wheels are still spinning forward within timer
  if ((clicks > this->brakeLastClicks) && ((millis() - this->timeAtBrake) < 500)) {
    this->brakeLastClicks = clicks;
    return false;
  }
  else {
    this->halt();
    if (this->targ.head == -1) {
      // let PC know we're done
      if (this->cmdID > -1) {
        comms.sendInstructionComplete(this->cmdID);
        this->cmdID = -1;
      }
    }
    // Generate final turn instruction
    else {
      int rotationRequired = this->targ.head - this->cur.head;
      if (rotationRequired > 180) rotationRequired -= 360;
      else if (rotationRequired < -180) rotationRequired += 360;
      TurnInstruction *turn2 = new TurnInstruction();
      turn2->deg = rotationRequired;
      turn2->correctionsRemaining = 0;
      turn2->cmdID = this->cmdID;
      insertInstruction(turn2,1);
      delay(150);
    }
    return true; 
  }
  return false;
}

bool GoXYInstruction::progress() {

  // If we don't care about final coordinates, we're done
  if ((this->targ.coor.x == -1) && (this->targ.coor.y == -1)) {
    // let PC know we're done
    if (this->cmdID > -1) {
      comms.sendInstructionComplete(this->cmdID);
      this->cmdID = -1;
    }
    return true;
  }

  this->latestPosition();
  updateMotorPositions();
  
  // if this is the first call to progress
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    this->correcting = false;
    this->braking = false;
    resetMotorPositions();

  #ifdef DEBUG_PRINT_GOXY
    Serial.print(F("Starting GoXY in "));
    Serial.print(this->moveDirection);
    Serial.println(F(" direction"));
  #endif
    // fire motors up
    this->moveAllMotors();
  }

  if (this->braking) {
    return this->brake();
  }

  // Waiting for the latest world model
  if (!this->newWM) return false;

  // check if destination has been reached (or timeout)
  if (this->reachedDestination() || (millis() - this->startTime > 30000)) {
    return this->brake();
  }

  // if course correct operation
  if (this->correcting) {
    // if course correct is complete or timeout
    if (this->reachedCorrection() || (millis() - this->timeAtCorrection > 500)) {
      this->correcting = false;
      this->moveAllMotors();
    }
  }

  // turn
  // If the current movement vector and the current to target vector differ, execute turn
  if (abs(this->angleCurToTarg()) > ANGLE_TO_CONSIDER_TURNING) {
    
    // Calculate the difference
    Vector headVector = headToVector(this->cur.head);
    Vector curToTarg = makeVector(this->targ.coor.x - this->cur.coor.x, this->targ.coor.y - this->cur.coor.y);
    int directionToFront = vectorAngle(headVector, curToTarg); 
    int rotationRequired = calculateDirectionAndTurn(this, directionToFront);
    
    TurnInstruction *turn = createTurnInstruction(rotationRequired, this->targ.coor.x, this->targ.coor.y, this->targ.head, cur.head);
    
    if (abs(turn->deg) > MIN_ANGLE_FOR_TURN) {
      GoXYInstruction *goxy = new GoXYInstruction();
      goxy->src = this->cur;
      goxy->targ = this->targ;
      goxy->moveDirection = this->moveDirection;
      goxy->cmdID = this->cmdID; // Copy the ID to the new GoXY
      
      // Insert the two instructions right after this one
      insertInstruction(turn, 1);
      insertInstruction(goxy, 2);

      // Finish executing this instruction
      //return true; 
      this->cmdID = -1;
      this->targ.head = -1;
      return this->brake();
    }
    else {
      // just change direction
      delete turn;
      resetMotorPositions();
      this->moveAllMotors();
    }
  }

  // course correction by moving half motors at half power
  else if (abs(this->angleCurToTarg()) > CORRECTION_START) {
#ifdef DEBUG_PRINT_CC
    Serial.print(F("BEGIN CC "));
    if (this->angleCurToTarg() < 0) Serial.println(F("TO LEFT"));
    else Serial.println(F("TO RIGHT"));
#endif
    this->timeAtCorrection = millis();
    this->correcting = true;
    this->correction = this->angleCurToTarg();
    this->moveHalfMotors();
  }
  return false;
}
