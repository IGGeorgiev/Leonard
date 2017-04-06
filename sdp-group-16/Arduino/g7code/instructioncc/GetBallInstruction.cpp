#include "GetBallInstruction.h"
#include "GrabInstruction.h"

#define OPEN_GRABBER_DISTANCE 80 // cm 

//#define DEBUG_PRINT_GETBALL

void GetBallInstruction::initFromCommand(Command cmd) {

  int sx = byteArrToSignedInt(cmd.params, 0);
  int sy = byteArrToSignedInt(cmd.params, 2);
  int sh = byteArrToSignedInt(cmd.params, 4);
  int bx = byteArrToSignedInt(cmd.params, 6);
  int by = byteArrToSignedInt(cmd.params, 8);
  
  GetBallInstruction *getBall = new GetBallInstruction();
  getBall->src.coor.x = sx;
  getBall->src.coor.y = sy;
  getBall->src.coor.x = sx;
  getBall->src.coor.y = sy;
  
  // initial positions unknown signalled by -1 heading
  if (sh == -1) {    
    getBall->src.head = -1;
    getBall->positionUnknown = true;
  } else {
    getBall->src.head = sh;
    getBall->positionUnknown = false;
  }
  
  getBall->targ.x = bx;
  getBall->targ.y = by;
  getBall->useProjectedForCompl = false;
  // prevent GoXY instruction from stopping motors
  getBall->completionBehaviour = COMPLETION_CONTINUE;
  getBall->cmdID = cmd.id;
  appendInstruction(getBall);
}

bool GetBallInstruction::hasBallForbidden() {
  return true;
}

void GetBallInstruction::halt(void) {
  greenMotorMove(GREEN_LH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_RH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_REAR_IDX, 0, MOTOR_FLOAT);
}

bool GetBallInstruction::progress() {

  // if this is the first call to progress()
  if (this->begun == false) {
    // do not set this->begun to true, allow GoXY to do that
    this->grabbedBall = false;
    this->grabberOpened = false;
    this->openGrabberInstruction = NULL;
    this->goxyComplete = false;
    this->ballSeenCounter = 0;
  }
  
  if (!this->goxyComplete)
    this->goxyComplete = GoXYInstruction::progress();  
  
  Position latestPos = latestPosition();
  Position projectedPos = projectedPosition();
  
  // if eligible to ungrab or already ungrabbing
  bool elegibleToUngrab = (!this->positionUnknown && (pointToPointDistance(projectedPos.coor, this->targ) < OPEN_GRABBER_DISTANCE));
  if (this->positionUnknown)
    elegibleToUngrab = (millis() - this->startTime)*ROBOT_SPEED > (pointToPointDistance(this->src.coor, this->targ) - OPEN_GRABBER_DISTANCE);
  if ((elegibleToUngrab || this->openGrabberInstruction != NULL) && this->grabberOpened == false) {
    // begin ungrab
    if (this->openGrabberInstruction == NULL) {
      this->openGrabberInstruction = new GrabInstruction();
      this->openGrabberInstruction->ungrab = true;  
    }
    // if complete
    if(this->openGrabberInstruction->progress()) {
      delete this->openGrabberInstruction;
      this->openGrabberInstruction = NULL;
      this->grabberOpened = true;
    }
  }
    
  // if grabbedBall flag is set then GrabInstruction has complete
  if (this->grabbedBall) {
#ifdef DEBUG_PRINT_GETBALL
    if (!this->braking)
      Serial.println(F("GetBall Braking"));
#endif
    // let PC know we're done and if we got ball
    if (this->cmdID > -1) {
      comms.sendInstructionCompleteParam(this->cmdID, hasBall());
      this->cmdID = -1;
    }

    return this->brake();
  }
  // if sonar sensor sees ball or we've reached the ball's coordinates
  else if (this->grabberOpened && (this->goxyComplete || ballInReach(&this->ballSeenCounter, 3))) {
     // set grabbedBall flag, hand off to grab instruction, still driving
     this->grabbedBall = true;
     GrabInstruction *grab = new GrabInstruction();
     grab->ungrab = false;
     insertInstruction(grab, 0);
#ifdef DEBUG_PRINT_GETBALL
     if (this->goxyComplete)
       Serial.println(F("GoXY Complete"));
     else {
       Serial.println(F("Ball detected"));
     }
#endif
     return false;
  }
  
  return false;
}
