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
package com.l2jprime.gameserver.network.clientpackets;

import com.l2jprime.gameserver.model.L2World;
import com.l2jprime.gameserver.model.TradeList;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;

public final class TradeDone extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		_response = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("trade"))
		{
			player.sendMessage("You trading too fast.");
			return;
		}

		final TradeList trade = player.getActiveTradeList();
		if ((trade == null) || (trade.getOwner().getActiveEnchantItem() != null) || (trade.getPartner().getActiveEnchantItem() != null) || trade.isLocked())
		{
			return;
		}

		// abort cast anyway
		player.abortCast(true);

		if (player.isCastingNow() || player.isCastingPotionNow())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (_response == 1)
		{
			if ((trade.getPartner() == null) || (L2World.getInstance().findObject(trade.getPartner().getObjectId()) == null))
			{
				// Trade partner not found, cancel trade
				player.cancelActiveTrade();
				SystemMessage msg = new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
				player.sendPacket(msg);
				msg = null;
				return;
			}

			if (!player.getAccessLevel().allowTransaction())
			{
				player.sendMessage("Unsufficient privileges.");
				player.cancelActiveTrade();
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			trade.confirm();
		}
		else
		{
			player.cancelActiveTrade();
		}
	}

	@Override
	public String getType()
	{
		return "[C] 17 TradeDone";
	}
}
