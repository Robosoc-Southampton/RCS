
#pragma once

#include <Arduino.h>
#include <Servo.h>

class RCSServo {
public:
    void setup(int16_t pin, int16_t initial, int16_t min, int16_t max);
    void write(int16_t value);

private:
    Servo arduinoServo;
    int16_t min;
    int16_t max;
};
