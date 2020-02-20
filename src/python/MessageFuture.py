
from threading import Condition

class MessageFuture:
    def __init__(self):
        self.__event = Condition()
        self.__set = False
        self.__value = None

    def setValue(self, value):
        self.__value = value
        self.__set = True
        self.__event.acquire()
        self.__event.notifyAll()
        self.__event.release()

    def wait(self):
        self.__event.acquire()
        while not self.__set:
            self.__event.wait()
        self.__event.release()

        return self.__value
