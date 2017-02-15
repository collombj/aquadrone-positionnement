# coding=utf-8
import socket
import sys
from datetime import datetime
import mavlink10 as mavlink


class Fifo(object):
    def __init__(self):
        self.buf = []

    def write(self, data):
        self.buf += data
        return len(data)

    def read(self):
        return self.buf.pop(0)


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


def parse_llh(msg, mav):
    """
    Méthode permettant de parser le flux LLH envoyé par le GNSS/RTK.
    Les champs intéréssants sont (séparation : 3 espaces [BLANK])
        - 1 : Latitude
        - 2 : Longitude
        - 3 : Altitude
        - 4 : Type
        - 5 : Nombre de satelittes
    :param mav: MAVLink pour l'envoie des messages
    :param msg: Message à parser
    """
    splits = msg.split("   ")  # séparation avec 3 espaces

    time = datetime.now().time().microsecond
    lat = float(splits[1])
    lon = float(splits[2])
    alt = float(splits[3])
    signal_type = get_type(splits[4])
    sat_nb = splits[5]

    mav_msg = mavlink.MAVLink_gps_raw_int_message(time, signal_type, lat, lon, alt, 0, 0, 0, 0, sat_nb)
    mav.send(mav_msg)


def init_socket_tcp(ip, port):
    """
    Méthode permettant d'instancier la connexion avec le serveur
    :param ip: IP du GPS
    :param port: Port de la connexion GPS
    :return: La socket TCP initialisée et connectée au GPS
    """
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((ip, int(port)))

    return sock


def init_socket_udp():
    """
    Méthode  initialisant la socket UDP pour l'envoie des messages MAVLink
    :return: la socket UDP prete pour l'envoie de données
    """
    cs = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    cs.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    cs.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

    return cs


def receiver(tcp, udp, llh_length):
    """
    Méthode recevant des informations sur le réseau
    :param tcp: Socket d'écoute
    :param udp: Socket permettant l'envoie des messages MAVLink
    :param llh_length: Longueur des messages LLH à recevoir
    """
    fifo = Fifo()
    mav = mavlink.MAVLink(fifo, 200)
    while True:
        print "Received"
        chunks = []
        bytes_recd = 0
        while bytes_recd < llh_length:
            chunk = tcp.recv(llh_length - bytes_recd)
            if chunk == b'':
                raise RuntimeError("socket connection closed")

            chunks.append(chunk)
            bytes_recd += len(chunk)

        print chunks
        print b''.join(chunks).decode("ASCII")

        parse_llh(b''.join(chunks).decode("ASCII"), mav)
        udp.sendto(fifo.read(), ("192.168.2.255", 14550))



####
# Début du Main
####
llh_length_static = 133

if len(sys.argv) != 3:
    print "[IP] [PORT] are required"
    exit(-1)

ip_arg = sys.argv[1]
port_arg = sys.argv[2]

while True:
    print "Init"
    try:
        st = init_socket_tcp(ip_arg, port_arg)
        su = init_socket_udp()
        receiver(st, su, llh_length_static)
    except Exception as e:
        print e
