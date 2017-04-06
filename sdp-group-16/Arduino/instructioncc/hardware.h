#ifndef HARDWARE_H
#define HARDWARE_H

#define GRABBER_MOTOR_IDX 1
#define FL_MOTOR_IDX 2
#define FR_MOTOR_IDX 3
#define BR_MOTOR_IDX 4
#define BL_MOTOR_IDX 5

/* MOTORS / ROTARY ENCODERS */

enum MOTOR_DIR {
	MOTOR_FLOAT = 0,
	MOTOR_FWD = 1,
	MOTOR_BWD = 2,
};

// Encoder board specific constants
#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6

// Light Sensor specific constants
#define LIGHT_SENSOR_SLAVE_ADDRESS 0x39
#define ADC0_CODE 0x43
#define ADC1_CODE 0x83

extern long int positions[ROTARY_COUNT];// = {0};

void resetMotorPositions();
void updateMotorPositions();
void printMotorPositions();


void motorMove(int motorNum, int motorPower, enum MOTOR_DIR dir);

/* COMPASS */

void setupCompass(int mode, int pitch);
int getHeading();

/* GRABBER */

bool hasBall();

/* SOLENOID */

void kick();
void turnOffSolenoid();

/* RESET */
void reset();

#endif
