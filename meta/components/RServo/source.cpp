
#include "include/RServo.h"

void RServo::attach(i16 pin, i16 initial) {
    arduinoServo.attach(pin);
    write(initial);
}

void RServo::setBounds(i16 min, i16 max) {
    this->min = min;
    this->max = max;
}

void RServo::write(i16 value) {
    if (value < min) value = min;
    if (value > max) value = max;
    arduinoServo.write(value);
}
