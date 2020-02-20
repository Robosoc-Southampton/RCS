from ErrorHandler import ErrorHandler
import bluetooth
from threading import Lock


class BluetoothInterface:
    """ Facilitates writing messages to a bluetooth device. """

    def __init__(self, mac, err: ErrorHandler):
        self.__mac = mac
        self.__sock = None
        self.__connected = False
        self.__connectLock = Lock()
        self.__err = err

    def connect(self):
        """ Try to connect to the bluetooth device, returning true if successful. """
        self.__connectLock.acquire()

        if not self.__connected:
            try:
                self.__sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
                self.__sock.connect((self.__mac, 1))
                self.__connected = True

            except bluetooth.btcommon.BluetoothError as e:
                self.__err.handleError(e)

        self.__connectLock.release()

        return self.__connected

    def send(self, data: str):
        """ Try to send data to the connected device, returning true if successful. """
        if not self.connect(): return False

        try:
            self.__sock.send(data)
            return True
        except bluetooth.btcommon.BluetoothError as e:
            self.__err.handleError(e)
            self.__connected = False
            return None

    def read(self, bytes: int):  # TODO
        """ Read a specific number of bytes, blocking. """
        data = b""

        while self.connect() and bytes > 0:
            append = self.__sock.recv(bytes)
            data += append
            bytes -= len(append)

        return data
