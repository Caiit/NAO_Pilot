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
        self.BUFFER_SIZE = 100000


    def run(self):
        self.createServer()
        while (not self.shutdown):
            if (not self.connection):
                self.connection, addr = self.serverSocket.accept()
                self.connection.setblocking(0)
                print 'Connected with ' + addr[0] + ':' + str(addr[1])
            #     # TODO: maak mooi geluidje
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
        # self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        # self.serverSocket.setblocking(0)
        # self.ready = select.select([self.serverSocket], [], [], 1)


    def receiveMessages(self):
        size = 0
        try:
            msg = self.connection.recv(10)[2:]
            if (not msg == ""):
                size = int(msg)
        except IOError as e:  # and here it is handeled
            if e.errno == errno.EWOULDBLOCK:
                # Waiting for new message
                return
            print "Other problem with connection: closed"
            self.connection = None
        if (not size):
            self.connection = None
            return

        print size
        try:
            data = self.connection.recv(size)
            while (len(data) < size):
                data += self.connection.recv(size - len(data))
        except Exception as e:
            raise

        print "data: ", data
        self.inMessages.put(data)
        # try :
        #     message = '{"type": "received", "text": "Data received"}'
        #
        #     self.outMessages.put(message)
        #     self.sendMessage()
        # except socket.error:
        #     print 'Send failed'

        # Make sure message is correct json message
        # self.buffer += data
        # if ('}' not in self.buffer): return
        # begin, end, self.buffer = self.buffer.partition('}')
        # message = begin + end
        # self.inMessages.put(message)


    def sendMessage(self):
        if (self.connection and not self.outMessages.empty()):
            message = self.outMessages.get_nowait().encode("UTF-8") + chr(0)
            # message = 50000 * "abcdefg" + "h" + chr(0)
            size = len(message)
            sendString = str(size).zfill(8) + message
            # print "0's in message: ", message.count(chr(0))
            # print "bytes: ", str(size).zfill(8)
            self.connection.send(sendString)
        # if (not self.outMessages.empty()):
        #     try:
        #         self.serverSocket.send(self.outMessages.get_nowait())
        #     except socket.error:
        #         # no connection yet
        #         return


IP = "localhost"
HOST = ''   # Symbolic name, meaning all available interfaces
PORT = 3006 # Arbitrary non-privileged port
MAX_CLIENTS = 5
BUFFER_SIZE = 1024

thread = networkThread()
send = "status"

# Proxies
tts = ALProxy("ALTextToSpeech", IP, 9559)
motionProxy = ALProxy("ALMotion", IP, 9559)
postureProxy = ALProxy("ALRobotPosture", IP, 9559)
camProxy = ALProxy("ALVideoDevice", IP, 9559)

def toJson(data):
    try:
        return json.loads(data)
    except:
        print "No valid json"
        return {"type": "error"}


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
    if value == 0.0:
        motionProxy.rest()
    motionProxy.setStiffnesses(part, value)


def speak(data):
    tts.setVolume(float(data["volume"])/100)
    pitch = float(data["pitch"]) / 100
    if pitch > 0.0 and pitch < 1.0: pitch = 1.0
    tts.setParameter("pitchShift", pitch)
    tts.say("\\rspd=" + str(data["speed"]) + "\\" + str(data["text"]))


def walk(data):
    print data
    xSpeed = float(data["x_speed"])
    ySpeed = float(data["y_speed"])
    thetaSpeed = float(data["theta_speed"])
    if (xSpeed == 0 and ySpeed == 0 and thetaSpeed == 0):
        motionProxy.stopMove()
        postureProxy.goToPosture("StandInit", 0.5)
    else:
        if motionProxy.getStiffnesses("Body") < 0.8:
            stiffness("Body", 1.0)
        postureProxy.goToPosture("StandInit", 0.5)
        motionProxy.moveToward(xSpeed, ySpeed, thetaSpeed)


# TODO: can be deleted if walk works
def turn(data):
    print data
    if motionProxy.getStiffnesses("Body") < 0.8:
        stiffness("Body", 1.0)
    postureProxy.goToPosture("StandInit", 0.5)
    motionProxy.moveToward(0, 0, float(data["speed"]))


def takePicture():
    resolution = vision_definitions.kQVGA
    colorSpace = vision_definitions.kRGBColorSpace
    fps = 20

    nameId = camProxy.subscribe("camera", resolution, colorSpace, fps)

    print 'getting images in remote'
    naoImage = camProxy.getImageRemote(nameId)
    camProxy.unsubscribe(nameId)

    if not naoImage: return

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
    try:
        exec(code)
        motionProxy.angleInterpolationBezier(names, times, keys)
    except:
        thread.outMessages.put('{"type": "error", "text": "could not perform move, try again"}')


def handleMessages():
    global send
    if thread.inMessages.empty():
        return

    data = toJson(thread.inMessages.get())

    dataType = data["type"]
    if dataType == "connect":
        print "Connecting: ", data["text"]
    elif dataType == "stiffness":
        stiffness(str(data["part"]), float(data["value"]))
    elif dataType == "speak":
        speak(data)
    elif dataType == "walk":
        walk(data)
    elif dataType == "turn":
        turn(data)
    elif dataType == "picture":
        if data["get"] == "true":
            send = "picture"
        else:
            send = "nothing"
    elif dataType == "moves":
        moves(data)
    elif dataType == "disconnect":
        print "Disconnecting"


def sendMessage():
    if send == "picture":
        takePicture()


def main():
    atexit.register(onExit)
    thread.daemon = True
    thread.start()
    while (True):
        handleMessages()
        sendMessage()


if __name__ == "__main__":
    main()
