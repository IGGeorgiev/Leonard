#include "Comms.h"
#include "global.h"
#include "hardware.h"
#include "SDPArduino.h"
#include "Instruction.h"
#include "GoInstruction.h"
#include "GoXYInstruction.h"
#include "TurnInstruction.h"
#include "GrabInstruction.h"
#include "KickInstruction.h"

#define ID_BYTES 2
#define OPCODE_BYTES 1

/* Static */

int noParamsBytesForOpcode(Opcode opcode) {
  // note: not number of params, but number of bytes for params (2 bytes per param)
  switch (opcode) {
    case RESET:     return 0;
    case STOP:      return 0;
    case GO:        return 0;
    case GOXY:      return 12; // [2]signed, [2]signed, [2]signed, [2]signed, [2]signed, [2]signed
    case TURN:      return 3;  // [2]signed, [1]unsigned
    case GRAB:      return 1;  // [1]unsigned
    case KICK:      return 1;  // [1]unsigned
    case HASBALL:   return 0;
    case UPDATEWM:  return 10; // [4]unsigned, [2]signed, [2]signed, [2]signed
    default:        return 0;
  } 
} 


/* Comms */

Comms::Comms() {
  this->maxIDSuccessPCToArd = 0;
  this->clearSoftwareBuffer();
  this->clearSerialBuffer();
}

/*
 Called regularly.
 Receives commands from PC to Arduino.
*/
void Comms::readSerial() {
  
  /*
   Read bytes in the Serial buffer into this->buffer
   At each byte, check if it completes an instruction
   If so, interprets it and resets buffer
  */
  
  while (Serial.available() > 0) {

    // read byte and add it to buffer
    byte inByte = Serial.read();
    
    // software buffer overflow
    if (this->bufferPos >= BUFFER_SIZE) {
      Serial.println(F("Error: Comms software buffer overflow."));
      this->clearSoftwareBuffer();
      this->clearSerialBuffer();
      return;
    }
    
    //Serial.println(inByte, BIN); 
    delay(5);
    
    if (this->bufferPos == 2 && inByte == 1) {
      Serial.println(F("WARNING: HARD RESET CASE"));
      this->hardReset();
      return; 
    }
    
    // append byte to buffer
    this->buffer[this->bufferPos] = inByte;
    this->bufferPos++;

    // check to see if a whole command is contained within buffer
    this->checkForCompleteCommand();
  }
}

/*
 Resets maxIDSuccessPCToArd to 1
 Necessary when the PC-side system restarts but we haven't
 Set to 1 as opposed to 0 becasue the RESET command had ID 1
*/
void Comms::resetMessID() {
  this->maxIDSuccessPCToArd = 1;
}

/*
 clears any bytes on the software buffer belonging to this class
*/
void Comms::clearSoftwareBuffer() {
  for (int i = 0 ; i < this->bufferPos ; i++)
    this->buffer[i] = 0;
  this->bufferPos = 0;
}

/*
 clears any bytes on the hardware serial buffer
 only call when error discovered
*/
void Comms::clearSerialBuffer() {
  while(Serial.available() > 0)
    Serial.read();
}

/*
 clears all inputs and sets PC to zero
*/
void Comms::hardReset() {
  // Stop motors
  motorAllStop();
  // Clear buffers
  this->clearSoftwareBuffer();
  this->clearSerialBuffer();
  // Jump to PC 0
  asm volatile ("  jmp 0");
}

/*
 called whenever a new byte is added to buffer
 check if we have enough bytes for a whole command
 */
void Comms::checkForCompleteCommand() {
  
  Command cmd;
  parseResult result = cmd.commandFromBytes(this->buffer, this->bufferPos); 
  
  if (result == TOO_SHORT)
    return;
  else if (result == INVALID) {
    Serial.println(F("Error: Comms parse error"));
    this->clearSoftwareBuffer();
    this->clearSerialBuffer();
    return;
  } else if (result == PARSE_RESET) {
    // Perform hard reset
    this->hardReset();
    return;
  }
  
  if (cmd.opcode == UPDATEWM) {
    // Do nothing, just remember the WM
    updateWorldModel(cmd.params);
  }
  else if (this->validateNewCommand(cmd)) {
    // update maxIDSuccessPCToArd, tell PC success and execute instruction
    this->maxIDSuccessPCToArd = cmd.id;
    this->respondSuccess();
    cmd.instantiateInstruction();
  }
  else {
    // tell the PC there was an error and clear the hardware buffer
    this->respondError();
    this->clearSerialBuffer();
  }
  
  // clear the software buffer regardless of success
  // (we never read more than we absolutely need for a command)
  this->clearSoftwareBuffer();
}

