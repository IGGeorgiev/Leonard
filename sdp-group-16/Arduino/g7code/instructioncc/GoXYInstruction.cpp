#include "GoXYInstruction.h"
#include "Instruction.h"
#include "hardware.h"
#include "global.h"

#define VISION_LATENCY_PROJECTION 300   // ms - projecting latest position
#define VISION_LATENCY_WM_OUTDATED 500  // ms - if starting pos not provided, how long to assume a wmupdate is correct
#define VISION_LATENCY_NEW_CC 600       // ms - how soon after completed cc can we begin new one
//#define DEBUG_PRINT_CC
//#define DEBUG_PRINT_GOXY

void GoXYInstruction::initFromCommand(Command cmd) {
  
  int sx = byteArrToSignedInt(cmd.params, 0);
  int sy = byteArrToSignedInt(cmd.params, 2);
  int sh = byteArrToSignedInt(cmd.params, 4);
  int tx = byteArrToSignedInt(cmd.params, 6);
  int ty = byteArrToSignedInt(cmd.params, 8);
  
  GoXYInstruction *goxy = new GoXYInstruction();
  
  // initial positions unknown signalled by -1 heading
  if (sh == -1) {
    goxy->src.head = -1;
    goxy->positionUnknown = true;
  } else {
    goxy->src.head = sh;
    goxy->positionUnknown = false;
  }
  goxy->src.coor.x = sx;
  goxy->src.coor.y = sy;
  goxy->targ.x = tx;
  goxy->targ.y = ty;
  goxy->useProjectedForCompl = true;
  goxy->completionBehaviour = COMPLETION_BRAKE;
  goxy->cmdID = cmd.id;
  appendInstruction(goxy);
}

void GoXYInstruction::halt(void) {
  greenMotorMove(GREEN_LH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_RH_IDX, 0, MOTOR_FLOAT);
  greenMotorMove(GREEN_REAR_IDX, 0, MOTOR_FLOAT);
#ifdef DEBUG_PRINT_GOXY
  Serial.println(F("halt"));
#endif
}

bool GoXYInstruction::brake(void) {

  updateMotorPositions();
  
  int clicks = positions[ROT_LH_MOTOR_IDX] + positions[ROT_RH_MOTOR_IDX];
  
  // first call to brake()
  if (!this->braking) {
    greenMotorMove(GREEN_LH_IDX, 100, MOTOR_BRAKE);
    greenMotorMove(GREEN_RH_IDX, 100, MOTOR_BRAKE);
    greenMotorMove(GREEN_REAR_IDX, 100, MOTOR_BRAKE);
    this->braking = true;
    this->brakeLastClicksTime = millis();
    this->brakeLastClicks = clicks;
    return false;
  } 
  
  // new clicks arrived, reset timer
  if (abs(clicks) > abs(this->brakeLastClicks)) {
    this->brakeLastClicks = clicks;
    this->brakeLastClicksTime = millis();
    return false;
  }
  
  // if no clicks in last 50ms
  if (millis() - this->brakeLastClicksTime > 50) {
    // float motors
    this->halt();
    return true; 
  }
  
  return false;
}

// checks if point is left or right of a line
// a is line start point, b is line end point and c is test point
bool isLeft(Point a, Point b, Point c) {
  return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) > 0;
}

int distanceFromLine(Point pa, Point pb, Point pc) {
  float a = pa.y-pb.y;
  float b = pb.x-pa.x;
  float c = (pa.x-pb.x)*pa.y + (pb.y-pa.y)*pa.x;
  return abs(a*pc.x + b*pc.y + c)/sqrt(a*a+b*b);
}

// given a point recorded by the vision system and the distance the robot is likely to have
// travelled since that point was recorded, project a point that the robot is likely at now.
Point projectedFuturePoint(Position start, int distance) {
 return makePoint(start.coor.x+cos(toRad(start.head))*distance, start.coor.y+sin(toRad(start.head))*distance);
}

