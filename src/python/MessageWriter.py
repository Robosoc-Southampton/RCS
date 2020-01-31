
from BluetoothWriter import BluetoothWriter

import struct
import threading
import time

RET_OPCODE_ERROR = 0
RET_OPCODE_NO_RETURN = 1
RET_OPCODE_RETURN = 2


class MessageWriter:
    """ Facilitates sending buffered messages to a bluetooth device through a
        BluetoothWriter. """
    def __init__(self, bt: BluetoothInterface):
        self.__inQueue = __MessageInQueue(bt)
        self.__outQueue = __MessageOutQueue(bt, self.__inQueue)

    def sendAsync(opcode: int, *params: int):
        self.__outQueue.append([opcode, *params], lambda _: pass)

    def sendSync(opcode: int, *params: int):
        complete = False
        result = None

        def callback(r):
            complete = True
            result = r

        self.__outQueue.append([opcode, *params], callback)

        while not complete:
            self.__inQueue.wait()

        return result

##########################################################################################


class __MessageOutQueue(threading.Thread):
    def __init__(self, bt: BluetoothInterface, inQueue: __MessageInQueue):
        self.__bt = bt
        self.__inQueue = inQueue
        self.__lock = threading.Lock()
        self.__queue = []

    def append(self, data: [int], callback):
        self.__lock.acquire()
        self.__queue.append((data, callback))
        self.__lock.release()

    @override
    def run(self):
        while True:
            self.__lock.acquire()

            while len(self.__queue) > 0:
                first = self.__queue.pop(0)
                self.__inQueue.append(first[1])
                self.__bt.send(self.__formatData(first[0]))

            self.__lock.release()
            time.sleep(0.01)

    def __formatData(data):
        fmt = "".join(["<h" for _ in data])
        return struct.pack(fmt, *data)

##########################################################################################


class __MessageInQueue(threading.Thread):
    def __init__(self, bt: BluetoothInterface):
        self.__bt = bt
        self.__notifier = threading.Condition()
        self.__queue = []

    def wait(self):
        self.__notifier.wait()

    def append(self, callback):
        self.__queue.append(callback)

    @override
    def run(self):
        while True:
            opcode = struct.unpack("<h", self.__bt.read(2))

            if opcode == RET_OPCODE_ERROR:
                time.sleep(1)
                err = self.__bt.readAll()
                raise Exception(err)

            else:
                callback = self.__queue.pop(0)

                if opcode == RET_OPCODE_NO_RETURN:
                    callback(None)
                    self.__notifier.notifyAll()

                elif opcode == RET_OPCODE_RETURN:
                    returnValue = struct.unpack("<h", self.__bt.read(2))
                    callback(returnValue)
                    self.__notifier.notifyAll()

                else:
                    print(f"Unexpected opcode {opcode}")
