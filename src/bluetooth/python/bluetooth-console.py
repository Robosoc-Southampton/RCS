
import argparse
import bluetooth
import os
import sys
import time

parser = argparse.ArgumentParser(description=
    'Connect to a bluetooth device and stream data to/from it using stdin/stdout.')
parser.add_argument('--addr', help='MAC address of the bluetooth device')

args = parser.parse_args()
bluetooth_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
mac_address = args.addr

try:
    bluetooth_socket.connect((mac_address, 1))
except bluetooth.btcommon.BluetoothError as e:
    sys.stderr.write("Failed to connect to " + mac_address)
    sys.stderr.flush()
    os.exit(1)

sys.stdout.write("" + chr(0))
sys.stdout.flush()

while True:
    data = sys.stdin.buffer.read(1)
    if data == "": break
    sys.stderr.write(f"{time.clock()} Sending '{data}'\n")
    bluetooth_socket.send(data)
