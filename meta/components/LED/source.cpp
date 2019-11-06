
void LED::setup(int16_t pin, int16_t initial) {
    this->pin = pin;

    pinMode(pin, OUTPUT);
    write(initial);
}

void LED::write(int16_t state) {
    if (state) on(); else off();
}

void LED::on() {
    digitalWrite(pin, HIGH);
    state = true;
}

void LED::off() {
    digitalWrite(pin, LOW);
    state = false;
}

void LED::toggle() {
    if (state) off(); else on();
}

