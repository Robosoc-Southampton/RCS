
#pragma once

#include "rcs.h"

class RLED {
public:
    void setup(i16 pin, i16 state);
    void write(i16 state);
    void on();
    void off();
    void toggle();

private:
    i16 pin;
    bool state = false;
};