/*
 ID must be maxIDSuccessPCToArd  + 1
 Hash must add up
 Opcode must be valid
*/
bool Comms::validateNewCommand(Command c) {
  
  // TODO(barboram): remove all unnecessary parts here
  if (c.id == 1 && c.opcode == RESET)
    return true;
  
  if (c.id != this->maxIDSuccessPCToArd + 1) {
    Serial.println(F("ERRid"));
    Serial.println(c.id);
    Serial.println(this->maxIDSuccessPCToArd);
    return false;
  }
  
  if (!c.opcodeValid()) {
    Serial.println(F("ERRopcode"));
    Serial.println(c.opcode);
    return false; 
  }
  
  return true;
}

/*
 Sends command to PC to say we've reset and expect IDs to begin at 1
*/
void Comms::sendArdReset() {
  Serial.print(F("$ARDRESET;"));
}

/*
 Respond Error and provide the last successfully received message ID
*/
void Comms::respondError() {
  Serial.print(F("$ERR&"));
  Serial.print(this->maxIDSuccessPCToArd);
  Serial.println(F(";"));
}

/*
 Respond Success and provide the last successfully received message ID
*/
void Comms::respondSuccess() {
  Serial.print(F("$SUC&"));
  Serial.print(this->maxIDSuccessPCToArd);
  Serial.println(F(";"));
  delay(10);
  Serial.print(F("$SUC&"));
  Serial.print(this->maxIDSuccessPCToArd);
  Serial.println(F(";"));
}

/*
 Sends command to PC to say we've completed a specific instruction with success=yes/no
*/ 
void Comms::sendInstructionCompleteParam(int cmdID, bool success) {
  if (cmdID < 0)
    return;
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F("&"));
  Serial.print(success);
  Serial.print(F(";"));
  delay(10);
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F("&"));
  Serial.print(success);
  Serial.print(F(";"));
}

void Comms::sendInstructionValueParam(int cmdID, int val) {
  if (cmdID < 0)
    return;
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F("&"));
  Serial.print(val);
  Serial.print(F(";"));
  delay(10);
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F("&"));
  Serial.print(val);
  Serial.print(F(";"));
}

/*
 Sends command to PC to say we've completed a specific instruction
*/ 
void Comms::sendInstructionComplete(int cmdID) {
  if (cmdID < 0)
    return;
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F(";"));
  delay(10);
  Serial.print(F("$COMP&"));
  Serial.print(cmdID);
  Serial.print(F(";"));
}


/* Command */

Command::Command() {
  this->id = 0;
  this->opcode = INVALID_OP;
}


/*
 Constructor method for command from PC to Arduino.
 Returns false if not enough bytes yet
 Otherwise, returns true and sets relevant field of Command
*/
parseResult Command::commandFromBytes(byte buffer[], int bufferPos) {
  
  // must have at least ID and opcode
  if (bufferPos < ID_BYTES + OPCODE_BYTES)
    return TOO_SHORT;
  
  this->id = buffer[1] | buffer[0] << 8;
  this->opcode = static_cast<Opcode>(buffer[2]);

  if (!this->opcodeValid()) {
    Serial.println(this->opcode);
    return INVALID;
  }
  
  // If command is reset, ignore everything else and perform hard reset
  if (this->opcode == RESET) {
    return PARSE_RESET;
  }
  
  // all params 2 bytes
  int paramBytes = noParamsBytesForOpcode(this->opcode);
  
  // now we know params length, do we have all required bytes to interpret command?
  if (bufferPos < ID_BYTES + OPCODE_BYTES + paramBytes)
    return TOO_SHORT;
  
  // params is a byte array of size 2n where n is number of params
  for (int i = 0 ; i <  paramBytes ; i++)
    this->params[i] = buffer[ID_BYTES + OPCODE_BYTES+i];
  
  return SUCCESS;
}

bool Command::opcodeValid() {
   return (this->opcode >= MIN_OPCODE && this->opcode <= MAX_OPCODE); 
}

/*
 Given a well-formed command, this method instantiates an Instruction
 or calls necessary methods
*/
void Command::instantiateInstruction() {
  switch (this->opcode) {
    case RESET: {
      comms.resetMessID();
      deleteAllInstructions();
      reset();
      break;
    }
    case STOP: {
      deleteAllInstructions();
      // let PC know we're done
      if (this->id > -1) {
        comms.sendInstructionComplete(this->id);
      this->id = -1;
      }
      break;
    }
    case GO:
      GoInstruction::initFromCommand(*this);
      break;
    case GOXY:
      GoXYInstruction::initFromCommand(*this);
      break;
    case TURN:
      TurnInstruction::initFromCommand(*this);
      break;
    case GRAB:
      GrabInstruction::initFromCommand(*this);
      break;
    case KICK:
      KickInstruction::initFromCommand(*this);
      break;
    case HASBALL: {
      comms.sendInstructionCompleteParam(this->id, hasBall());
      break;
    }
    default:
      Serial.println(F("Error: opcode not defined")); 
      break;
  }
}
