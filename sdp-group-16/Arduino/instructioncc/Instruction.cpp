#include <SoftwareSerial.h>
#include <Wire.h>
#include "Instruction.h"
#include <stdlib.h>
#include <Arduino.h>  // needed for Serial to work
#include "global.h"


/* Instruction (Abstract) */

// constructor
Instruction::Instruction(void) {
  this->begun = false;
  this->startTime = millis();
  this->cmdID = -1;
}

/* Instructions array methods */

Instruction *instructions[INSTRUCTIONS_SIZE];

void appendInstruction(Instruction *ins) {
  for (int i = 0 ; i < INSTRUCTIONS_SIZE ; i++) {
    if (instructions[i] == NULL) {
      instructions[i] = ins;
      return;
    }
  }
  delete ins;
  Serial.println(F("Error: instructions array full."));
}

void prependInstruction(Instruction *ins) {
  if (instructions[INSTRUCTIONS_SIZE-1] != NULL) {
     Serial.println(F("Error: instructions array full."));
     delete ins;
     return;
  }
  for (int i = INSTRUCTIONS_SIZE-1 ; i > 0 ; i--) {
    if (instructions[i-1] != NULL) {
      instructions[i] = instructions[i-1];
    }
  }
  instructions[0] = ins;
}

void insertInstruction(Instruction *ins, int index) {
  if (index < 0 || index >= INSTRUCTIONS_SIZE)
    return;
  if (instructions[INSTRUCTIONS_SIZE-1] != NULL) {
     Serial.println(F("Error: instructions array full."));
     delete ins;
     return;
  }
  for (int i = INSTRUCTIONS_SIZE-1 ; i > index ; i--) {
     if (instructions[i-1] != NULL) 
       instructions[i] = instructions[i-1];
  }
  instructions[index] = ins;
}

void progressInstruction() {
  
  // if there is an instruction at index 0, progresses it
  // and if complete, removes it
  Instruction *i = instructions[0];
  if (i != NULL)
    if (i->progress())
      deleteFirstInstruction();
}

void deleteAllInstructions() {
  for (int i = 0 ; i < INSTRUCTIONS_SIZE ; i++) {
    if(instructions[i] != NULL) {
      instructions[i]->halt();
      delete instructions[i];
    }
    instructions[i] = NULL;
  }
}

void deleteFirstInstruction() {
  
  if (instructions[0] != NULL) {
    delete instructions[0];
    instructions[0] = NULL;
  } else {
    Serial.println(F("Error: no first instruction to delete")); 
  }
  
  for (int i = 0 ; i < INSTRUCTIONS_SIZE-1 ; i++) {
    instructions[i] = instructions[i+1];
    instructions[i+1] = NULL;
  }

}

