@echo off
title L2J-prime: Game Server Restart
echo L2J-prime: Game Server Restart
echo ATTENTION: It needs XMLRPC Server Enabled in Powerpak in order to work

REM -------------------------------------
REM Default parameters for a basic server.
java -Dfile.encoding=UTF8 -cp ./lib/*;l2jprime-core.jar com.l2jprime.gameserver.powerpak.xmlrpc.XMLRPCClient_ManagementTester
REM
