
void RCSServo::setup(int16_t pin, int16_t initial, int16_t min, int16_t max) {
    arduinoServo.attach(pin);
    this->min = min;
    this->max = max;
    write(initial);
}

void RCSServo::write(int16_t value) {
    if (value < min) value = min;
    if (value > max) value = max;
    arduinoServo.write(value);
}
