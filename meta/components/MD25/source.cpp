
#include "include/MD25.h"

namespace {
	const uint8_t MD25_ADDRESS = 0x58u,
	              SPEED1_REGISTER = 0x00u,
	              SPEED2_REGISTER = 0x01u,
	              ENCODER1_REGISTER = 0x02,
	              ENCODER2_REGISTER = 0x06,
	              VOLTAGE_REGISTER = 0x0A,
	              ENCODER_RESET = 0x20,
	              MODE_SELECTOR = 0x0F,
	              ACCELERATION_REGISTER = 0x0E,
	              COMMAND_REGISTER = 0x10;

	const uint8_t STOP_SPEED = 128,
	              DEFAULT_ACCELERATION = 5;

	const uint16_t ENCODER_READ_TRIES = 3;
    const uint16_t ENCODER_READ_TIMEOUT = 5000;

    void i2c_write(uint8_t register, uint8_t data);
    int32_t i2c_read4(uint8_t register);
    bool i2c_test_read4(uint8_t register);
    uint8_t i2c_read1(uint8_t register);
};

const int TRIES = 3;
const int TIMEOUT = 5000;

void MD25::setup() {
	Wire.begin();

	i2c_write(MODE_SELECTOR, 0);
	i2c_write(ACCELERATION_REGISTER, DEFAULT_ACCELERATION);

	// delay required to allow the MD25 to actually initialise
	delay(500);

	resetEncoders();
}

i16 MD25::testEncoders(i16 left, i16 right) {
    i16 okay = true;
	if (left) okay &= i2c_test_read4(ENCODER1_REGISTER);
	if (right) okay &= i2c_test_read4(ENCODER2_REGISTER);
	return okay;
}

void MD25::setAcceleration(i16 acceleration) {
	i2c_write(ACCELERATION_REGISTER, acceleration);
}

void MD25::setLeftMotorSpeed(i16 speed) {
	i2c_write(SPEED1_REGISTER, speed);
}

void MD25::setRightMotorSpeed(i16 speed) {
	i2c_write(SPEED2_REGISTER, speed);
}

void MD25::stopMotors() {
	i2c_write(SPEED1_REGISTER, STOP_SPEED);
	i2c_write(SPEED2_REGISTER, STOP_SPEED);
}

void MD25::resetEncoders() {
	i2c_write(COMMAND_REGISTER, ENCODER_RESET);
}

i16 MD25::readLeftEncoder() {
	return i2c_read4(ENCODER1_REGISTER);
}

i16 MD25::readRightEncoder() {
	return i2c_read4(ENCODER2_REGISTER);
}

i16 MD25::readBatteryVoltage() {
	return i2c_read1(VOLTAGE_REGISTER);
}


namespace {
    void i2c_write(uint8_t reg, uint8_t data) {
        Wire.beginTransmission(MD25_ADDRESS);
        Wire.write(reg);
        Wire.write(data);
        Wire.endTransmission();
    }

    int32_t i2c_read4(uint8_t reg) {
        int32_t value = 0;

        for (int t = TRIES; t; --t) {
            long start_time = micros();

            value = 0;

            delay(2); // TODO: maybe this helps?

            Wire.beginTransmission(MD25_ADDRESS);
            Wire.write(reg);
            Wire.endTransmission();

            delay(2); // TODO: maybe this helps?

            Wire.requestFrom(MD25_ADDRESS, 4u); // request 4 bytes

            while (Wire.available() < 4) { // wait for 4 bytes
                if (micros() - start_time > TIMEOUT) break;
            }

            if (Wire.available() < 4) {
                digitalWrite(LED_BUILTIN, HIGH);
                while (Wire.available()) Wire.read();
                delay(10);
                continue;
            }

            for (uint8_t i = 0; i < 4; ++i) {
                value <<= 8;
                value += Wire.read();
            }

            digitalWrite(LED_BUILTIN, LOW);

            return value;
        }

        rcs::error("MD25 encoder read fails.");
    }

    bool i2c_test_read4(uint8_t reg) {
        uint8_t to_read = 4;
        auto start_time = millis();

        Wire.beginTransmission(MD25_ADDRESS);
        Wire.write(reg);
        Wire.endTransmission();

        Wire.requestFrom(MD25_ADDRESS, 4u); // request 4 bytes

        while (Wire.available() < 4 && millis() - start_time < 500); // wait for 4 bytes

        for (int i = 0; Wire.available() && to_read; ++i) {
            Wire.read();
            --to_read;
        }

        return to_read == 0;
    }

    uint8_t i2c_read1(uint8_t reg) {
        Wire.beginTransmission(MD25_ADDRESS);
        Wire.write(reg);
        Wire.endTransmission();

        Wire.requestFrom(MD25_ADDRESS, 1u); // request 4 bytes

        while (Wire.available() < 1); // wait for 4 bytes

        return Wire.read();
    }
}
