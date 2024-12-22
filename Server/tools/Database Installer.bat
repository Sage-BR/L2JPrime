@echo off

REM ###############################################
REM ## Configurate Database Connections please!  ##
REM ###############################################
echo Checking environment...
mysql --help >nul 2>nul
if errorlevel 1 goto nomysql
echo   - MySQL: OK

set DateT=%date%

REM Configurate database connection loginserver
set lsuser=root
set lspass=root
set lsdb=l2jdb
set lshost=localhost

REM Configurate database connection Gameserver
set gsuser=root
set gspass=root
set gsdb=l2jdb
set gshost=localhost
REM ############################################

:Step1
cls
echo. ---------------------------------------------------------------------
echo.
echo.   L2-Prime Team - Database Login Server
echo. _____________________________________________________________________
echo.
echo.   1 - Full install database loginserver`s.
echo.   2 - Skip install loginserver db, go to install gameserver databases
echo.   3 - Exit from installer
echo. ---------------------------------------------------------------------

set Step1prompt=x
set /p Step1prompt= Please enter values :
if /i %Step1prompt%==1 goto LoginInstall
if /i %Step1prompt%==2 goto Step2
if /i %Step1prompt%==3 goto fullend
goto Step1


:LoginInstall

echo Clear database : %lsdb% and install database loginserver`s.
echo.
mysql -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < login_install.sql
echo Update table accounts.sql
mysql -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../sql/accounts.sql
echo Update table gameservers.sql
mysql -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../sql/gameservers.sql
echo.
echo Database login server has been installed with no-errors!!
pause
goto :Step2

:Step2

echo. ---------------------------------------------------------------------
echo.
echo.   L2-Prime Team - database operation about gameserver
echo. _____________________________________________________________________
echo.
echo.   1 - Full Install Gameserver Database`s.
echo.   2 - Customs NPCs Install
echo.   3 - Customs NPCs Spawn Install
echo.   4 - Customs Items Install
echo.   5 - Exit.
echo. ---------------------------------------------------------------------

set Step2prompt=x
set /p Step2prompt= Please, put value:
if /i %Step2prompt%==1 goto fullinstall
if /i %Step2prompt%==2 goto addnpcs
if /i %Step2prompt%==3 goto addspawns
if /i %Step2prompt%==4 goto additems
if /i %Step2prompt%==5 goto fullend
goto Step2

:fullinstall

echo Drop and clean old gamserver database`s.
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < full_install.sql
set title=installed
goto CreateTables

:CreateTables

