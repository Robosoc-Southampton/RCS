
import bluetooth

class BluetoothInterface:
    """ Facilitates writing messages to a bluetooth device. """
    def __init__(self, mac):
        self.__mac = mac
        self.__sock = None
        self.__connected = False

    def connect(self):
        """ Try to connect to the bluetooth device, returning true if successful. """
        if self.__connected: return True

        try:
            self.__sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
            self.__sock.connect((self.__mac, 1))
            self.__connected = True
        except bluetooth.btcommon.BluetoothError as e:
            print(e)
            return None

        return self.__connected

    def send(self, data: str):
        """ Try to send data to the connected device, returning true if successful. """
        if not self.connect(): return False

        try:
            self.__sock.send(data)
            return True
        except bluetooth.btcommon.BluetoothError as e:
            print(e)
            self.__connected = False
            return None

    def read(self, bytes: int):
        return self.__sock.recv(bytes)

    def readAll(self):
        data = ""
        while True:
            append = self.__sock.recv(1024)
            if len(append) == 0: break
            data += append
        return data
