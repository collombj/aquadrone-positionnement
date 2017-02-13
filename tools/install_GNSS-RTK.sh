#!/bin/bash

wifiName="Slave"
wifiPassword="emlidreach"
interfaceWln="wlan0"
interfaceEth="eth0"
ipRTKSlave="192.168.42.1"
ipRTKMaster="192.168.2.1"
portSynchroRTK="9000"
portExportRTK="9001"



#######################################################

# Vérifie si le script est exécuté en Root
if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

# Récupération du SSID
wifiSSID=`sudo iwlist wlan0 scan | grep -e "ESSID" | grep "${wifiName}" | cut -d "\"" -f2`

# Ajout de la configuration sur le Raspberry
cat >> /etc/wpa_supplicant/wpa_supplicant.conf << EOL
network={
    ssid="${wifiSSID}"
    psk="${wifiPassword}"
}
EOL

# Activation de la nouvelle configuration sur wlan0
ifdown $interfaceWln > /dev/null 2>&1
ifup $interfaceWln > /dev/null 2>&1
dhclient -v $interfaceWln > /dev/null 2>&1

# Activation du port forwarding
echo '1' | tee /proc/sys/net/ipv4/conf/$interfaceWln/forwarding > /dev/null 2>&1
echo '1' | tee /proc/sys/net/ipv4/conf/$interfaceEth/forwarding > /dev/null 2>&1

iptables -F
iptables -A PREROUTING -t nat -i $interfaceWln -p tcp --dport $portSynchroRTK -j DNAT --to $ipRTKMaster:$portSynchroRTK
iptables -A PREROUTING -t nat -i $interfaceEth -p tcp --dport $portExportRTK -j DNAT --to $ipRTK:$portExportRTK
iptables-save
