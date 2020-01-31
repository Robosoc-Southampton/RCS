
#include "include/L298.h"

namespace {
    void setL298MotorSpeed(int16_t, int16_t, int16_t, int16_t);
}

void L298::setup(int16_t IN1, int16_t IN2,
                 int16_t IN3, int16_t IN4,
                 int16_t ENA, int16_t ENB) {
    this->IN1 = IN1;
    this->IN2 = IN2;
    this->IN3 = IN3;
    this->IN4 = IN4;
    this->ENA = ENA;
    this->ENB = ENB;

    pinMode(IN1, OUTPUT);
    pinMode(IN2, OUTPUT);
    pinMode(IN3, OUTPUT);
    pinMode(IN4, OUTPUT);
    pinMode(ENA, OUTPUT);
    pinMode(ENB, OUTPUT);

    digitalWrite(IN1, LOW);
    digitalWrite(IN2, LOW);
    digitalWrite(IN3, LOW);
    digitalWrite(IN4, LOW);
}

void L298::setLeftMotorSpeed(int16_t speed) {
    setL298MotorSpeed(IN2, IN1, ENB, speed);
}

void L298::setRightMotorSpeed(int16_t speed) {
    setL298MotorSpeed(IN3, IN4, ENA, speed);
}

namespace {
    void setL298MotorSpeed(int16_t INX, int16_t INY, int16_t ENX, int16_t speed) {
        if (speed < 0) {
            speed = -speed;
            digitalWrite(INX, HIGH);
            digitalWrite(INY, LOW);
        }
        else {
            digitalWrite(INX, LOW);
            digitalWrite(INY, HIGH);
        }

        if (speed > 128) speed = 128;

        analogWrite(ENX, speed * 255 / 128);
    }
}
