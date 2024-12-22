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

/**
 * l2jprime
 */
import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.communitybbs.CommunityBoard;
import com.l2jprime.gameserver.datatables.BufferSkillsTable;
import com.l2jprime.gameserver.datatables.CharSchemesTable;
import com.l2jprime.gameserver.handler.VoicedCommandHandler;
import com.l2jprime.gameserver.handler.custom.CustomBypassHandler;
import com.l2jprime.gameserver.handler.voicedcommandhandlers.Repair;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.powerpak.Buffer.BuffHandler;
import com.l2jprime.gameserver.powerpak.Buffer.BuffTable;
import com.l2jprime.gameserver.powerpak.RaidInfo.RaidInfoHandler;
import com.l2jprime.gameserver.powerpak.engrave.EngraveManager;
import com.l2jprime.gameserver.powerpak.globalGK.GKHandler;
import com.l2jprime.gameserver.powerpak.gmshop.GMShop;

public class PowerPak
{
	private final Logger LOGGER = Logger.getLogger(PowerPak.class);
	private static PowerPak _instance = null;

	public static PowerPak getInstance()
	{
		if (_instance == null)
		{
			_instance = new PowerPak();
		}
		return _instance;
	}

	private PowerPak()
	{
		if (Config.POWERPAK_ENABLED)
		{
			PowerPakConfig.load();
			if (PowerPakConfig.BUFFER_ENABLED)
			{
				LOGGER.info("Buffer is Enabled.");
				BuffTable.getInstance();
				if (((PowerPakConfig.BUFFER_COMMAND != null) && (PowerPakConfig.BUFFER_COMMAND.length() > 0)) || PowerPakConfig.BUFFER_USEBBS)
				{

					final BuffHandler handler = new BuffHandler();
					if (PowerPakConfig.BUFFER_USECOMMAND && (PowerPakConfig.BUFFER_COMMAND != null) && (PowerPakConfig.BUFFER_COMMAND.length() > 0))
					{
						VoicedCommandHandler.getInstance().registerVoicedCommandHandler(handler);
					}

					if (PowerPakConfig.BUFFER_USEBBS)
					{
						CommunityBoard.getInstance().registerBBSHandler(handler);
					}
					CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);

				}

				BufferSkillsTable.getInstance();
				CharSchemesTable.getInstance();
			}

			if (PowerPakConfig.GLOBALGK_ENABDLED)
			{
				final GKHandler handler = new GKHandler();
				if ((PowerPakConfig.GLOBALGK_COMMAND != null) && (PowerPakConfig.GLOBALGK_COMMAND.length() > 0))
				{
					VoicedCommandHandler.getInstance().registerVoicedCommandHandler(handler);
				}

				if (PowerPakConfig.GLOBALGK_USEBBS)
				{
					CommunityBoard.getInstance().registerBBSHandler(handler);
				}
				CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
				LOGGER.info("Global Gatekeeper is Enabled.");
			}

			if (PowerPakConfig.GMSHOP_ENABLED)
			{
				final GMShop gs = new GMShop();
				CustomBypassHandler.getInstance().registerCustomBypassHandler(gs);
				if ((PowerPakConfig.GLOBALGK_COMMAND != null) && (PowerPakConfig.GLOBALGK_COMMAND.length() > 0))
				{
					VoicedCommandHandler.getInstance().registerVoicedCommandHandler(gs);
				}

				if (PowerPakConfig.GMSHOP_USEBBS)
				{
					CommunityBoard.getInstance().registerBBSHandler(gs);
				}
				LOGGER.info("GM Shop is Enabled.");
			}

			if (PowerPakConfig.ENGRAVER_ENABLED)
			{
				EngraveManager.getInstance();
				LOGGER.info("Engrave System is Enabled.");
			}

			final RaidInfoHandler handler = new RaidInfoHandler();
			CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
			LOGGER.info("Raid Info is Enabled.");

			if (PowerPakConfig.CHAR_REPAIR)
			{
				final Repair repair_handler = new Repair();
				VoicedCommandHandler.getInstance().registerVoicedCommandHandler(repair_handler);
				CustomBypassHandler.getInstance().registerCustomBypassHandler(repair_handler);
				LOGGER.info("Char Repair is Enabled.");
			}

		}
	}

	public void chatHandler(final L2PcInstance sayer, final int chatType, final String message)
	{
	}
}