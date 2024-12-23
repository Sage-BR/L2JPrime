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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.l2jprime.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jprime.Config;
import com.l2jprime.gameserver.handler.IAdminCommandHandler;
import com.l2jprime.gameserver.managers.CastleManager;
import com.l2jprime.gameserver.managers.CastleManorManager;
import com.l2jprime.gameserver.managers.CastleManorManager.CropProcure;
import com.l2jprime.gameserver.managers.CastleManorManager.SeedProduction;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.entity.siege.Castle;
import com.l2jprime.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;
import javolution.util.FastList;

/**
 * Admin comand handler for Manor System This class handles following admin commands: - manor_info = shows info about current manor state - manor_approve = approves settings for the next manor period - manor_setnext = changes manor settings to the next day's - manor_reset castle = resets all manor
 * data for specified castle (or all) - manor_setmaintenance = sets manor system under maintenance mode - manor_save = saves all manor data into database - manor_disable = disables manor system
 * @author l3x
 */
public class AdminManor implements IAdminCommandHandler
{
	private static final String[] _adminCommands =
	{
		"admin_manor",
		"admin_manor_reset",
		"admin_manor_save",
		"admin_manor_disable"
	};

	@Override
	public boolean useAdminCommand(String command, final L2PcInstance activeChar)
	{
		/*
		 * if(!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel())){ return false; } if(Config.GMAUDIT) { Logger _logAudit = Logger.getLogger("gmaudit"); LogRecord record = new LogRecord(Level.INFO, command); record.setParameters(new Object[] { "GM: " +
		 * activeChar.getName(), " to target [" + activeChar.getTarget() + "] " }); _logAudit.LOGGER(record); }
		 */

		StringTokenizer st = new StringTokenizer(command);
		command = st.nextToken();

		switch (command)
		{
			case "admin_manor":
				showMainPage(activeChar);
				break;
			case "admin_manor_reset":
				int castleId = 0;

				try
				{
					castleId = Integer.parseInt(st.nextToken());
				}
				catch (final Exception e)
				{
					if (Config.ENABLE_ALL_EXCEPTIONS)
					{
						e.printStackTrace();
					}
				}

				if (castleId > 0)
				{
					final Castle castle = CastleManager.getInstance().getCastleById(castleId);
					castle.setCropProcure(new FastList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
					castle.setCropProcure(new FastList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
					castle.setSeedProduction(new FastList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
					castle.setSeedProduction(new FastList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);

					if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
					{
						castle.saveCropData();
						castle.saveSeedData();
					}

					activeChar.sendMessage("Manor data for " + castle.getName() + " was nulled");
				}
				else
				{
					for (final Castle castle : CastleManager.getInstance().getCastles())
					{
						castle.setCropProcure(new FastList<CropProcure>(), CastleManorManager.PERIOD_CURRENT);
						castle.setCropProcure(new FastList<CropProcure>(), CastleManorManager.PERIOD_NEXT);
						castle.setSeedProduction(new FastList<SeedProduction>(), CastleManorManager.PERIOD_CURRENT);
						castle.setSeedProduction(new FastList<SeedProduction>(), CastleManorManager.PERIOD_NEXT);

						if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
						{
							castle.saveCropData();
							castle.saveSeedData();
						}
					}

					activeChar.sendMessage("Manor data was nulled");
				}

				showMainPage(activeChar);
				break;
			case "admin_manor_save":
				CastleManorManager.getInstance().save();
				activeChar.sendMessage("Manor System: all data saved");
				showMainPage(activeChar);
				break;
			case "admin_manor_disable":
				final boolean mode = CastleManorManager.getInstance().isDisabled();

				CastleManorManager.getInstance().setDisabled(!mode);

				if (mode)
				{
					activeChar.sendMessage("Manor System: enabled");
				}
				else
				{
					activeChar.sendMessage("Manor System: disabled");
				}

				showMainPage(activeChar);
				break;
		}

		st = null;

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return _adminCommands;
	}

	/*
	 * private String formatTime(long millis) { String s = ""; int secs = (int) millis/1000; int mins = secs/60; secs -= mins*60; int hours = mins/60; mins -= hours*60; if (hours>0) s += hours + ":"; s += mins + ":"; s += secs; return s; }
	 */

	private void showMainPage(final L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><body>");

		replyMSG.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
		replyMSG.append("<table width=\"100%\"><tr><td>");
		replyMSG.append("Disabled: " + (CastleManorManager.getInstance().isDisabled() ? "yes" : "no") + "</td><td>");
		replyMSG.append("Under Maintenance: " + (CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no") + "</td></tr><tr><td>");
		replyMSG.append("<tr><td>Approved: " + (CastleManorManager.APPROVE == 1 ? "yes" : "no") + "</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"" + (CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr><tr><td>");
		replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("<br><center>Castle Information:<table width=\"100%\">");
		replyMSG.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");

		for (final Castle c : CastleManager.getInstance().getCastles())
		{
			replyMSG.append("<tr><td>" + c.getName() + "</td><td>" + c.getManorCost(CastleManorManager.PERIOD_CURRENT) + "a</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_NEXT) + "a</td></tr>");
		}

		replyMSG.append("</table><br>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);

		adminReply = null;
		replyMSG = null;
	}
}
