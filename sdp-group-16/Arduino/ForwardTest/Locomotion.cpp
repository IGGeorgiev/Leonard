#include "SDPArduino.h"

// Defines the motors to their port on the motor board
#define frontMotor 2
#define rightMotor 3
#define backMotor 4
#define leftMotor 5
#define kicker 1

// if facing the wheel
// motorForward == anticlockwise
// motorBackward == clockwise

void moveForward() {
	motorBackward(rightMotor,100);
	motorForward(leftMotor,100);
}

void moveRight() {
	motorForward(frontMotor,100);
	motorBackward(backMotor,100);
}

void moveBackward() {
	motorBackward(leftMotor,100);
	motorForward(rightMotor,100);
}

void moveLeft() {
	motorForward(backMotor,100);
	motorBackward(frontMotor,100);
}

void rotateClockwise() {
	motorForward(backMotor,100);
	motorForward(frontMotor,100);
	motorForward(leftMotor,100);
	motorForward(rightMotor,100);
}

void rotateAntiClockwise() {
	motorBackward(backMotor,100);
	motorBackward(frontMotor,100);
	motorBackward(leftMotor,100);
	motorBackward(rightMotor,100);
}

void kick() {
	motorBackward(kicker, 100);
}
