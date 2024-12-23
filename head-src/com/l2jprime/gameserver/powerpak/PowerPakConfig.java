/*
 * l2jprime Project - 4teambr.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jprime.gameserver.powerpak;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.L2Properties;
import com.l2jprime.gameserver.templates.L2Item;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Nick
 */
public class PowerPakConfig
{
	private static String PP_CONFIG_FILE = "config/powerpak/powerpak.properties";
	private static final Logger LOGGER = Logger.getLogger(PowerPakConfig.class);
	public static boolean ENGRAVER_ENABLED;
	public static int ENGRAVE_PRICE = 0;
	public static int ENGRAVE_PRICE_ITEM = 57;
	public static int ENGRAVER_X = 82270;
	public static int ENGRAVER_Y = 149660;
	public static int ENGRAVER_Z = -3495;
	public static int MAX_ENGRAVED_ITEMS_PER_CHAR;
	public static boolean SPAWN_ENGRAVER = true;
	public static boolean ENGRAVE_ALLOW_DESTROY;
	public static ArrayList<Integer> ENGRAVE_EXCLUDED_ITEMS = new ArrayList<>();
	public static ArrayList<Integer> ENGRAVE_ALLOW_GRADE = new ArrayList<>();

	public static int BUFFER_NPC;
	public static boolean BUFFER_ENABLED;
	public static List<String> BUFFER_EXCLUDE_ON = new FastList<>();
	public static String BUFFER_COMMAND;
	public static int BUFFER_PRICE;
	public static boolean BUFFER_USEBBS;
	public static boolean BUFFER_USECOMMAND;

	public static FastMap<Integer, Integer> FIGHTER_SKILL_LIST;
	public static FastMap<Integer, Integer> MAGE_SKILL_LIST;

	public static int NPCBUFFER_MAX_SCHEMES;
	public static int NPCBUFFER_MAX_SKILLS;
	public static boolean NPCBUFFER_STORE_SCHEMES;
	public static int NPCBUFFER_STATIC_BUFF_COST;

	public static List<String> GLOBALGK_EXCLUDE_ON;
	public static boolean GLOBALGK_ENABDLED;
	public static boolean GLOBALGK_USEBBS;
	public static int GLOBALGK_NPC;
	public static int GLOBALGK_PRICE;
	public static int GLOBALGK_TIMEOUT;
	public static String GLOBALGK_COMMAND;
	public static boolean GLOBALGK_USECOMMAND;

	public static int GMSHOP_NPC;
	public static boolean GMSHOP_ENABLED;
	public static boolean GMSHOP_USEBBS;
	public static String GMSHOP_COMMAND;
	public static List<String> GMSHOP_EXCLUDE_ON;
	public static boolean GMSHOP_USECOMMAND;

	public static boolean ENABLE_SAY_SOCIAL_ACTIONS;

	public static boolean CHAR_REPAIR;

