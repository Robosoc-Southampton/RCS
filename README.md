# RCS

RCS aims to simplify the control of a robot by generating code and providing
tools for remote control, simulation, and debugging.

# RCS programs

The system is comprised of the following core programs but may be extended.

## Code generation

### `rcs-gen`

Generate Python, Kotlin and C++ code for controlling a robot.

Usage:

```
rcs-gen
    [-ga <arduino output path>]
    [-gk <kotlin file path>]
    [-gp <python file path>]
    <robot.json>
    <component definition paths...>
```

C++ code generated is to be compiled and uploaded to an Arduino.

Python and Kotlin code will connect to adapters (mentioned below)
and send messages using a defined protocol to control a virtual or
physical robot.

## Adapters

An adapter is a program accepting TCP connections, receiving messages
using a defined control protocol, and forwarding them to some system
that handles the control requests.

Adapters are designed to accept multiple connections seamlessly.

### `rcs-bluetooth-adapter`

An adapter encoding and forwarding messages via bluetooth to a
physical robot.

### `rcs-simulator-adapter`

An adapter simulating a physical board and driving virtual robots
around this board.

### `rcs-debug-adapter`

A simple adapter printing all requests and responding with generic
information. 

# Robot configuration

The system relies on configuration for robots and components being provided.

This configuration is in the form of JSON files and C source/header files for
components.

## `robot.json`

A robot definition has the following format:

```json
{
  "name": "<name>",
  "components": [
    <components...>
  ]
}
```

A component within a robot has the following format:

```json
{
  "type": "<component type>",
  "name": "<name of component>",
  "attributes": [
    {
      "name": "<attribute name>",
      "value": <attribute value>,
    }
  ],
  "tags": ["<tag 1>", "<tag 2>", ...]
}
```

## Component definition

Component definitions describe a generic component and provide
Arduino code for that component.

A component definition lives inside a folder containing
* `config.json` - Configuration
* `source.cpp` - Source code (header included automatically)
* `header.h` - Header

The configuration has the following format:

```json
{
  "name": "<component type>",
  "attributes": [
    <attribute definitions...>
  ],
  "methods": [
    <method definitions...>
  ]
}
```

Where an attribute definition has the following format:

```json
{
  "name": "<attribute name>",
  "default": <optional default value>
}
```

And a method definition has the following format:

```json
{
  "name": "<method name>",
  "parameters": ["<parameter 1 name>", "<parameter 2 name>", ...],
  "returns": <true|false>
}
```

# Libraries

Code in `src/lib` is general purpose and may be used by external
programs. `src/lib/kotlin` includes code for parsing robot configuration.
`src/lib/*` contains code for creating adapters.
