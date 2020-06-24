
#pragma once

#include "rcs.h"
#include <Wire.h>

class MD25 {
public:
    void setup();

	// test each encoder (if the respective left/right flag is true), and
	// return whether every encoder tested can be read from
    i16 testEncoders(i16 left, i16 right);

	// sets and gets the acceleration of the MD25
	// valid acceleration values are in the range 1 to 10
	// see https://www.robot-electronics.co.uk/htm/md25i2c.htm#acceleration%20register
	void setAcceleration(i16 acceleration);

    void setLeftMotorSpeed(i16 speed);
    void setRightMotorSpeed(i16 speed);
    void stopMotors();

	// resets the encoders
    void resetEncoders();
	// reads the value of the left/right encoders
    i16 readLeftEncoder();
    i16 readRightEncoder();

	// returns the battery voltage multiplied by 10 (e.g. 12.1V -> 121)
	i16 readBatteryVoltage();
};
