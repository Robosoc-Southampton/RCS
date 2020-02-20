from BluetoothInterface import BluetoothInterface
from MessageFuture import MessageFuture
from MessageOutQueue import MessageOutQueue
from MessageInQueue import MessageInQueue


class MessageWriter:
    """ Facilitates sending buffered messages to a bluetooth device through a
        BluetoothInterface. """

    def __init__(self, bt: BluetoothInterface):
        self.__inQueue = MessageInQueue(bt)
        self.__outQueue = MessageOutQueue(bt, self.__inQueue)
        self.__inQueue.start()
        self.__outQueue.start()
        self.__lastFuture = None

    def send(self, opcode: int, *params: int):
        future = MessageFuture()
        self.__outQueue.append([opcode, *params], future)
        self.__lastFuture = future
        return future

    def waitForLast(self):
        if self.__lastFuture is not None:
            self.__lastFuture.wait()


import time

bt = BluetoothInterface("98:D3:32:11:17:5F", None)
m = MessageWriter(bt)
m.send(11, 13, 0)
m.send(13)
time.sleep(1)
m.send(14)
time.sleep(1)
m.send(13)
time.sleep(1)
m.send(14)
m.waitForLast()
