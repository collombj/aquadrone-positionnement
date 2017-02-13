# coding=utf-8
import binascii
import socket


def getType(type):
    if type == "1":
        return 6  # GPS_FIX_TYPE_RTK_FIXED
    elif type == "2":
        return 5  # GPS_FIX_TYPE_RTK_FLOAT
    elif type == "3" or type == "4":
        return 4  # GPS_FIX_TYPE_DGPS
    elif type == "5":
        return 3  # GPS_FIX_TYPE_3D_FIX
    else:
        return 1  # GPS_FIX_TYPE_NO_FIX


def printLLH(msg):
    splits = msg.split("   ")  # séparation avec 3 espaces

    print "Lat: " + splits[1]
    print "Lon: " + splits[2]
    print "Alt: " + splits[3]
    print "Type: " + str(getType(splits[4]))
    print "Sat nb: " + splits[5]
    print "#############################################"


llh_length = 133

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    s.connect(("192.168.42.1", 9001))
except Exception as e:
    print e
    exit(-1)

while True:
    chunks = []
    bytes_recd = 0
    while bytes_recd < llh_length:
        chunk = s.recv(llh_length - bytes_recd)
        if chunk == b'':
            raise RuntimeError("socket connection closed")

        chunks.append(chunk)
        bytes_recd += len(chunk)

    print chunks
    print b''.join(chunks).decode("ASCII")

    printLLH(b''.join(chunks).decode("ASCII"))
