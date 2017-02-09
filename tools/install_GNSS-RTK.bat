@echo off

netsh interface portproxy add v4tov4 listenport=9000 listenaddress=192.168.2.1 connectport=9000 connectaddress=192.168.42.1