	public static void load()
	{
		try
		{
			final L2Properties p = new L2Properties(PP_CONFIG_FILE);
			ENGRAVER_ENABLED = Boolean.parseBoolean(p.getProperty("EngraveEnabled", "true"));
			ENGRAVE_PRICE = Integer.parseInt(p.getProperty("EngravePrice", "0"));
			ENGRAVE_PRICE_ITEM = Integer.parseInt(p.getProperty("EngravePriceItem", "57"));
			SPAWN_ENGRAVER = Boolean.parseBoolean(p.getProperty("EngraveSpawnNpc", "true"));
			ENGRAVE_ALLOW_DESTROY = Boolean.parseBoolean(p.getProperty("EngraveAllowDestroy", "false"));
			MAX_ENGRAVED_ITEMS_PER_CHAR = Integer.parseInt(p.getProperty("EngraveMaxItemsPerChar", "0"));
			String str = p.getProperty("EngraveNpcLocation", "").trim();
			if (str.length() > 0)
			{
				final StringTokenizer st = new StringTokenizer(str, " ");
				if (st.hasMoreTokens())
				{
					ENGRAVER_X = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens())
				{
					ENGRAVER_Y = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens())
				{
					ENGRAVER_Z = Integer.parseInt(st.nextToken());
				}
			}
			str = p.getProperty("EngraveExcludeItems", "").trim();
			if (str.length() > 0)
			{
				final StringTokenizer st = new StringTokenizer(str, ",");
				while (st.hasMoreTokens())
				{
					ENGRAVE_EXCLUDED_ITEMS.add(Integer.parseInt(st.nextToken().trim()));
				}
			}
			str = p.getProperty("EngraveAllowGrades", "all").toLowerCase();
			if ((str.indexOf("none") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_NONE);
			}

			if ((str.indexOf("a") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_A);
			}

			if ((str.indexOf("b") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_B);
			}

			if ((str.indexOf("c") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_C);
			}

			if ((str.indexOf("d") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_D);
			}

			if ((str.indexOf("s") != -1) || (str.indexOf("all") != -1))
			{
				ENGRAVE_ALLOW_GRADE.add(L2Item.CRYSTAL_S);
			}

			BUFFER_ENABLED = Boolean.parseBoolean(p.getProperty("BufferEnabled", "false"));
			StringTokenizer st = new StringTokenizer(p.getProperty("BufferExcludeOn", ""), " ");
			while (st.hasMoreTokens())
			{
				BUFFER_EXCLUDE_ON.add(st.nextToken());
			}
			BUFFER_COMMAND = p.getProperty("BufferCommand", "buffme");
			BUFFER_NPC = Integer.parseInt(p.getProperty("BufferNpcId", "50019"));
			BUFFER_PRICE = Integer.parseInt(p.getProperty("BufferPrice", "-1"));
			BUFFER_USEBBS = Boolean.parseBoolean(p.getProperty("BufferUseBBS", "false"));
			BUFFER_USECOMMAND = Boolean.parseBoolean(p.getProperty("BufferUseCommand", "false"));

			FIGHTER_SKILL_LIST = new FastMap<>();
			MAGE_SKILL_LIST = new FastMap<>();

			String[] fPropertySplit;
			fPropertySplit = p.getProperty("FighterSkillList", "").split(";");

			String[] mPropertySplit;
			mPropertySplit = p.getProperty("MageSkillList", "").split(";");

			for (final String skill : fPropertySplit)
			{
				final String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					LOGGER.info("[FighterSkillList]: invalid config property -> FighterSkillList \"" + skill + "\"");
				}
				else
				{
					try
					{
						FIGHTER_SKILL_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (final NumberFormatException nfe)
					{
						if (Config.ENABLE_ALL_EXCEPTIONS)
						{
							nfe.printStackTrace();
						}

						if (!skill.equals(""))
						{
							LOGGER.info("[FighterSkillList]: invalid config property -> FighterSkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}

			for (final String skill : mPropertySplit)
			{
				final String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					LOGGER.info("[MageSkillList]: invalid config property -> MageSkillList \"" + skill + "\"");
				}
				else
				{
					try
					{
						MAGE_SKILL_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (final NumberFormatException nfe)
					{
						if (Config.ENABLE_ALL_EXCEPTIONS)
						{
							nfe.printStackTrace();
						}

						if (!skill.equals(""))
						{
							LOGGER.info("[MageSkillList]: invalid config property -> MageSkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}

			NPCBUFFER_MAX_SCHEMES = Integer.parseInt(p.getProperty("NPCBufferMaxSchemesPerChar", "4"));
			NPCBUFFER_MAX_SKILLS = Integer.parseInt(p.getProperty("NPCBufferMaxSkllsperScheme", "24"));
			NPCBUFFER_STORE_SCHEMES = Boolean.parseBoolean(p.getProperty("NPCBufferStoreSchemes", "True"));
			NPCBUFFER_STATIC_BUFF_COST = Integer.parseInt(p.getProperty("NPCBufferStaticCostPerBuff", "-1"));

			GLOBALGK_NPC = Integer.parseInt(p.getProperty("GKNpcId", "7077"));
			GLOBALGK_ENABDLED = Boolean.parseBoolean(p.getProperty("GKEnabled", "false"));
			GLOBALGK_COMMAND = p.getProperty("GKCommand", "teleport");
			GLOBALGK_TIMEOUT = Integer.parseInt(p.getProperty("GKTimeout", "10"));
			if (GLOBALGK_TIMEOUT < 1)
			{
				GLOBALGK_TIMEOUT = 1;
			}
			GLOBALGK_PRICE = Integer.parseInt(p.getProperty("GKPrice", "-1"));
			GLOBALGK_USECOMMAND = Boolean.parseBoolean(p.getProperty("GKUseCommand", "false"));
			GLOBALGK_USEBBS = Boolean.parseBoolean(p.getProperty("GKUseBBS", "true"));
			GLOBALGK_EXCLUDE_ON = new FastList<>();
			st = new StringTokenizer(p.getProperty("GKExcludeOn", ""), " ");
			while (st.hasMoreTokens())
			{
				GLOBALGK_EXCLUDE_ON.add(st.nextToken().toUpperCase());
			}

			GMSHOP_NPC = Integer.parseInt(p.getProperty("GMShopNpcId", "53"));
			GMSHOP_ENABLED = Boolean.parseBoolean(p.getProperty("GMShopEnabled", "false"));
			GMSHOP_COMMAND = p.getProperty("GMShopCommand", "gmshop");
			GMSHOP_USEBBS = Boolean.parseBoolean(p.getProperty("GMShopUseBBS", "false"));
			GMSHOP_USECOMMAND = Boolean.parseBoolean(p.getProperty("GMShopUseCommand", "false"));
			GMSHOP_EXCLUDE_ON = new FastList<>();
			st = new StringTokenizer(p.getProperty("GMShopExcludeOn", ""), " ");
			while (st.hasMoreTokens())
			{
				GMSHOP_EXCLUDE_ON.add(st.nextToken().toUpperCase());
			}

			ENABLE_SAY_SOCIAL_ACTIONS = Boolean.parseBoolean(p.getProperty("EnableSocialSayActions", "false"));

			CHAR_REPAIR = Boolean.parseBoolean(p.getProperty("CharacterRepair", "False"));

		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.error("PowerPak: Unable to read  " + PP_CONFIG_FILE);
		}
	}
}
