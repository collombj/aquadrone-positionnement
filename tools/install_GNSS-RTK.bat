@echo off

netsh interface portproxy add v4tov4 listenport=9000 listenaddress=192.168.2.1 connectport=9000 connectaddress=192.168.42.1 protocol=tcp
netsh advfirewall firewall add rule name="9000-RTK_Sync" dir=in action=allow localport=9000 protocol=TCP remoteip=any profile=any
netsh advfirewall firewall add rule name="9000-RTK_Sync" dir=out action=allow localport=9000 protocol=TCP remoteip=any profile=any
