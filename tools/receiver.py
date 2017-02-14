# coding=utf-8
import socket
import sys


def get_type(signal_type):
    """
    Méthode permettant de transformer le type de signal transmis par le GNSS/RTK. La valeur retournée correspond à la
    valeur du message MAVLink numéro 24 (GPS_RAW_INT)
    :param signal_type: valeur représentant le type de signal
    :return: Le type de signal mais pour le flux MAVLink
    """
    if signal_type == "1":
        return 6  # GPS_FIX_TYPE_RTK_FIXED
    elif signal_type == "2":
        return 5  # GPS_FIX_TYPE_RTK_FLOAT
    elif signal_type == "3" or signal_type == "4":
        return 4  # GPS_FIX_TYPE_DGPS
    elif signal_type == "5":
        return 3  # GPS_FIX_TYPE_3D_FIX
    else:
        return 1  # GPS_FIX_TYPE_NO_FIX


def parse_llh(msg):
    """
    Méthode permettant de parser le flux LLH envoyé par le GNSS/RTK.
    Les champs intéréssants sont (séparation : 3 espaces [BLANK])
        - 1 : Latitude
        - 2 : Longitude
        - 3 : Altitude
        - 4 : Type
        - 5 : Nombre de satelittes
    :param msg: Message à parser
    """
    splits = msg.split("   ")  # séparation avec 3 espaces

    lat = float(splits[1])
    lon = float(splits[2])
    alt = float(splits[3])
    signal_type = get_type(splits[4])
    sat_nb = splits[5]

    print "Lat: " + splits[1]
    print "Lon: " + splits[2]
    print "Alt: " + splits[3]
    print "Type: " + str(get_type(splits[4]))
    print "Sat nb: " + splits[5]
    print "#############################################"


def init_socket(ip, port):
    """
    Méthode permettant d'instancier la connexion avec le serveur
    :param ip: IP du GPS
    :param port: Port de la connexion GPS
    :return:
    """
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((ip, int(port)))

    return sock


def receiver(sock, llh_length):
    """
    Méthode recevant des i nformations sur le réseau
    :param sock: Socket d'écoute
    :param llh_length: Longueur des messages LLH à recevoir
    """
    while True:
        chunks = []
        bytes_recd = 0
        while bytes_recd < llh_length:
            chunk = sock.recv(llh_length - bytes_recd)
            if chunk == b'':
                raise RuntimeError("socket connection closed")

            chunks.append(chunk)
            bytes_recd += len(chunk)

        print chunks
        print b''.join(chunks).decode("ASCII")

        parse_llh(b''.join(chunks).decode("ASCII"))

####
# Début du Main
####
llh_length = 133

if len(sys.argv) != 2:
    print "[IP] [PORT] are required"
    exit(-1)

ip = sys.argv[0]
port = sys.argv[1]

while True:
    try:
        s = init_socket(ip, port)
        receiver(s, llh_length)
    except Exception as e:
        print e
