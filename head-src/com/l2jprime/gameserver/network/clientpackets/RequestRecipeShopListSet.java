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

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2ManufactureItem;
import com.l2jprime.gameserver.model.L2ManufactureList;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.RecipeShopMsg;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;

public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private int _count;
	private int[] _items; // count*2

	@Override
	protected void readImpl()
	{
		_count = readD();

		if ((_count < 0) || ((_count * 8) > _buf.remaining()) || (_count > Config.MAX_ITEM_IN_PACKET))
		{
			_count = 0;
		}

		_items = new int[_count * 2];

		for (int x = 0; x < _count; x++)
		{
			final int recipeID = readD();
			_items[(x * 2) + 0] = recipeID;
			final int cost = readD();
			_items[(x * 2) + 1] = cost;
		}
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (player.isInDuel())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isTradeDisabled() || player.isInsideZone(L2Character.ZONE_NOSTORE))
		{
			// player.sendPacket(new RecipeShopManageList(player, player.isDwarven()));
			player.sendMessage("Private manufacture are disable here. Try in another place.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (_count == 0)
		{
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			player.standUp();
		}
		else
		{
			final L2ManufactureList createList = new L2ManufactureList();

			for (int x = 0; x < _count; x++)
			{
				final int recipeID = _items[(x * 2) + 0];
				final int cost = _items[(x * 2) + 1];
				createList.add(new L2ManufactureItem(recipeID, cost));
			}
			createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
			player.setCreateList(createList);

			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.sendPacket(new RecipeShopMsg(player));
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}

	@Override
	public String getType()
	{
		return "[C] b2 RequestRecipeShopListSet";
	}

}
