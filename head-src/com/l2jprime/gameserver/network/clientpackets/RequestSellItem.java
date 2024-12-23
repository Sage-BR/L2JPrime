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
import com.l2jprime.gameserver.cache.HtmCache;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.actor.instance.L2FishermanInstance;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jprime.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.ItemList;
import com.l2jprime.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jprime.gameserver.network.serverpackets.StatusUpdate;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;

public final class RequestSellItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _items; // count*3

	/**
	 * packet type id 0x1e sample 1e 00 00 00 00 // list id 02 00 00 00 // number of items 71 72 00 10 // object id ea 05 00 00 // item id 01 00 00 00 // item count 76 4b 00 10 // object id 2e 0a 00 00 // item id 01 00 00 00 // item count format: cdd (ddd)
	 */
	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();

		if ((_count <= 0) || ((_count * 12) > _buf.remaining()) || (_count > Config.MAX_ITEM_IN_PACKET))
		{
			_count = 0;
			_items = null;
			return;
		}

		_items = new int[_count * 3];

		for (int i = 0; i < _count; i++)
		{
			final int objectId = readD();
			_items[(i * 3) + 0] = objectId;
			final int itemId = readD();
			_items[(i * 3) + 1] = itemId;
			final long cnt = readD();

			if ((cnt > Integer.MAX_VALUE) || (cnt <= 0))
			{
				_count = 0;
				_items = null;
				return;
			}
			_items[(i * 3) + 2] = (int) cnt;
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

		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("buy"))
		{
			player.sendMessage("You buying too fast.");
			return;
		}

		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}

		final L2Object target = player.getTarget();
		if (!player.isGM() && ((target == null // No target (ie GM Shop)
		) || !(target instanceof L2MerchantInstance) // Target not a merchant and not mercmanager
			|| !player.isInsideRadius(target, L2NpcInstance.INTERACTION_DISTANCE, false, false)))
		{
			return; // Distance is too far
		}
		
		String htmlFolder = "";
		L2NpcInstance merchant = null;
		if (target instanceof L2MerchantInstance)
		{
			htmlFolder = "merchant";
			merchant = (L2NpcInstance) target;
		}
		else if (target instanceof L2FishermanInstance)
		{
			htmlFolder = "fisherman";
			merchant = (L2NpcInstance) target;
		}
		else
		{
			return;
		}

		if (_listId > 1000000) // lease
		{
			if (merchant.getTemplate().npcId != (_listId - 1000000))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}

		long totalPrice = 0;
		// Proceed the sell
		for (int i = 0; i < _count; i++)
		{
			final int objectId = _items[(i * 3) + 0];
			final int count = _items[(i * 3) + 2];

			// Check count
			if ((count <= 0) || (count > Integer.MAX_VALUE))
			{
				// Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.", Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				sm = null;
				return;
			}

			L2ItemInstance item = player.checkItemManipulation(objectId, count, "sell");

			// Check Item
			if ((item == null) || !item.getItem().isSellable())
			{
				continue;
			}

			final long price = item.getReferencePrice() / 2;
			totalPrice += price * count;

			// Fix exploit during Sell
			// Check totalPrice
			if (((Integer.MAX_VALUE / count) < price) || (totalPrice > Integer.MAX_VALUE) || (totalPrice <= 0))
			{
				// Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.", Config.DEFAULT_PUNISH);
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				sm = null;
				return;
			}

			item = player.getInventory().destroyItem("Sell", objectId, count, player, null);

			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) int price = item.getReferencePrice()*(int)count/2; L2ItemInstance li = null; L2ItemInstance la = null; if (_listId > 1000000) { li = merchant.findLeaseItem(item.getItemId(),item.getEnchantLevel()); la = merchant.getLeaseAdena(); if
			 * (li == null || la == null) continue; price = li.getPriceToBuy()*(int)count; // player sells, thus merchant buys. if (price > la.getCount()) continue; }
			 */
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) if (item != null && _listId > 1000000) { li.setCount(li.getCount()+(int)count); li.updateDatabase(); la.setCount(la.getCount()-price); la.updateDatabase(); }
			 */
		}
		player.addAdena("Sell", (int) totalPrice, merchant, false);

		final String html = HtmCache.getInstance().getHtm("data/html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm");

		if (html != null)
		{
			final NpcHtmlMessage soldMsg = new NpcHtmlMessage(merchant.getObjectId());
			soldMsg.setHtml(html.replaceAll("%objectId%", String.valueOf(merchant.getObjectId())));
			player.sendPacket(soldMsg);
		}

		// Update current load as well
		final StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, true));
	}

	@Override
	public String getType()
	{
		return "[C] 1E RequestSellItem";
	}
}
