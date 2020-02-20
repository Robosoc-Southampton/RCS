
from BluetoothInterface import BluetoothInterface

import struct
import threading

RET_OPCODE_MESSAGE = 0
RET_OPCODE_ERROR = 1
RET_OPCODE_NO_RETURN = 2
RET_OPCODE_RETURN = 3


class MessageInQueue(threading.Thread):
    def __init__(self, bt: BluetoothInterface):
        threading.Thread.__init__(self)
        self.daemon = True
        self.__bt = bt
        self.__queue = []

    def append(self, future):
        self.__queue.append(future)

    def run(self):
        while True:
            (opcode,) = struct.unpack("<h", self.__bt.read(2))

            if opcode == RET_OPCODE_ERROR:
                pass  # TODO

            elif opcode == RET_OPCODE_MESSAGE:
                pass  # TODO

            elif opcode == RET_OPCODE_NO_RETURN:
                future = self.__queue.pop(0)
                future.setValue(None)

            elif opcode == RET_OPCODE_RETURN:
                future = self.__queue.pop(0)
                returnValue =  struct.unpack("<h", self.__bt.read(2))
                future.setValue(returnValue)

            else:
                print(f"Unexpected opcode {opcode}")
