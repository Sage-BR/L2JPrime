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

import java.util.List;

import com.l2jprime.Config;
import com.l2jprime.gameserver.datatables.sql.ItemTable;
import com.l2jprime.gameserver.managers.CastleManorManager;
import com.l2jprime.gameserver.managers.CastleManorManager.CropProcure;
import com.l2jprime.gameserver.model.L2Manor;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2ManorManagerInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jprime.gameserver.network.serverpackets.StatusUpdate;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;
import com.l2jprime.gameserver.templates.L2Item;
import com.l2jprime.gameserver.util.Util;

import javolution.util.FastList;

@SuppressWarnings("unused")
public class RequestBuyProcure extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _items;
	private List<CropProcure> _procureList = new FastList<>();

	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();

		if ((_count > 500) || (_count < 0)) // protect server
		{
			_count = 0;
			return;
		}

		_items = new int[_count * 2];
		for (int i = 0; i < _count; i++)
		{
			readD();
			final int itemId = readD();
			_items[(i * 2) + 0] = itemId;
			final long cnt = readD();

			if ((cnt > Integer.MAX_VALUE) || (cnt < 1))
			{
				_count = 0;
				_items = null;
				return;
			}

			_items[(i * 2) + 1] = (int) cnt;
		}
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		// Alt game - Karma punishment
		if ((player == null) || !getClient().getFloodProtectors().getManor().tryPerformAction("BuyProcure") || (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0)))
		{
			return;
		}

		if (_count < 1)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		// Check for buylist validity and calculates summary values
		int slots = 0;
		int weight = 0;

		if (!(player.getTarget() instanceof L2ManorManagerInstance))
		{
			return;
		}

		final L2ManorManagerInstance manor = (L2ManorManagerInstance) player.getTarget();
		for (int i = 0; i < _count; i++)
		{
			final int itemId = _items[(i * 2) + 0];
			final int count = _items[(i * 2) + 1];
			if (count > Integer.MAX_VALUE)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.", Config.DEFAULT_PUNISH);
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				return;
			}

			final L2Item template = ItemTable.getInstance().getTemplate(L2Manor.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward()));
			weight += count * template.getWeight();

			if (!template.isStackable())
			{
				slots += count;
			}
			else if (player.getInventory().getItemByItemId(itemId) == null)
			{
				slots++;
			}
		}

		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}

		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}

		// Proceed the purchase
		final InventoryUpdate playerIU = new InventoryUpdate();
		_procureList = manor.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT);

		for (int i = 0; i < _count; i++)
		{
			final int itemId = _items[(i * 2) + 0];
			int count = _items[(i * 2) + 1];

			if (count < 0)
			{
				count = 0;
			}

			final int rewradItemId = L2Manor.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());

			int rewradItemCount = 1; // L2Manor.getInstance().getRewardAmount(itemId, manor.getCastle().getCropReward(itemId));

			rewradItemCount = count / rewradItemCount;

			// Add item to Inventory and adjust update packet
			final L2ItemInstance item = player.getInventory().addItem("Manor", rewradItemId, rewradItemCount, player, manor);
			final L2ItemInstance iteme = player.getInventory().destroyItemByItemId("Manor", itemId, count, player, manor);

			if ((item == null) || (iteme == null))
			{
				continue;
			}

			playerIU.addRemovedItem(iteme);
			if (item.getCount() > rewradItemCount)
			{
				playerIU.addModifiedItem(item);
			}
			else
			{
				playerIU.addNewItem(item);
			}

			// Send Char Buy Messages
			SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
			sm.addItemName(rewradItemId);
			sm.addNumber(rewradItemCount);
			player.sendPacket(sm);
			sm = null;

			// manor.getCastle().setCropAmount(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getAmount() - count);
		}

		// Send update packets
		player.sendPacket(playerIU);

		final StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}

	@Override
	public String getType()
	{
		return "[C] C3 RequestBuyProcure";
	}
}
