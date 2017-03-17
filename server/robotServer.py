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

"""
Threading class. Handles the network between the
robot and the app.
"""


class NetworkThread (threading.Thread):
    """
    Constructor, set all variables.
    """
    def __init__(self):
        threading.Thread.__init__(self)
        self.shutdown = False
        self.serverSocket = None
        self.connection = None
        self.inMessages = Queue.Queue()
        self.outMessages = Queue.Queue()
        self.PORT = 3006
        self.errorSend = False

    """
    Start the thread. Create a server and send and
    receive message from the app.
    """
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

    """
    Create a server.
    """
    def createServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF,
                                     99999999)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print 'Socket created'

        # Bind socket to local host and port
        try:
            self.serverSocket.bind(("", self.PORT))
        except socket.error as msg:
            print 'Bind failed. Error: ' + str(msg[0]) + ' Message ' + msg[1]
            sys.exit()

        print 'Socket bind complete'

        # Start listening on socket
        self.serverSocket.listen(1)
        print 'Socket now listening'

    """
    Receive messages from the app.
    """
    def receiveMessages(self):
        # Receive size of message
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
        except ValueError:
            if not self.errorSend:
                msg = '{"type": "error", "kind": "value", "text": \
                        "Could not read message, please send again"}'
                self.outMessages.put(msg)
                self.errorSend = True
            return
        if (not size):
            self.connection = None
            self.inMessages.put('{"type": "disconnect"}')
            print "Disconnected"
            return

        # Receive message
        try:
            data = self.connection.recv(100)
            while (len(data) < size):
                data += self.connection.recv(size - len(data))
        except IOError as e:
            if e.errno == errno.EWOULDBLOCK:
                # Waiting for new message
                return
            print "Problem with connection: closed"
            self.connection = None
            self.inMessages.put('{"type": "disconnect"}')
            print "Disconnected"
        self.inMessages.put(data)

    """
    Send the messages from the outMessages queue to the app.
    """
    def sendMessage(self):
        if (self.connection and not self.outMessages.empty()):
            message = self.outMessages.get_nowait().encode("UTF-8") + chr(0)
            size = len(message)
            sendString = str(size).zfill(8) + message
            self.connection.send(sendString)


"""
Robot class. Handles the messages from the app by
letting the robot do what asked.
"""


class Robot():
    """
    Constructor, set all variables.
    """
    def __init__(self):
        # Proxies
        self.IP = "localhost"
        self.tts = ALProxy("ALTextToSpeech", self.IP, 9559)
        self.motionProxy = ALProxy("ALMotion", self.IP, 9559)
        self.postureProxy = ALProxy("ALRobotPosture", self.IP, 9559)
        self.camProxy = ALProxy("ALVideoDevice", self.IP, 9559)
        self.batteryProxy = ALProxy("ALBattery", self.IP, 9559)
        self.systemProxy = ALProxy("ALSystem", self.IP, 9559)

        self.thread = NetworkThread()
        self.thread.daemon = True
        self.thread.start()
        while (True):
            self.handleMessages()

    """
    Convert a string to JSON.
    """
    def toJson(self, data):
        try:
            return json.loads(data)
        except:
            print "No valid json"
            return {"type": "error", "kind": "json", "text":
                    "Could not read message, please send again"}

    """
    Stop all connections and let the robot rest before
    exiting the program.
    """
    def stopProgram(self):
        self.motionProxy.rest()
        if self.thread:
            self.thread.shutdown = True

    """
    Set the stiffness to the given value.
    """
    def stiffness(self, part, value):
        if value == 0.0:
            self.motionProxy.rest()
        self.motionProxy.setStiffnesses(part, value)

    """
    Get the robots info: name, battery and stiffness status
    and add it to the threads outMessages queue.
    """
    def getInfo(self):
        name = self.systemProxy.robotName()
        battery = self.batteryProxy.getBatteryCharge()
        stiffness = "false"
        if all(i > 0.5 for i in self.motionProxy.getStiffnesses("Body")):
            stiffness = "true"
        self.thread.outMessages.put('{"type": "info", "name": "' + name +
                                    '", "battery": "' + str(battery) +
                                    '", "stiffness": "' + stiffness + '"}')

    """
    Let the robot speak the text with the given settings.
    """
    def speak(self, data):
        self.tts.setVolume(float(data["volume"])/100)
        pitch = float(data["pitch"]) / 100
        if pitch > 0.0 and pitch < 1.0:
            pitch = 1.0
        self.tts.setParameter("pitchShift", pitch)
        self.tts.say("\\rspd=" + str(data["speed"]) + "\\" + str(data["text"]))

    """
    Let the robot walk in the given direction with the
    given speed.
    """
    def walk(self, data):
        xSpeed = float(data["x_speed"])
        ySpeed = float(data["y_speed"])
        thetaSpeed = float(data["theta_speed"])
        if (xSpeed == 0 and ySpeed == 0 and thetaSpeed == 0):
            self.motionProxy.stopMove()
            self.postureProxy.goToPosture("StandInit", 0.5)
        else:
            self.postureProxy.goToPosture("StandInit", 0.5)
            self.motionProxy.moveToward(xSpeed, ySpeed, thetaSpeed)

    """
    Let the robot take a picture and add it to the
    threads outMessages queue.
    """
    def takePicture(self):
        resolution = vision_definitions.kQVGA
        colorSpace = vision_definitions.kRGBColorSpace
        fps = 20

        nameId = self.camProxy.subscribe("camera", resolution, colorSpace, fps)

        naoImage = self.camProxy.getImageRemote(nameId)
        self.camProxy.unsubscribe(nameId)

        if not naoImage:
            self.thread.outMessages.put('{"type": "error", "kind": "picture", \
                                          "text": "Could not take picture"}')
            return

        self.tts.say("Say \\rspd=70\\cheese")

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
        self.thread.outMessages.put('{"type": "picture", "img": "' +
                                    encoded_string + '"}')

    """
    Let the robot execute the given move file.
    """
    def moves(self, data):
        code = data["name"]
        if code == "stand":
            self.postureProxy.goToPosture("StandInit", 0.5)
        elif code == "sit":
            self.motionProxy.rest()
        else:
            try:
                self.postureProxy.goToPosture("StandInit", 0.5)
                with open(data["name"], 'r') as myfile:
                    code = myfile.read()
                exec(code)
                if names:
                    self.motionProxy.angleInterpolationBezier(names, times,
                                                              keys)
                self.postureProxy.goToPosture("StandInit", 0.5)
            except:
                msg = '{"type": "error", "kind": "move", \
                        "text": "Could not perform move, try again"}'
                self.thread.outMessages.put(msg)

    """
    Handle the messages received from the app.
    """
    def handleMessages(self):
        if self.thread.inMessages.empty():
            return

        data = self.toJson(self.thread.inMessages.get())

        dataType = data["type"]
        if dataType == "connect":
            self.tts.say("Oh")
        elif dataType == "stiffness":
            self.stiffness(str(data["part"]), float(data["value"]))
        elif dataType == "info":
            self.getInfo()
        elif dataType == "speak":
            self.speak(data)
        elif dataType == "walk":
            self.walk(data)
        elif dataType == "picture":
            self.takePicture()
        elif dataType == "moves":
            self.moves(data)
        elif dataType == "disconnect":
            self.tts.say("ByeBye")
            self.motionProxy.rest()

"""
Main program.
"""


# Robot object
robot = Robot()


def onExit():
    print "Exiting"
    robot.stopProgram()


def main():
    atexit.register(onExit)

if __name__ == "__main__":
    main()
