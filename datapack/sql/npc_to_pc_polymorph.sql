-- ---------------------------
-- Table structure for `npc_to_pc_polymorph`
-- ---------------------------
CREATE TABLE IF NOT EXISTS `npc_to_pc_polymorph` (
  `spawn` int(9) NOT NULL default '0',
  `template` int(9) NOT NULL default '0',
  `name` varchar(35) default NULL,
  `title` varchar(35) default NULL,
  `class_id` int(3) NOT NULL default '0',
  `female` int(1) NOT NULL default '0',
  `hair_style` int(1) NOT NULL default '0',
  `hair_color` int(1) NOT NULL default '0',
  `face` int(1) NOT NULL default '0',
  `name_color` int(7) NOT NULL default '0',
  `title_color` int(7) NOT NULL default '0',
  `noble` int(1) NOT NULL default '0',
  `hero` int(1) NOT NULL default '0',
  `pvp` int(1) NOT NULL default '0',
  `karma` int(7) NOT NULL default '0',
  `wpn_enchant` int(7) NOT NULL default '0',
  `right_hand` int(7) NOT NULL default '0',
  `left_hand` int(7) NOT NULL default '0',
  `gloves` int(7) NOT NULL default '0',
  `chest` int(7) NOT NULL default '0',
  `legs` int(7) NOT NULL default '0',
  `feet` int(7) NOT NULL default '0',
  `hair` int(7) NOT NULL default '0',
  `hair2` int(7) NOT NULL default '0',
  `pledge` int(2) NOT NULL default '0',
  `cw_level` int(3) NOT NULL default '0',
  `clan_id` int(9) NOT NULL default '0',
  `ally_id` int(9) NOT NULL default '0',
  `clan_crest` int(9) NOT NULL default '0',
  `ally_crest` int(9) NOT NULL default '0',
  `rnd_class` int(1) NOT NULL default '0',
  `rnd_appearance` int(1) NOT NULL default '0',
  `rnd_weapon` int(1) NOT NULL default '0',
  `rnd_armor` int(1) NOT NULL default '0',
  `max_rnd_enchant` int(7) NOT NULL default '0',
  KEY `spawn` (`spawn`),
  KEY `template` (`template`)
) DEFAULT CHARSET=utf8;

INSERT INTO `npc_to_pc_polymorph` VALUES ('1', '50019', 'Shyla', 'Guardian Buffer', '104', '1', '2', '1', '2', '0', '0', '0', '0', '0', '0', '8', '6614', '0', '6380', '6379', '0', '6381', '9204', '0', '8', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `npc_to_pc_polymorph` VALUES ('1', '53', 'Kronus', 'GM SHOP', '114', '0', '2', '1', '2', '0', '0', '0', '0', '0', '0', '8', '6620', '0', '6380', '6379', '0', '6381', '9204', '0', '8', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `npc_to_pc_polymorph` VALUES ('1', '7077', 'Gracious', 'Vortex Gatekeeper', '110', '0', '2', '1', '2', '0', '0', '0', '0', '0', '0', '8', '0', '0', '6380', '6379', '0', '6381', '9204', '0', '8', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `npc_to_pc_polymorph` VALUES ('1', '31228', 'Carina', 'Class Master', '118', '1', '2', '1', '2', '0', '0', '0', '0', '0', '0', '8', '0', '0', '6380', '6379', '0', '6381', '9204', '0', '8', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');