#!/bin/bash

wifiName="Slave"
wifiPassword="emlidreach"



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

# Add auto launch
cmd='sudo screen -dm -S GNSS /home/pi/GNSS/worker.sh'
sudo sed -i -e "\%$cmd%d" \
-e "0,/^[^#]*exit 0/s%%$cmd\n&%" \
/etc/rc.local

chmod +x /home/pi/GNSS/starter.sh
chmod +x /home/pi/GNSS/worker.sh
