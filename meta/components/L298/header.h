
#pragma once

#include "rcs.h"

class L298 {
public:
    /** Set up the L298, specifying the pins to use. */
    void setLeftMotorPins(i16 ENA, i16 IN1, i16 IN2);
    void setRightMotorPins(i16 ENB, i16 IN3, i16 IN4);

    /** Set the speed of the motors. Expects an integer in the range -255
        to 255 inclusive. */
    void setLeftMotorSpeed(i16);
    void setRightMotorSpeed(i16);

private:
    i16 IN1, IN2, IN3, IN4, ENA, ENB;
};
