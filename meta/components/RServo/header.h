
#pragma once

#include "rcs.h"
#include <Servo.h>

class RServo {
public:
    void attach(i16 pin, i16 initial);
    void setBounds(i16 min, i16 max);
    void write(i16 value);

private:
    Servo arduinoServo;
    i16 min;
    i16 max;
};
