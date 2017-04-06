#ifndef COMMS_H
#define COMMS_H

#include "Arduino.h"
#include "global.h"

/*
 Opcodes for each instruciton. 
 Only 1 byte allocated for storage.
 Obviously need to match up to opcodes on python PC-side comms system.
*/
#define MIN_OPCODE 1
#define MAX_OPCODE 17
enum Opcode {
  INVALID_OP = 0,  // do not implement
  RESET = 1,
  STOP = 2,
  UPDATEWM = 3,
  GO = 4,
  GOXY = 5,
  GETBALL = 6,
  TURN = 7,
  GRAB = 8,
  RECEIVE = 9,
  PREPKICK = 10,
  KICK = 11,
  REVERSE = 12,
  ABORT = 13,
  HASBALL = 14,
  RETARG = 15,
  PENDEF = 16,
  PENDEFUPD = 17
  // remember to change MAX_OPCODE above!
};

enum parseResult {
  TOO_SHORT,
  INVALID,
  SUCCESS
};

int noParamsBytesForOpcode(Opcode opcode);

// need not be any longer than longest possible instruction
#define BUFFER_SIZE 50

class Command;

class Comms {
  public:
    Comms();
    void resetMessID();
    void readSerial();
    void sendArdReset();
    void sendInstructionCompleteParam(int cmdID, bool success);
    void sendInstructionValueParam(int cmdID, int val);
    void sendInstructionComplete(int cmdID);
  
  private:
    unsigned int maxIDSuccessPCToArd; // stores the maximum ID of command we've received
    byte buffer[BUFFER_SIZE];
    int bufferPos;   // points at empty slot to insert next received byte
    
    void clearSoftwareBuffer();
    void clearSerialBuffer();
    void checkForCompleteCommand();
    bool validateNewCommand(Command c);
    
    void respondError();
    void respondSuccess();
};

#define MAX_PARAM_BYTES 10

class Command {
  public:
   unsigned int id;
   Opcode opcode; 
   byte params[MAX_PARAM_BYTES];
   unsigned int hash;
   
   Command();
   parseResult commandFromBytes(byte buffer[], int bufferPos);
   unsigned int computeHash();
   bool hashAddsUp();
   bool opcodeValid();
   void instantiateInstruction();
  
};

#endif