bool reachedDestination(Point src, Point cur, Point targ) {
  int srcToCur = pointToPointDistance(src, cur);
  int srcToTarg = pointToPointDistance(src, targ);
  return (srcToCur >= srcToTarg);
}

// determines if course correct is necessary
// and without making tiny course corrections
bool needsCourseCorrect(Position cur, Point targ, int *turnRequired) {
  
  Vector usToTargVector = makeVector(targ.x - cur.coor.x, targ.y - cur.coor.y);
  Vector usDirVector = makeVector(cos(toRad(cur.head)), sin(toRad(cur.head)));
  *turnRequired = vectorAngle(usDirVector, usToTargVector);
  int distUsToTarg = pointToPointDistance(cur.coor, targ);
  int distFromProjectedLine = distUsToTarg * sin(*turnRequired);
  
  // also check alpha, so we don't do pointless, tiny ccs for long distances away from targ
  return (abs(distFromProjectedLine) >= 7 && abs(*turnRequired) > 5);
}

Position GoXYInstruction::latestPosition() {

  if (this->positionUnknown)
    return makePosition(0,0,0);
    
  // set coor to worldModel unless the worldModel is out of date
  Position latest;
  latest = worldModel.rob;
  
  if (worldModel.receivedTimestamp < this->startTime) {  // world model older than instruction
    latest = this->src;
  }
  
  return latest;
}

Position GoXYInstruction::projectedPosition() {
  
  if (this->positionUnknown)
    return makePosition(0,0,0);
  
  Position latest = latestPosition();
  
  unsigned long timeSinceCur = VISION_LATENCY_PROJECTION + (millis() - worldModel.receivedTimestamp);
  if (worldModel.receivedTimestamp < this->startTime)   // world model older than instruction
    timeSinceCur = VISION_LATENCY_PROJECTION + (millis() - this->startTime);
  
  // limit this to time when robot started moving
  timeSinceCur = min(timeSinceCur, millis() - this->startTime);
  
  Point projectedPos = projectedFuturePoint(latest, timeSinceCur * ROBOT_SPEED);
  
  Position projected;
  projected.coor = projectedPos;
  projected.head = latest.head;

  return projected;
}

// can be called to alter target point
// will only be called if the target is still roughly on the same line
void GoXYInstruction::retarg(byte params[]) {
  
  // if instruction is already in braking phase, do not attempt to retarg
  if (this->braking)
    return;
  
  int newX = byteArrToUnsignedInt(params, 2);
  int newY = byteArrToUnsignedInt(params, 4);
  
  this->targ.x = newX;
  this->targ.y = newY;
  
  // cancel ongoing course correction
  if (this->rearClicksRequired > 0) {
    greenMotorMove(GREEN_REAR_IDX, 0, MOTOR_FLOAT);
    this->rearClicksRequired = 0;
    this->lastCompletedCC = millis();
  }
}

// debug only
long last;
long unsigned lastWM = 0;

