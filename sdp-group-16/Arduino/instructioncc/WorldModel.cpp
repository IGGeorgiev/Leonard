#include "WorldModel.h"
#include "global.h"

#define WORLD_MODEL_DEBUG_PRINT

WorldModel worldModel;

void updateWorldModel(byte params[]) {
  
  unsigned long sent = byteArrToUnsignedLong(params, 0);
  
  // if this is an old or already known piece of info, ignore
  if (sent == worldModel.sentTimestamp) {
#ifdef WORLD_MODEL_DEBUG_PRINT
    Serial.print(F("Old WM Received."));
#endif    
    return;
  }
  
  worldModel.sentTimestamp = sent;
  worldModel.receivedTimestamp = millis();
  worldModel.rob.coor.x = byteArrToSignedInt(params, 4);
  worldModel.rob.coor.y = byteArrToSignedInt(params, 6);
  worldModel.rob.head = byteArrToSignedInt(params, 8);

#ifdef WORLD_MODEL_DEBUG_PRINT
  Serial.println(F("WM RC'd"));
  Serial.print(worldModel.rob.coor.x);
  Serial.print(" ");
  Serial.print(worldModel.rob.coor.y);
  Serial.print(" ");
  Serial.println(worldModel.rob.head);
#endif  
}
