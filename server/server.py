#!/usr/bin/python

import socket
import sys
import atexit

HOST = ''   # Symbolic name, meaning all available interfaces
PORT = 3006 # Arbitrary non-privileged port
MAX_CLIENTS = 5
BUFFER_SIZE = 100

def createServer():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    atexit.register(lambda: onExit(s))
    print 'Socket created'

    #Bind socket to local host and port
    try:
        s.bind((HOST, PORT))
    except socket.error as msg:
        print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
        sys.exit()

    print 'Socket bind complete'

    #Start listening on socket
    s.listen(MAX_CLIENTS)
    print 'Socket now listening'

    while (1):
        conn, addr = s.accept()
        print 'Connected with ' + addr[0] + ':' + str(addr[1])
        handleConnection(conn)


def handleConnection(connection):
    while (1):
        data = connection.recv(BUFFER_SIZE)[2:]
        if not data: break
        print "Data: ", data
        try :
            connection.send("Data received \0")
        except socket.error:
            print 'Send failed'
    connection.close()


def onExit(s):
    closeConnection(s)


def closeConnection(s):
    print("Closing connection")
    s.close()


def main():
    server = createServer()


if __name__ == "__main__":
    main()
