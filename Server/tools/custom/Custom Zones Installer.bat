@echo off

REM ###############################################
REM ## Configurate Database Connections please!  ##
REM ###############################################
echo Checking environment...
mysql --help >nul 2>nul
if errorlevel 1 goto nomysql
echo   - MySQL: OK

set DateT=%date%

REM Configurate database connection Gameserver
set gsuser=root
set gspass=root
set gsdb=l2jdb
set gshost=localhost
REM ############################################

:Step1
echo. ---------------------------------------------------------------------
echo.
echo.   L2-Prime Team - Custom Zones Installer 
echo. _____________________________________________________________________
echo.
echo.   1 - Install Monastery Custom Zone (Powered Mobs (lvl85), Gold Tokens Spoil (10% just on all mobs), TOP76LS and HIGH76LS on few mobs spoil (15%), High EXP/Adena(4x) drop)
echo.   2 - (Not yet) Install ACM Custom Zone (Powered Mobs (lvl80-85), Prime Adena Drop (1/4*AdenaDrop just on Higher mobs), High EXP/Adena drop)
echo.   3 - Install Imperial Tomb Custom Zone (Powered Mobs (lvl85-90) with Strong Mobs, Gold Tokens Spoil , High EXP/Adena drop)
echo.   4 - (Not yet) Install Every Custom Zone (1,2,3)
echo.   5 - Revert to retail
echo.   6 - Exit from installer
echo. ---------------------------------------------------------------------

set Step1prompt=x
set /p Step1prompt= Please enter values :
if /i %Step1prompt%==1 goto MonasteryInstall
REM  if /i %Step1prompt%==2 goto ACMInstall
if /i %Step1prompt%==3 goto ImperialTombInstall
REM  if /i %Step1prompt%==4 goto FullInstall
if /i %Step1prompt%==5 goto Revert
if /i %Step1prompt%==6 goto exit
goto Step1



:MonasteryInstall

echo *** Installing Monastery Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/monastery_custom_zone.sql
echo *** Done ***

goto Step1



:ACMInstall

echo *** Installing ACM Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/acm_custom_zone.sql
echo *** Done ***

goto Step1



:ImperialTombInstall

echo *** Installing Imperial Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/imperial_custom_zone.sql
echo *** Done ***

goto Step1



:FullInstall

echo *** Installing Monastery Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/monastery_custom_zone.sql
echo *** Done ***

echo *** Installing ACM Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/acm_custom_zone.sql
echo *** Done ***

echo *** Installing Imperial Custom Zone ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/imperial_custom_zone.sql
echo *** Done ***

goto Step1



:Revert

echo *** Reverting Custom Zones to retail ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../../sql/custom_zones/restore_retail_drops_npcs.sql
echo *** Done ***

goto Step1


:exit
