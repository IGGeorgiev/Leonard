#include "WorldModel.h"
#include "global.h"

WorldModel worldModel;

void updateWorldModel(byte params[]) {
  
  unsigned long sent = byteArrToUnsignedLong(params, 0);
  
  // if this is an old or already known piece of info, ignore
  if (sent <= worldModel.sentTimestamp) {
    Serial.print(F("Old WM Received. Stored time "));
    Serial.print(worldModel.sentTimestamp);
    Serial.print(F(" new time "));
    Serial.println(sent);
    return;
  }
  
  worldModel.sentTimestamp = sent;
  worldModel.receivedTimestamp = millis();
  worldModel.rob.coor.x = byteArrToSignedInt(params, 4);
  worldModel.rob.coor.y = byteArrToSignedInt(params, 6);
  worldModel.rob.head = byteArrToSignedInt(params, 8);
  worldModel.ball.x = byteArrToSignedInt(params, 10);
  worldModel.ball.y = byteArrToSignedInt(params, 12);
  
  Serial.println(F("WM RC'd"));
}

void resetWorldModel() {
  worldModel.sentTimestamp = 0; 
  worldModel.receivedTimestamp = 0;
}

void WorldModel::debugPrint(void) {
  Serial.print(F("worldModel: ("));
  Serial.print(this->rob.coor.x);
  Serial.print(F(", "));
  Serial.print(this->rob.coor.y);
  Serial.print(F(") "));
  Serial.print(this->rob.head);
  Serial.print(F("deg sentcts:"));
  Serial.print(this->sentTimestamp);
  Serial.print(F(" rects:"));
  Serial.print(this->receivedTimestamp);
  Serial.println(F(""));
}