echo Now be %title%ed database gameserver`s.
pause

echo *** Sucesfull 1 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/access_levels.sql

echo *** Sucesfull 2 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/armor.sql

echo *** Sucesfull 3 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/armorsets.sql

echo *** Sucesfull 4 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auction.sql

echo *** Sucesfull 5 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auction_bid.sql

echo *** Sucesfull 6 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auction_watch.sql

echo *** Sucesfull 7 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/augmentations.sql

echo *** Sucesfull 8 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auto_announcements.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auto_chat.sql

echo *** Sucesfull 9 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auto_chat_text.sql

echo *** Sucesfull 10 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/boxaccess.sql

echo *** Sucesfull 11 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/boxes.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/buff_templates.sql

echo *** Sucesfull 12 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle.sql

echo *** Sucesfull 13 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_door.sql

echo *** Sucesfull 14 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_doorupgrade.sql

echo *** Sucesfull 15 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_siege_guards.sql

echo *** Sucesfull 16 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/char_templates.sql

echo *** Sucesfull 17 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_friends.sql

echo *** Sucesfull 18 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_hennas.sql

echo *** Sucesfull 19 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_macroses.sql

echo *** Sucesfull 20 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_quests.sql

echo *** Sucesfull 21 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_raid_points.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_recipebook.sql

echo *** Sucesfull 22 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_recommends.sql

echo *** Sucesfull 23 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_shortcuts.sql

echo *** Sucesfull 24 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_skills.sql

echo *** Sucesfull 25 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_skills_save.sql

echo *** Sucesfull 26 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_subclasses.sql

echo *** Sucesfull 27 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/characters.sql

echo *** Sucesfull 28 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_data.sql

echo *** Sucesfull 29 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_privs.sql

echo *** Sucesfull 30 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_skills.sql

echo *** Sucesfull 31 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_subpledges.sql

echo *** Sucesfull 32 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_wars.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clan_notices.sql

echo *** Sucesfull 33 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clanhall.sql

echo *** Sucesfull 34 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clanhall_functions.sql

echo *** Sucesfull 35 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/class_list.sql

echo *** Sucesfull 36 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/cursed_weapons.sql

echo *** Sucesfull 37 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/dimensional_rift.sql

echo *** Sucesfull 38 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/droplist.sql

echo *** Sucesfull 39 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/enchant_skill_trees.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/engrave.sql

echo *** Sucesfull 40 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/etcitem.sql

echo *** Sucesfull 41 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fish.sql

echo *** Sucesfull 42 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fishing_skill_trees.sql

echo *** Sucesfull 43 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/forums.sql

echo *** Sucesfull 44 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/games.sql

echo *** Sucesfull 45 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/global_tasks.sql

echo *** Sucesfull 46 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/grandboss_data.sql

echo *** Sucesfull 47 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/grandboss_list.sql

echo *** Sucesfull 48 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/helper_buff_list.sql

echo *** Sucesfull 49 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/henna_trees.sql

echo *** Sucesfull 50 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/heroes.sql

echo *** Sucesfull 51 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/items.sql

echo *** Sucesfull 52 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/itemsonground.sql

echo *** Sucesfull 53 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/locations.sql

echo *** Sucesfull 54 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/lvlupgain.sql

echo *** Sucesfull 55 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_areas_list.sql

echo *** Sucesfull 56 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_buylists.sql

echo *** Sucesfull 57 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_lease.sql

echo *** Sucesfull 58 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_shopids.sql

echo *** Sucesfull 59 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchants.sql

echo *** Sucesfull 60 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/minions.sql

echo *** Sucesfull 61 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/mods_wedding.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/mods_buffer.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_offline_trade.sql

echo *** Sucesfull 62 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/npc.sql

echo *** Sucesfull 63 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/npcskills.sql

echo *** Sucesfull 64 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/olympiad_nobles.sql

echo *** Sucesfull 65 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pets.sql

echo *** Sucesfull 66 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pets_stats.sql

echo *** Sucesfull 67 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pledge_skill_trees.sql

echo *** Sucesfull 68 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/posts.sql

echo *** Sucesfull 69 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/raidboss_spawnlist.sql

echo *** Sucesfull 70 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/random_spawn.sql

echo *** Sucesfull 71 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/random_spawn_loc.sql

echo *** Sucesfull 72 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/seven_signs.sql

echo *** Sucesfull 73 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/seven_signs_festival.sql

echo *** Sucesfull 74 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/seven_signs_status.sql

echo *** Sucesfull 75 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/siege_clans.sql

echo *** Sucesfull 76 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_learn.sql

echo *** Sucesfull 77 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_spellbooks.sql

echo *** Sucesfull 78 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_trees.sql

echo *** Sucesfull 79 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/spawnlist.sql

echo *** Sucesfull 80 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/teleport.sql

echo *** Sucesfull 81 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/topic.sql

echo *** Sucesfull 83 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/weapon.sql

echo *** Sucesfull 84 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/zone_vertices.sql

echo *** Sucesfull 85 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/quest_global_data.sql

echo *** Sucesfull 87 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_manor_procure.sql

echo *** Sucesfull 88 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_manor_production.sql

echo *** Sucesfull 89 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_weapon.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_npc.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_teleport.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_armor.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_armorsets.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_contestable_clanhalls.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_merchant_shopids.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_droplist.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_merchant_buylists.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_etcitem.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_notspawned.sql

echo *** Sucesfull 90 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/rebirth_manager.sql

echo *** Sucesfull 91 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/remove_unneeded_spawns.sql

echo *** Sucesfull 92 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/admin_command_access_rights.sql

echo *** Sucesfull 93 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pkkills.sql

echo *** Sucesfull 94 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/characters_custom_data.sql

echo *** Sucesfull 95 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/tvt.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/tvt_teams.sql

echo *** Sucesfull 97 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/character_recipebook.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fort_door.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fort_doorupgrade.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fort_siege_guards.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fort.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fortsiege_clans.sql

echo *** Sucesfull 99 percents. ***
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/four_sepulchers_spawnlist.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/custom_spawnlist.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/vanhalter_spawnlist.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/clanhall_siege.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/paystream.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/smsonline.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/npc_to_pc_polymorph.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/vip.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/ctf.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/ctf_teams.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/dm.sql

echo *** Sucesfull 100 percents. **
echo.
echo GameServer Database %title%.
pause
goto :Step1

:addnpcs

echo.
echo Put in database custom NPCs...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/added_custom_merchant_buylist.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/added_custom_npc.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/added_custom_teleport.sql
echo custom NPCs loaded with no-error. Greet!!!
pause
:end
echo.
echo Installed Sucessfull.
echo.
pause
goto :Step1

:addspawns

echo.
echo Put in database custom NPCs Spawnlist...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/added_custom_spawnlist.sql
echo custom NPCs Spawnlist loaded with no-error. Greet!!!
pause
:end
echo.
echo Installed Sucessfull.
echo.
pause
goto :Step1

:additems

echo.
echo Put in database spawn GmShop, Classmaster and other customs...
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/added_custom_etcitem.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/DynastyArmor_SQL_OK.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/Epic_MaskAndShield_SQL_OK.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/Gold_Bar_Item_OK.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/Mordor_Weapons_SQL_OK.sql
mysql -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/customs/Wings_SQL_OK.sql
echo GmShop and other customs loaded with no-error. Greet!!!
pause
:end
echo.
echo Installed Sucessfull.
echo.
pause
goto :Step1

:end
echo.
echo Installing sucessfull.
echo.
pause

:fullend
