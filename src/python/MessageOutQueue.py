
from MessageInQueue import MessageInQueue
from BluetoothInterface import BluetoothInterface
from MessageFuture import MessageFuture

import struct
import threading
import time


# TODO: prevent buffer overflow
class MessageOutQueue(threading.Thread):
    def __init__(self, bt: BluetoothInterface, inQueue: MessageInQueue):
        threading.Thread.__init__(self)
        self.daemon = True
        self.__bt = bt
        self.__inQueue = inQueue
        self.__lock = threading.Lock()
        self.__queue = []

    def append(self, data: [int], future: MessageFuture):
        self.__lock.acquire()
        self.__queue.append((data, future))
        self.__lock.release()

    def run(self):
        while True:
            self.__lock.acquire()

            while len(self.__queue) > 0:
                first = self.__queue.pop(0)
                self.__inQueue.append(first[1])
                self.__bt.send(self.__formatData(first[0]))

            self.__lock.release()
            time.sleep(0.01)

    def __formatData(self, data: [int]):
        fmt = "<" + "".join(["h" for _ in data])
        return struct.pack(fmt, *data)
