/* l2jprime Project - 4teambr.com
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
package com.l2jprime.gameserver.network.clientpackets;

import com.l2jprime.gameserver.datatables.sql.ClanTable;
import com.l2jprime.gameserver.model.L2Clan;
import com.l2jprime.gameserver.model.L2World;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;

public final class RequestStopPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;

	@Override
	protected void readImpl()
	{
		_pledgeName = readS();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		final L2Clan playerClan = player.getClan();
		if (playerClan == null)
		{
			return;
		}

		final L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (clan == null)
		{
			player.sendMessage("No such clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (!playerClan.isAtWarWith(clan.getClanId()))
		{
			player.sendMessage("You aren't at war with this clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		// Check if player who does the request has the correct rights to do it
		if ((player.getClanPrivileges() & L2Clan.CP_CL_PLEDGE_WAR) != L2Clan.CP_CL_PLEDGE_WAR)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}

		// LOGGER.info("RequestStopPledgeWar: By leader or authorized player: " + playerClan.getLeaderName() + " of clan: "
		// + playerClan.getName() + " to clan: " + _pledgeName);

		// L2PcInstance leader = L2World.getInstance().getPlayer(clan.getLeaderName());
		// if(leader != null && leader.isOnline() == 0)
		// {
		// player.sendMessage("Clan leader isn't online.");
		// player.sendPacket(ActionFailed.STATIC_PACKET);
		// return;
		// }

		// if (leader.isProcessingRequest())
		// {
		// SystemMessage sm = new SystemMessage(SystemMessage.S1_IS_BUSY_TRY_LATER);
		// sm.addString(leader.getName());
		// player.sendPacket(sm);
		// return;
		// }

		ClanTable.getInstance().deleteclanswars(playerClan.getClanId(), clan.getClanId());
		for (final L2PcInstance cha : L2World.getInstance().getAllPlayers())
		{
			if ((cha.getClan() == player.getClan()) || (cha.getClan() == clan))
			{
				cha.broadcastUserInfo();
			}
		}
	}

	@Override
	public String getType()
	{
		return "[C] 4F RequestStopPledgeWar";
	}
}
