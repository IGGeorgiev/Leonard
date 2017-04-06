#include "KickInstruction.h"
#include "GrabInstruction.h"
#include "PrepKickInstruction.h"
#include "PauseInstruction.h"

// number of clicks after prep pos that we know we've kicked
#define KICKED_AFTER_PREP_POS 4
#define GUARANTEED_TO_KICK (24/3)

void KickInstruction::initFromCommand(Command cmd) {

  // pause so that grabber "relaxes"
  PauseInstruction *pause = new PauseInstruction();
  pause->pause = 200;
  appendInstruction(pause);

  // lift grabber
  GrabInstruction *ungrab = new GrabInstruction();
  ungrab->ungrab = true;
  appendInstruction(ungrab);
  
  // kick
  KickInstruction *kick = new KickInstruction();
  kick->strength = byteArrToUnsignedShort(cmd.params, 0);
  appendInstruction(kick);
  
  GrabInstruction *grab = new GrabInstruction();
  grab->ungrab = false;
  // we assign the command ID to the grab instruction,
  // so that it will tell PC if we have ball or not after kick
  grab->cmdID = cmd.id;
  appendInstruction(grab);
}

// given initialPos and kick strength, returns position at which kick will have occurred
int kickedPos(int initialPos, unsigned int stren) {
  
  int distAheadOfPrepPos = distanceAhead(prepPos(stren), initialPos);
  
  // wasn't prepared correctly, make any kick lobe travel over it.
  if (distAheadOfPrepPos > KICKED_AFTER_PREP_POS)
    return initialPos + GUARANTEED_TO_KICK;
  
  return initialPos + (KICKED_AFTER_PREP_POS - distAheadOfPrepPos);
}

bool KickInstruction::positionAcceptable(int pos, unsigned int stren, bool moving) {
  return (pos >= kickedPos(this->initialPosition, stren));
}

