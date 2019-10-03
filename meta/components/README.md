
# Components

`meta/components` contains standard component definitions for commonly used
electronic components.

## Special components

There are a few classes components that support specially treated behaviour,
for example motor controllers allowing movement control.

### Motor controllers

A motor controller must have two methods `setLeftMotorSpeed` and
`setRightMotorSpeed`, each taking an integer between -128 and 127 inclusive for
backwards/forwards. Any numbers outside this range should be clamped within the
range.
