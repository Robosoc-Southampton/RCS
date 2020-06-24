
#include "include/L298.h"

namespace {
    void setL298MotorSpeed(i16, i16, i16, i16);
}

void L298::setLeftMotorPins(i16 ENA, i16 IN1, i16 IN2) {
    this->ENA = ENA;
    this->IN1 = IN1;
    this->IN2 = IN2;

    pinMode(ENA, OUTPUT);
    pinMode(IN1, OUTPUT);
    pinMode(IN2, OUTPUT);

    digitalWrite(IN1, LOW);
    digitalWrite(IN2, LOW);
}

void L298::setRightMotorPins(i16 ENB, i16 IN3, i16 IN4) {
    this->ENB = ENB;
    this->IN3 = IN3;
    this->IN4 = IN4;

    pinMode(ENA, OUTPUT);
    pinMode(IN3, OUTPUT);
    pinMode(IN4, OUTPUT);

    digitalWrite(IN3, LOW);
    digitalWrite(IN4, LOW);
}

void L298::setLeftMotorSpeed(i16 speed) {
    setL298MotorSpeed(IN1, IN2, ENA, speed);
}

void L298::setRightMotorSpeed(i16 speed) {
    setL298MotorSpeed(IN3, IN4, ENB, speed);
}

namespace {
    void setL298MotorSpeed(i16 INX, i16 INY, i16 ENX, i16 speed) {
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
