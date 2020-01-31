
#include "include/RLED.h"

void RLED::attach(i16 pin, i16 state) {
    this->pin = pin;

    pinMode(pin, OUTPUT);
    write(state);
}

void RLED::write(i16 state) {
    if (state) on(); else off();
}

void RLED::on() {
    digitalWrite(pin, HIGH);
    state = true;
}

void RLED::off() {
    digitalWrite(pin, LOW);
    state = false;
}

void RLED::toggle() {
    if (state) off(); else on();
}

