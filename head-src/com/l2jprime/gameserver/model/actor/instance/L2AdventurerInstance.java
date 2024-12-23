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
package com.l2jprime.gameserver.model.actor.instance;

import com.l2jprime.Config;
import com.l2jprime.gameserver.managers.RaidBossSpawnManager;
import com.l2jprime.gameserver.model.spawn.L2Spawn;
import com.l2jprime.gameserver.network.serverpackets.ExQuestInfo;
import com.l2jprime.gameserver.network.serverpackets.RadarControl;
import com.l2jprime.gameserver.templates.L2NpcTemplate;

/**
 * This class ...
 * @version $Revision: $ $Date: $
 * @author LBaldi
 */
public class L2AdventurerInstance extends L2FolkInstance
{
	// private static Logger LOGGER = Logger.getLogger(L2AdventurerInstance.class);

	public L2AdventurerInstance(final int objectId, final L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(final L2PcInstance player, final String command)
	{
		if (command.startsWith("npcfind_byid"))
		{
			try
			{
				final int bossId = Integer.parseInt(command.substring(12).trim());
				switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(bossId))
				{
					case ALIVE:
					case DEAD:
						L2Spawn spawn = RaidBossSpawnManager.getInstance().getSpawns().get(bossId);
						player.sendPacket(new RadarControl(0, 1, spawn.getLocx(), spawn.getLocy(), spawn.getLocz()));
						spawn = null;
						break;
					case UNDEFINED:
						player.sendMessage("This Boss isn't in game - notify l2jprime Datapack Dev Team");
						break;
				}
			}
			catch (final NumberFormatException e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e.printStackTrace();
				}

				LOGGER.warn("Invalid Bypass to Server command parameter.");
			}
		}
		else if (command.startsWith("raidInfo"))
		{
			final int bossLevel = Integer.parseInt(command.substring(9).trim());
			String filename = "data/html/adventurer_guildsman/raid_info/info.htm";

			if (bossLevel != 0)
			{
				filename = "data/html/adventurer_guildsman/raid_info/level" + bossLevel + ".htm";
			}

			showChatWindow(player, bossLevel, filename);
			filename = null;
		}
		else if (command.equalsIgnoreCase("questlist"))
		{
			player.sendPacket(new ExQuestInfo());
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(final int npcId, final int val)
	{
		String pom = "";

		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "data/html/adventurer_guildsman/" + pom + ".htm";
	}

	private void showChatWindow(final L2PcInstance player, final int bossLevel, final String filename)
	{
		showChatWindow(player, filename);
	}
}
