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
package com.l2jprime.gameserver.network.serverpackets;

import java.util.List;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.L2TradeList;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.templates.L2Item;

public class WearList extends L2GameServerPacket
{
	private static final String _S__EF_WEARLIST = "[S] EF WearList";
	private final int _listId;
	private final L2ItemInstance[] _list;
	private final int _money;
	private int _expertise;

	public WearList(final L2TradeList list, final int currentMoney, final int expertiseIndex)
	{
		_listId = list.getListId();
		final List<L2ItemInstance> lst = list.getItems();
		_list = lst.toArray(new L2ItemInstance[lst.size()]);
		_money = currentMoney;
		_expertise = expertiseIndex;
	}

	public WearList(final List<L2ItemInstance> lst, final int listId, final int currentMoney)
	{
		_listId = listId;
		_list = lst.toArray(new L2ItemInstance[lst.size()]);
		_money = currentMoney;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xef);
		writeC(0xc0); // ?
		writeC(0x13); // ?
		writeC(0x00); // ?
		writeC(0x00); // ?
		writeD(_money); // current money
		writeD(_listId);

		int newlength = 0;
		for (final L2ItemInstance item : _list)
		{
			if ((item.getItem().getCrystalType() <= _expertise) && item.isEquipable())
			{
				newlength++;
			}
		}
		writeH(newlength);

		for (final L2ItemInstance item : _list)
		{
			if ((item.getItem().getCrystalType() <= _expertise) && item.isEquipable())
			{
				writeD(item.getItemId());
				writeH(item.getItem().getType2()); // item type2

				if (item.getItem().getType1() != L2Item.TYPE1_ITEM_QUESTITEM_ADENA)
				{
					writeH(item.getItem().getBodyPart()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}
				else
				{
					writeH(0x00); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}

				writeD(Config.WEAR_PRICE);

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__EF_WEARLIST;
	}
}
