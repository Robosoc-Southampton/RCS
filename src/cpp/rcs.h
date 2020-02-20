
#pragma once

#include <Arduino.h>

using i16 = int16_t;

namespace rcs {
    void stop();
    void notify();
    void log(const char *message);
    void error(const char *message);
    void assert(bool condition, const char *message);

    i16 read_serial_int();
    void write_serial_int(i16);
}
