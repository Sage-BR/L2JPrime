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

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jprime.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jprime.gameserver.network.serverpackets.ValidateLocation;
import com.l2jprime.gameserver.templates.L2NpcTemplate;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class L2SiegeNpcInstance extends L2FolkInstance
{
	// private static Logger LOGGER = Logger.getLogger(L2SiegeNpcInstance.class);

	public L2SiegeNpcInstance(final int objectID, final L2NpcTemplate template)
	{
		super(objectID, template);
	}

	/**
	 * this is called when a player interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(final L2PcInstance player)
	{
		if (!canTarget(player))
		{
			return;
		}

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			my = null;

			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				showSiegeInfoWindow(player);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

	/**
	 * If siege is in progress shows the Busy HTML<BR>
	 * else Shows the SiegeInfo window
	 * @param player
	 */
	public void showSiegeInfoWindow(final L2PcInstance player)
	{
		if (validateCondition(player))
		{
			getCastle().getSiege().listRegisterClan(player);
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/siege/" + getTemplate().npcId + "-busy.htm");
			html.replace("%castlename%", getCastle().getName());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			html = null;
		}
	}

	private boolean validateCondition(final L2PcInstance player)
	{
		if (getCastle().getSiege().getIsInProgress())
		{
			return false; // Busy because of siege
		}
		
		return true;
	}
}
