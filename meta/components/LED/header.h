
#include <Arduino.h>

class LED {
public:
    void setup(int16_t pin, int16_t initial);
    void write(int16_t state);
    void on();
    void off();
    void toggle();

private:
    int16_t pin;
    bool state = false;
};
