
#include "include/rcs.h"

void rcs::stop() {
    // TODO
}

void rcs::notify() {
    digitalWrite(LED_BUILTIN, HIGH);
    delay(200);
    digitalWrite(LED_BUILTIN, LOW);
    delay(200);
    digitalWrite(LED_BUILTIN, HIGH);
    delay(200);
    digitalWrite(LED_BUILTIN, LOW);
    delay(200);
}

void rcs::error(const char *message) {
    rcs::write_serial_int(0);
    Serial.println(message);

    rcs::stop();

    while (1) {
        digitalWrite(LED_BUILTIN, LOW);
        delay(100);
        digitalWrite(LED_BUILTIN, HIGH);
        delay(100);
    }
}

void rcs::assert(bool condition, const char *message) {
    if (!condition) error(message);
}

i16 rcs::read_serial_int() {
    while (Serial.available() < 2);
    i16 b0 = Serial.read();
    i16 b1 = Serial.read();
    return b1 << 8 | b0;
}

void rcs::write_serial_int(i16 data) {
    Serial.write((unsigned byte) data);
    Serial.write((unsigned byte) (data >> 8));
}
