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

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.ItemInfo;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;

import javolution.util.FastList;

/**
 * This class ...
 * @author Yme
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $ Rebuild 23.2.2006 by Advi
 */
public class PetInventoryUpdate extends L2GameServerPacket
{
	private static Logger LOGGER = Logger.getLogger(InventoryUpdate.class);
	private static final String _S__37_INVENTORYUPDATE = "[S] b3 InventoryUpdate";
	private final List<ItemInfo> _items;

	/**
	 * @param items
	 */
	public PetInventoryUpdate(final List<ItemInfo> items)
	{
		_items = items;
		if (Config.DEBUG)
		{
			showDebug();
		}
	}

	public PetInventoryUpdate()
	{
		this(new FastList<ItemInfo>());
	}

	public void addItem(final L2ItemInstance item)
	{
		_items.add(new ItemInfo(item));
	}

	public void addNewItem(final L2ItemInstance item)
	{
		_items.add(new ItemInfo(item, 1));
	}

	public void addModifiedItem(final L2ItemInstance item)
	{
		_items.add(new ItemInfo(item, 2));
	}

	public void addRemovedItem(final L2ItemInstance item)
	{
		_items.add(new ItemInfo(item, 3));
	}

	public void addItems(final List<L2ItemInstance> items)
	{
		for (final L2ItemInstance item : items)
		{
			_items.add(new ItemInfo(item));
		}
	}

	private void showDebug()
	{
		for (final ItemInfo item : _items)
		{
			LOGGER.debug("oid:" + Integer.toHexString(item.getObjectId()) + " item:" + item.getItem().getName() + " last change:" + item.getChange());
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb3);
		final int count = _items.size();
		writeH(count);
		for (final ItemInfo item : _items)
		{
			writeH(item.getChange());
			writeH(item.getItem().getType1()); // item type1
			writeD(item.getObjectId());
			writeD(item.getItem().getItemId());
			writeD(item.getCount());
			writeH(item.getItem().getType2()); // item type2
			writeH(0x00); // ?
			writeH(item.getEquipped());
			// writeH(temp.getItem().getBodyPart()); // rev 377 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeD(item.getItem().getBodyPart()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(item.getEnchant()); // enchant level
			writeH(0x00); // ?
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__37_INVENTORYUPDATE;
	}
}
