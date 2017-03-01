#!/bin/bash

interfaceWln="wlan0"
interfaceEth="eth0"
ipRTKSlave="192.168.42.1"
ipRTKMaster="192.168.2.1"
portSynchroRTK="9000"
portExportRTK="9001"

# Attente du démarrage du Reach
sleep 240

# Activation de la nouvelle configuration sur wlan0
ifdown $interfaceWln > /dev/null 2>&1
ifup $interfaceWln > /dev/null 2>&1
dhclient -v $interfaceWln > /dev/null 2>&1

# Activation du port forwarding
echo '1' | tee /proc/sys/net/ipv4/conf/$interfaceWln/forwarding > /dev/null 2>&1
echo '1' | tee /proc/sys/net/ipv4/conf/$interfaceEth/forwarding > /dev/null 2>&1

# Mise en place du port forwarding
iptables -t nat -F > /dev/null 2>&1
iptables --table nat --append POSTROUTING --out-interface $interfaceEth -j MASQUERADE > /dev/null 2>&1
iptables --table nat --append POSTROUTING --out-interface $interfaceWln -j MASQUERADE > /dev/null 2>&1
iptables -A PREROUTING -t nat -i $interfaceWln -p tcp --dport $portSynchroRTK -j DNAT --to $ipRTKMaster:$portSynchroRTK > /dev/null 2>&1
iptables -A PREROUTING -t nat -i $interfaceEth -p tcp --dport $portExportRTK -j DNAT --to $ipRTKSlave:$portExportRTK > /dev/null 2>&1


python /home/pi/GNSS/receiver.py