bool GoXYInstruction::progress() {
  
  updateMotorPositions();
  
  // if this is the first call to progress
  if (this->begun == false) {
    this->begun = true;
    this->startTime = millis();
    
    this->lastCompletedCC = 0;
    this->rearClicksRequired = 0;
    
    this->braking = false;
    
    // reset motor position counters
    positions[ROT_LH_MOTOR_IDX] = 0;
    positions[ROT_RH_MOTOR_IDX] = 0;
    positions[ROT_REAR_MOTOR_IDX] = 0;
#ifdef DEBUG_PRINT_GOXY
    Serial.println(F("Starting GoXY"));
#endif
    // fire motors up
    greenMotorMove(GREEN_LH_IDX, 100, MOTOR_FWD);
    greenMotorMove(GREEN_RH_IDX, 100, MOTOR_FWD);
    greenMotorMove(GREEN_REAR_IDX, 0, MOTOR_FLOAT);
  }
  
  if (this->braking)
    return this->brake();
  
  // if worldmodel has arrived since beginning instruction and won't be affected by vision lag, position is known
  if (this->positionUnknown && worldModel.receivedTimestamp >= this->startTime + VISION_LATENCY_WM_OUTDATED) {
    this->positionUnknown = false;
    this->src = worldModel.rob;
#ifdef DEBUG_PRINT_GOXY
    Serial.print(F("Received first useable world model: "));
    printPosition(worldModel.rob);
#endif
  }
  Position latestPos = this->latestPosition();
  Position projectedPos = this->projectedPosition();
  
  // debug only
  if (lastWM != worldModel.receivedTimestamp) {
    if (worldModel.receivedTimestamp - lastWM > 400 && lastWM != 0) {
#ifdef DEBUG_PRINT_GOXY
      Serial.print(F("Delayed WM! Time since last WM: "));
      println(worldModel.receivedTimestamp - lastWM);
#endif
    }
    lastWM = worldModel.receivedTimestamp;
  }

  // if course correct operation is in operation
  if (this->rearClicksRequired != 0) {
    // if course correct is complete
    if (abs(positions[ROT_REAR_MOTOR_IDX]) >= abs(this->rearClicksRequired)) {
#ifdef DEBUG_PRINT_CC
       Serial.print(F("Completed cc of "));
       println(this->rearClicksRequired);
#endif
       greenMotorMove(GREEN_REAR_IDX, 0, MOTOR_FLOAT);
       this->rearClicksRequired = 0;
       this->lastCompletedCC = millis();
    }
  }
  // if no course correct in operation and elegible to begin a new one 
  else if (millis() - this->lastCompletedCC > VISION_LATENCY_NEW_CC) {
    int turnRequired = 0;
    if (!this->positionUnknown && needsCourseCorrect(projectedPos, this->targ, &turnRequired)) {
#ifdef DEBUG_PRINT_CC
       Serial.print(F("BEGIN CC "));
       println(turnRequired);
#endif
       this->rearClicksRequired = turnRequired / (360.0/180.0);
       positions[ROT_REAR_MOTOR_IDX] = 0;
       greenMotorMove(GREEN_REAR_IDX, (rearClicksRequired > 0) ? -100 : 100, MOTOR_FWD);
    }
  }
  
  // check if destination has been reached (or timeout)
  bool destReached;
  if (!this->positionUnknown)
    destReached = reachedDestination(this->src.coor, ((this->useProjectedForCompl) ? projectedPos.coor : latestPos.coor), this->targ);
  else  // if positionUnknown, use time
    destReached = (millis() - this->startTime)*ROBOT_SPEED > pointToPointDistance(this->src.coor, this->targ);
  if(destReached || millis() - this->startTime > 10000) {
    if (this->completionBehaviour == COMPLETION_FLOAT) {
      this->halt();
      // let PC know we're done
      if (this->cmdID > -1) {
        comms.sendInstructionComplete(this->cmdID);
        this->cmdID = -1;
      }
    }
    else if (this->completionBehaviour == COMPLETION_BRAKE) {
      // let PC know we're done
      if (this->cmdID > -1) {
        comms.sendInstructionComplete(this->cmdID);
        this->cmdID = -1;
      }
      return this->brake();
    }
    return true;
  }
  
  // sensible print interval
  if (millis() - last > 300) {
    
    last = millis();
    
#ifdef DEBUG_PRINT_CC
//    if (!this->positionUnknown) {
//     Serial.print(F("CC req? "));
//     int turnRequired = 0;
//     print(needsCourseCorrect(projectedPos, this->targ, &turnRequired));
//     Serial.print(F(" TR: "));
//     println(turnRequired);
//     printPosition(latestPos);
//     printPosition(projectedPos);
//    } else {
//      Serial.print(F("posUnk st: "));
//      print(this->startTime);
//      Serial.print(F(" wm rc: "));
//      println(worldModel.receivedTimestamp);
//    }

     if (this->rearClicksRequired != 0) {
       Serial.print(F("ON cc "));
       println(this->rearClicksRequired);
     } else {
       Serial.print(F("not on CC for "));
       println(millis() - this->lastCompletedCC); 
     }
#endif
  }
  
  return false;
}
