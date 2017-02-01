#!/usr/bin/python

import threading
import socket
import errno
import select
import Queue
import json
import sys
import atexit

from naoqi import ALProxy
import vision_definitions
import Image
import base64

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
        self.HOST = ""
        self.PORT = 3006
        self.errorSend = False


    def run(self):
        self.createServer()
        while (not self.shutdown):
            if (not self.connection):
                self.connection, addr = self.serverSocket.accept()
                self.connection.setblocking(0)
                print 'Connected with ' + addr[0] + ':' + str(addr[1])
                self.inMessages.put('{"type": "connect"}')
            self.receiveMessages()
            self.sendMessage()
        print "Closing server"
        self.serverSocket.close()


    def createServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, 99999999)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print 'Socket created'

        # Bind socket to local host and port
        try:
            self.serverSocket.bind((self.HOST, self.PORT))
        except socket.error as msg:
            print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
            sys.exit()

        print 'Socket bind complete'

        # Start listening on socket
        self.serverSocket.listen(1)
        print 'Socket now listening'


    def receiveMessages(self):
        size = 0
        try:
            msg = self.connection.recv(10)[2:]
            if (not msg == ""):
                size = int(msg)
        except IOError as e:
            if e.errno == errno.EWOULDBLOCK:
                # Waiting for new message
                self.errorSend = False
                return
            print "Other problem with connection: Disconnected"
            self.connection = None
            self.outMessages.put('{"type": "disconnect"}')
        except ValueError:
            if not self.errorSend:
                self.outMessages.put('{"type": "error", "kind": "value", "text": "Could not read message, please send again"}')
                self.errorSend = True
            return
        if (not size):
            self.connection = None
            self.inMessages.put('{"type": "disconnect"}')
            print "Disconnected"
            return

        try:
            data = self.connection.recv(100)
            while (len(data) < size):
                msgTest = self.connection.recv(size - len(data))
                data += msgTest
        except IOError as e:  # and here it is handeled
            if e.errno == errno.EWOULDBLOCK:
                # Waiting for new message
                return
            print "Other problem with connection: closed"
            self.connection = None
        self.inMessages.put(data)


    def sendMessage(self):
        if (self.connection and not self.outMessages.empty()):
            message = self.outMessages.get_nowait().encode("UTF-8") + chr(0)
            size = len(message)
            sendString = str(size).zfill(8) + message
            self.connection.send(sendString)


IP = "localhost"
thread = networkThread()

# Proxies
tts = ALProxy("ALTextToSpeech", IP, 9559)
motionProxy = ALProxy("ALMotion", IP, 9559)
postureProxy = ALProxy("ALRobotPosture", IP, 9559)
camProxy = ALProxy("ALVideoDevice", IP, 9559)
batteryProxy = ALProxy("ALBattery", IP, 9559)
systemProxy = ALProxy("ALSystem", IP, 9559)

def toJson(data):
    try:
        return json.loads(data)
    except:
        print "No valid json"
        return {"type": "error", "kind": "json", "text": "Could not read message, please send again"}


def onExit():
    motionProxy.rest()
    print "Exiting"
    if thread:
        thread.shutdown = True


def stiffness(part, value):
    if value == 0.0:
        motionProxy.rest()
    motionProxy.setStiffnesses(part, value)


def getInfo():
    name = systemProxy.robotName()
    battery = batteryProxy.getBatteryCharge()
    stiffness = "false"
    if all(i > 0.5 for i in motionProxy.getStiffnesses("Body")):
        stiffness = "true"
    thread.outMessages.put('{"type": "info", "name": "' + name +
                           '", "battery": "' + str(battery) + '", "stiffness": "'
                            + stiffness + '"}')


def speak(data):
    tts.setVolume(float(data["volume"])/100)
    pitch = float(data["pitch"]) / 100
    if pitch > 0.0 and pitch < 1.0: pitch = 1.0
    tts.setParameter("pitchShift", pitch)
    tts.say("\\rspd=" + str(data["speed"]) + "\\" + str(data["text"]))


def walk(data):
    xSpeed = float(data["x_speed"])
    ySpeed = float(data["y_speed"])
    thetaSpeed = float(data["theta_speed"])
    if (xSpeed == 0 and ySpeed == 0 and thetaSpeed == 0):
        motionProxy.stopMove()
        postureProxy.goToPosture("StandInit", 0.5)
    else:
        postureProxy.goToPosture("StandInit", 0.5)
        motionProxy.moveToward(xSpeed, ySpeed, thetaSpeed)


def takePicture():
    resolution = vision_definitions.kQVGA
    colorSpace = vision_definitions.kRGBColorSpace
    fps = 20

    nameId = camProxy.subscribe("camera", resolution, colorSpace, fps)

    naoImage = camProxy.getImageRemote(nameId)
    camProxy.unsubscribe(nameId)


    if not naoImage:
        thread.outMessages.put('{"type": "error", "kind": "picture", "text": "Could not take picture"}')
        return

    tts.say("Say \\rspd=70\\cheese")

    # Get the image size and pixel array.
    imageWidth = naoImage[0]
    imageHeight = naoImage[1]
    array = naoImage[6]

    # Create a PIL Image from our pixel array.
    im = Image.fromstring("RGB", (imageWidth, imageHeight), array)

    # Save the image.
    im.save("camImage.png", "PNG")

    with open("camImage.png", "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
    image_file.close()
    thread.outMessages.put('{"type": "picture", "img": "' + encoded_string + '"}')


def moves(data):
    code = data["file"]
    if code == "stand":
        postureProxy.goToPosture("StandInit", 0.5)
    elif code == "sit":
        motionProxy.rest()
    else:
        try:
            exec(code)
            if names:
                motionProxy.angleInterpolationBezier(names, times, keys)
            postureProxy.goToPosture("StandInit", 0.5)
        except:
            thread.outMessages.put('{"type": "error", "kind": "move", "text": "Could not perform move, try again"}')


def handleMessages():
    if thread.inMessages.empty():
        return

    data = toJson(thread.inMessages.get())

    dataType = data["type"]
    if dataType == "connect":
        tts.say("Oh")
    elif dataType == "stiffness":
        stiffness(str(data["part"]), float(data["value"]))
    elif dataType == "info":
        getInfo()
    elif dataType == "speak":
        speak(data)
    elif dataType == "walk":
        walk(data)
    elif dataType == "picture":
        takePicture()
    elif dataType == "moves":
        moves(data)
    elif dataType == "disconnect":
        tts.say("ByeBye")
        motionProxy.rest()


def main():
    atexit.register(onExit)
    thread.daemon = True
    thread.start()
    while (True):
        handleMessages()


if __name__ == "__main__":
    main()
