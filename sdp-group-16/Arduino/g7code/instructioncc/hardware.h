#ifndef HARDWARE_H
#define HARDWARE_H

#define ROT_LH_MOTOR_IDX 0
#define ROT_RH_MOTOR_IDX 1
#define ROT_FINGER_IDX 3
#define ROT_GRABBER_IDX 4
#define ROT_REAR_MOTOR_IDX 5

// "green" refers to lego-style motor board
// 1-4 are on board at address GREEN_ADDRESS_1_4
#define GREEN_ADDRESS_1_4 0x5A
#define GREEN_LH_IDX 1
#define GREEN_RH_IDX 2
#define GREEN_REAR_IDX 3
#define GREEN_FINGER_IDX 4
// 5-8 are on board at address GREEN_ADDRESS_5_8
#define GREEN_ADDRESS_5_8 0x50
#define GREEN_GRABBER_IDX 5

// Arduino direct I/O
#define SONAR_GRABBER_TRIG 3
#define SONAR_GRABBER_ECHO A3
#define REED_SWITCH_GRABBER 5

void hardwareSetup();


/* MOTORS / ROTARY ENCODERS */

enum MOTOR_DIR {
	MOTOR_FLOAT = 0,
	MOTOR_FWD = 1,
	MOTOR_BWD = 2,
	MOTOR_BRAKE = 3
};

#define ROTARY_SLAVE_ADDRESS 5
#define ROTARY_COUNT 6

extern long int positions[ROTARY_COUNT];// = {0};

void resetMotorPositions();
void updateMotorPositions();
void printMotorPositions();

void greenMotorMove(int motorNum, int motorPower, enum MOTOR_DIR dir);
void greenMotorAllStop(void);




/* SONAR */
int sonarDistance(int trigPin, int echoPin, int maxDist);
bool ballInReach(int *counter, int countsRequired);

/* GRABBER */
bool hasBall();


#endif
