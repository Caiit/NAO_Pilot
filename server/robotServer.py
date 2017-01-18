#!/usr/bin/python

import threading
import socket
import Queue
import json
import sys
import atexit

from naoqi import ALProxy

# ---------------- THREADING CLASS ----------------------------
class networkThread (threading.Thread):


    def __init__(self):
        threading.Thread.__init__(self)
        self.shutdown = False
        self.serverSocket = None
        self.connection = None
        self.inMessages = Queue.Queue()
        self.outMessages = Queue.Queue()
        self.buffer = ""


    def run(self):
        self.createServer()
        while (not self.shutdown):
            print "looping"
            if (not self.connection):
                self.connection, addr = self.serverSocket.accept()
                print 'Connected with ' + addr[0] + ':' + str(addr[1])
                # TODO: maak mooi geluidje
            self.receiveMessages()
            self.sendMessage()
        print "Closing server"
        self.serverSocket.close()


    def createServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print 'Socket created'

        # Bind socket to local host and port
        try:
            self.serverSocket.bind((HOST, PORT))
        except socket.error as msg:
            print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
            sys.exit()

        print 'Socket bind complete'

        # Start listening on socket
        self.serverSocket.listen(MAX_CLIENTS)
        print 'Socket now listening'


    def receiveMessages(self):
        print "Receiving"
        data = self.connection.recv(BUFFER_SIZE)[2:]
        if (not data):
            self.connection = None
            return
        try :
            self.connection.send("Data received \0")
        except socket.error:
            print 'Send failed'

        # Make sure message is correct json message
        self.buffer += data
        if ('}' not in self.buffer): return
        begin, end, self.buffer = self.buffer.partition('}')
        message = begin + end
        self.inMessages.put(message)


    def sendMessage(self):
        if (self.connection and not self.outMessages.empty()):
            self.connection.send(self.outMessages.get())


thread = networkThread()
IP = "localhost"
HOST = ''   # Symbolic name, meaning all available interfaces
PORT = 3006 # Arbitrary non-privileged port
MAX_CLIENTS = 5
BUFFER_SIZE = 1024

# Proxies
# tts = ALProxy("ALTextToSpeech", IP, 9559)
# motionProxy = ALProxy("ALMotion", IP, 9559)
# postureProxy = ALProxy("ALRobotPosture", IP, 9559)
# batteryProxy = ALProxy("ALBattery", IP, 9559)
# systemProxy = ALProxy("ALSystem", IP, 9559)

def toJson(data):
    return json.loads(data)


def onExit():
    # closeConnection(s)
    print "Exiting"
    if thread:
        thread.shutdown = True


def closeConnection(s):
    print("Closing connection")
    motionProxy.rest()
    s.close()


def stiffness(part, value):
    print part
    # if value == 0.0:
    #     motionProxy.rest()
    # motionProxy.setStiffnesses(part, value)


def speak(data):
    print data
    # tts.setVolume(float(data["volume"])/100)
    # pitch = float(data["pitch"]) / 100
    # if pitch > 0.0 and pitch < 1.0: pitch = 1.0
    # tts.setParameter("pitchShift", pitch)
    # tts.say("\\rspd=" + str(data["speed"]) + "\\" + str(data["text"]))


def walk(data):
    print data
    # if motionProxy.getStiffnesses("Body") < 0.8:
    #     stiffness("Body", 1.0)
    # postureProxy.goToPosture("StandInit", 0.5)
    # motionProxy.move(float(data["x_speed"]), float(data["y_speed"]), 0)

def getRobotInfo(data):
    print data
    # info = {"type": "info", "battery": battery.getBatteryCharge(), "name": systemProxy.robotName()}
    info = {"type": "info", "battery": 15, "name": "BeepBoop"}
    thread.outMessages.put(info)


def handleMessages():
    data = toJson(thread.inMessages.get())

    dataType = data["type"]
    if dataType == "connect":
        print "Connecting: ", data["text"]
    elif dataType == "info":
        getRobotInfo(data)
    elif dataType == "stiffness":
        stiffness(str(data["part"]), float(data["value"]))
    elif dataType == "speak":
        speak(data)
    elif dataType == "walk":
        walk(data)
    elif dataType == "disconnect":
        print "Disconnecting"


def main():
    atexit.register(onExit)
    thread.start()
    while (True):
        handleMessages()

if __name__ == "__main__":
    main()
