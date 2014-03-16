@echo off
title Smart Pad
java -Xms128m -Xmx768m -XX:MaxPermSize=384m -cp src\main\resources\smart-pad.yaml -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y -jar target\app-server-0.0.1-SNAPSHOT.jar server src\main\resources\smart-pad.yaml