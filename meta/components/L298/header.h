
#pragma once

#include <Arduino.h>

class L298 {
public:
    /** Set up the L298, specifying the pins to use. */
    void setup(int16_t IN1, int16_t IN2,
               int16_t IN3, int16_t IN4,
               int16_t ENA, int16_t ENB);

    /** Set the speed of the left motor. Expects an integer in the range -128
        to 127 inclusive. */
    void setLeftMotorSpeed(int16_t);

    /** Set the speed of the right motor. Expects an integer in the range -128
        to 127 inclusive. */
    void setRightMotorSpeed(int16_t);

private:
    int16_t IN1, IN2, IN3, IN4, ENA, ENB;
};
