import serial
from threading import Lock
from ErrorHandler import ErrorHandler


class SerialInterface:
    """ Facilitates writing messages to a bluetooth device. """

    def __init__(self, port, err: ErrorHandler):
        self.__port = port
        self.__ser = None
        self.__connected = False
        self.__connectLock = Lock()
        self.__err = err

    def connect(self):
        """ Try to connect to the bluetooth device, returning true if successful. """
        self.__connectLock.acquire()

        if not self.__connected:
            try:
                self.__ser = serial.Serial(self.__port, 9600, timeout=0)
                self.__connected = True

            except Exception as e: # TODO
                self.__err.handleError(e)

        self.__connectLock.release()

        return self.__connected

    def send(self, data: str):
        """ Try to send data to the connected device, returning true if successful. """
        if not self.connect(): return False

        try:
            self.__ser.write(data)
            return True
        except bluetooth.btcommon.BluetoothError as e:
            self.__err.handleError(e)
            self.__connected = False
            return None

    def read(self, bytes: int):  # TODO
        """ Read a specific number of bytes, blocking. """
        data = b""

        while self.connect() and bytes > 0:
            append = self.__ser.recv(bytes)
            data += append
            bytes -= len(append)

        return data
