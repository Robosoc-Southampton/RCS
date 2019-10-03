
# Bluetooth module

The bluetooth module adds a Kotlin wrapper to send data via bluetooth to a
robot.

It uses a python program (depending on pybluez) and pipes data to send using
stdin/stdout.

See `kotlin/BluetoothHandle`.

It's rather hacky but the native Java/Kotlin bluetooth libraries don't work
for me.
