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
package com.l2jprime.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.controllers.GameTimeController;
import com.l2jprime.gameserver.datatables.sql.ItemTable;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance.ItemLocation;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.templates.L2Item;
import com.l2jprime.util.CloseUtil;
import com.l2jprime.util.database.DatabaseUtils;
import com.l2jprime.util.database.L2DatabaseFactory;

import javolution.util.FastList;

/**
 * @author Advi
 */
public abstract class ItemContainer
{
	protected static final Logger LOGGER = Logger.getLogger(ItemContainer.class);

	protected final List<L2ItemInstance> _items;

	protected ItemContainer()
	{
		_items = new FastList<>();
	}

	protected abstract L2Character getOwner();

	protected abstract ItemLocation getBaseLocation();

	/**
	 * Returns the ownerID of the inventory
	 * @return int
	 */
	public int getOwnerId()
	{
		return getOwner() == null ? 0 : getOwner().getObjectId();
	}

	/**
	 * Returns the quantity of items in the inventory
	 * @return int
	 */
	public int getSize()
	{
		return _items.size();
	}

	/**
	 * Returns the list of items in inventory
	 * @return L2ItemInstance : items in inventory
	 */
	public L2ItemInstance[] getItems()
	{
		synchronized (_items)
		{
			return _items.toArray(new L2ItemInstance[_items.size()]);
		}
	}

	/**
	 * Returns the item from inventory by using its <B>itemId</B><BR>
	 * <BR>
	 * @param itemId : int designating the ID of the item
	 * @return L2ItemInstance designating the item or null if not found in inventory
	 */
	public L2ItemInstance getItemByItemId(final int itemId)
	{
		for (final L2ItemInstance item : _items)
		{
			if ((item != null) && (item.getItemId() == itemId))
			{
				return item;
			}
		}
		
		return null;
	}

	/**
	 * Returns the item from inventory by using its <B>itemId</B><BR>
	 * <BR>
	 * @param itemId : int designating the ID of the item
	 * @param itemToIgnore : used during a loop, to avoid returning the same item
	 * @return L2ItemInstance designating the item or null if not found in inventory
	 */
	public L2ItemInstance getItemByItemId(final int itemId, final L2ItemInstance itemToIgnore)
	{
		for (final L2ItemInstance item : _items)
		{
			if ((item != null) && (item.getItemId() == itemId) && !item.equals(itemToIgnore))
			{
				return item;
			}
		}
		
		return null;
	}

	/**
	 * Returns item from inventory by using its <B>objectId</B>
	 * @param objectId : int designating the ID of the object
	 * @return L2ItemInstance designating the item or null if not found in inventory
	 */
	public L2ItemInstance getItemByObjectId(final int objectId)
	{
		for (final L2ItemInstance item : _items)
		{
			if (item == null)
			{
				_items.remove(item);
				continue;
			}

			if (item.getObjectId() == objectId)
			{
				return item;
			}
		}
		return null;
	}

	/**
	 * Gets count of item in the inventory
	 * @param itemId : Item to look for
	 * @param enchantLevel : enchant level to match on, or -1 for ANY enchant level
	 * @return int corresponding to the number of items matching the above conditions.
	 */
	public int getInventoryItemCount(final int itemId, final int enchantLevel)
	{
		int count = 0;

		for (final L2ItemInstance item : _items)
		{
			if ((item != null) && (item.getItemId() == itemId) && ((item.getEnchantLevel() == enchantLevel) || (enchantLevel < 0)))
			{
				// if (item.isAvailable((L2PcInstance)getOwner(), true) || item.getItem().getType2() == 3)//available or quest item
				if (item.isStackable())
				{
					count = item.getCount();
				}
				else
				{
					count++;
				}
			}
		}
		
		return count;
	}

	/**
	 * Adds item to inventory
	 * @param process : String Identifier of process triggering this action
	 * @param item : L2ItemInstance to be added
	 * @param actor : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public L2ItemInstance addItem(final String process, L2ItemInstance item, final L2PcInstance actor, final L2Object reference)
	{
		L2ItemInstance olditem = getItemByItemId(item.getItemId());

		// If stackable item is found in inventory just add to current quantity
		if ((olditem != null) && olditem.isStackable())
		{
			final int count = item.getCount();
			olditem.changeCount(process, count, actor, reference);
			olditem.setLastChange(L2ItemInstance.MODIFIED);

			// And destroys the item
			ItemTable.getInstance().destroyItem(process, item, actor, reference);
			item.updateDatabase();
			item = olditem;

			// Updates database
			if ((item.getItemId() == 57) && (count < (10000 * Config.RATE_DROP_ADENA)))
			{
				// Small adena changes won't be saved to database all the time
				if ((GameTimeController.getGameTicks() % 5) == 0)
				{
					item.updateDatabase();
				}
			}
			else
			{
				item.updateDatabase();
			}
		}
		// If item hasn't be found in inventory, create new one
		else
		{
			item.setOwnerId(process, getOwnerId(), actor, reference);
			item.setLocation(getBaseLocation());
			item.setLastChange(L2ItemInstance.ADDED);

			// Add item in inventory
			addItem(item);

			// Updates database
			item.updateDatabase();
		}

		refreshWeight();

		olditem = null;

		return item;
	}

	/**
	 * Adds item to inventory
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be added
	 * @param count : int Quantity of items to be added
	 * @param actor : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public L2ItemInstance addItem(final String process, final int itemId, final int count, final L2PcInstance actor, final L2Object reference)
	{
		L2ItemInstance item = getItemByItemId(itemId);

		// If stackable item is found in inventory just add to current quantity
		if ((item != null) && item.isStackable())
		{
			item.changeCount(process, count, actor, reference);
			item.setLastChange(L2ItemInstance.MODIFIED);

			// Updates database
			if ((itemId == 57) && (count < (10000 * Config.RATE_DROP_ADENA)))
			{
				// Small adena changes won't be saved to database all the time
				if ((GameTimeController.getGameTicks() % 5) == 0)
				{
					item.updateDatabase();
				}
			}
			else
			{
				item.updateDatabase();
			}
		}
		// If item hasn't be found in inventory, create new one
		else
		{
			for (int i = 0; i < count; i++)
			{
				L2Item template = ItemTable.getInstance().getTemplate(itemId);

				if (template == null)
				{
					LOGGER.warn((actor != null ? "[" + actor.getName() + "] " : "") + "Invalid ItemId requested: " + itemId);
					return null;
				}

				item = ItemTable.getInstance().createItem(process, itemId, template.isStackable() ? count : 1, actor, reference);
				item.setOwnerId(getOwnerId());

				if (process.equals("AutoLoot"))
				{
					item.setLocation(ItemLocation.INVENTORY);
				}
				else
				{
					item.setLocation(getBaseLocation());
				}

				item.setLastChange(L2ItemInstance.ADDED);

				// Add item in inventory
				addItem(item);
				// Updates database
				item.updateDatabase();

				// If stackable, end loop as entire count is included in 1 instance of item
				if (template.isStackable() || !Config.MULTIPLE_ITEM_DROP)
				{
					break;
				}

				template = null;
			}
		}

		refreshWeight();

		return item;
	}

	/**
	 * Adds Wear/Try On item to inventory<BR>
	 * <BR>
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be added
	 * @param actor : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new weared item
	 */
	public L2ItemInstance addWearItem(final String process, final int itemId, final L2PcInstance actor, final L2Object reference)
	{
		// Surch the item in the inventory of the player
		L2ItemInstance item = getItemByItemId(itemId);

		// There is such item already in inventory
		if (item != null)
		{
			return item;
		}
		
		// Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity
		// Add the L2ItemInstance object to _allObjects of L2world
		item = ItemTable.getInstance().createItem(process, itemId, 1, actor, reference);

		// Set Item Properties
		item.setWear(true); // "Try On" Item -> Don't save it in database
		item.setOwnerId(getOwnerId());
		item.setLocation(getBaseLocation());
		item.setLastChange(L2ItemInstance.ADDED);

		// Add item in inventory and equip it if necessary (item location defined)
		addItem(item);

		// Calculate the weight loaded by player
		refreshWeight();

		return item;
	}

	/**
	 * Transfers item to another inventory
	 * @param process : String Identifier of process triggering this action
	 * @param objectId
	 * @param count : int Quantity of items to be transfered
	 * @param target
	 * @param actor : L2PcInstance Player requesting the item transfer
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public L2ItemInstance transferItem(final String process, final int objectId, int count, final ItemContainer target, final L2PcInstance actor, final L2Object reference)
	{
		if (target == null)
		{
			return null;
		}

		L2ItemInstance sourceitem = getItemByObjectId(objectId);
		if (sourceitem == null)
		{
			return null;
		}

		L2ItemInstance targetitem = sourceitem.isStackable() ? target.getItemByItemId(sourceitem.getItemId()) : null;

		synchronized (sourceitem)
		{
			// check if this item still present in this container
			if (getItemByObjectId(objectId) != sourceitem)
			{
				return null;
			}

			// Check if requested quantity is available
			if (count > sourceitem.getCount())
			{
				count = sourceitem.getCount();
			}

			// If possible, move entire item object
			if ((sourceitem.getCount() == count) && (targetitem == null))
			{
				removeItem(sourceitem);
				target.addItem(process, sourceitem, actor, reference);
				targetitem = sourceitem;
			}
			else
			{
				if (sourceitem.getCount() > count) // If possible, only update counts
				{
					sourceitem.changeCount(process, -count, actor, reference);
				}
				else
				// Otherwise destroy old item
				{
					removeItem(sourceitem);
					ItemTable.getInstance().destroyItem(process, sourceitem, actor, reference);
				}

				if (targetitem != null) // If possible, only update counts
				{
					targetitem.changeCount(process, count, actor, reference);
				}
				else
				// Otherwise add new item
				{
					targetitem = target.addItem(process, sourceitem.getItemId(), count, actor, reference);
				}
			}

			// Updates database
			sourceitem.updateDatabase();
			if ((targetitem != sourceitem) && (targetitem != null))
			{
				targetitem.updateDatabase();
			}

			if (sourceitem.isAugmented())
			{
				sourceitem.getAugmentation().removeBoni(actor);
			}

			refreshWeight();
		}

		sourceitem = null;

		return targetitem;
	}

	/**
	 * Destroy item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param item : L2ItemInstance to be destroyed
	 * @param actor : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance destroyItem(final String process, final L2ItemInstance item, final L2PcInstance actor, final L2Object reference)
	{
		synchronized (item)
		{
			// check if item is present in this container
			if (!_items.contains(item))
			{
				return null;
			}

			removeItem(item);
			ItemTable.getInstance().destroyItem(process, item, actor, reference);

			item.updateDatabase();

			if (item.isVarkaKetraAllyQuestItem())
			{
				actor.setAllianceWithVarkaKetra(0);
			}

			refreshWeight();
		}

		return item;
	}

	/**
	 * Destroy item from inventory by using its <B>objectID</B> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param actor : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance destroyItem(final String process, final int objectId, final int count, final L2PcInstance actor, final L2Object reference)
	{
		final L2ItemInstance item = getItemByObjectId(objectId);

		if (item == null)
		{
			return null;
		}

		// Adjust item quantity
		if (item.getCount() > count)
		{
			synchronized (item)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(L2ItemInstance.MODIFIED);

				item.updateDatabase();
				refreshWeight();
			}

			return item;
		}
		// Directly drop entire item
		return destroyItem(process, item, actor, reference);
	}

	/**
	 * Destroy item from inventory by using its <B>itemId</B> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param actor : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public L2ItemInstance destroyItemByItemId(final String process, final int itemId, final int count, final L2PcInstance actor, final L2Object reference)
	{
		final L2ItemInstance item = getItemByItemId(itemId);

		if (item == null)
		{
			return null;
		}

		synchronized (item)
		{
			// Adjust item quantity
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(L2ItemInstance.MODIFIED);
			}
			// Directly drop entire item
			else
			{
				return destroyItem(process, item, actor, reference);
			}

			item.updateDatabase();
			refreshWeight();
		}

		return item;
	}

	/**
	 * Destroy all items from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param actor : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public synchronized void destroyAllItems(final String process, final L2PcInstance actor, final L2Object reference)
	{
		for (final L2ItemInstance item : _items)
		{
			destroyItem(process, item, actor, reference);
		}
	}

	/**
	 * Get warehouse adena
	 * @return
	 */
	public int getAdena()
	{
		int count = 0;

		for (final L2ItemInstance item : _items)
		{
			if (item.getItemId() == 57)
			{
				count = item.getCount();
				return count;
			}
		}

		return count;
	}

	/**
	 * Adds item to inventory for further adjustments.
	 * @param item : L2ItemInstance to be added from inventory
	 */
	protected void addItem(final L2ItemInstance item)
	{
		synchronized (_items)
		{
			_items.add(item);
		}
	}

	/**
	 * Removes item from inventory for further adjustments.
	 * @param item : L2ItemInstance to be removed from inventory
	 */
	protected void removeItem(final L2ItemInstance item)
	{
		synchronized (_items)
		{
			_items.remove(item);
		}
	}

	/**
	 * Refresh the weight of equipment loaded
	 */
	protected void refreshWeight()
	{
	}

	/**
	 * Delete item object from world
	 */
	public void deleteMe()
	{
		try
		{
			updateDatabase();
		}
		catch (final Throwable t)
		{
			LOGGER.error("deletedMe()", t);
		}

		List<L2Object> items = new FastList<>(_items);
		_items.clear();

		L2World.getInstance().removeObjects(items);
		items = null;
	}

	/**
	 * Update database with items in inventory
	 */
	public void updateDatabase()
	{
		if (getOwner() != null)
		{
			final List<L2ItemInstance> items = _items;

			if (items != null)
			{

				for (final L2ItemInstance item : items)
				{
					if (item != null)
					{
						item.updateDatabase();
					}
				}

			}

		}
	}

	/**
	 * Get back items in container from database
	 */
	public void restore()
	{
		final int ownerid = getOwnerId();
		final String baseLocation = getBaseLocation().name();

		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("SELECT object_id FROM items WHERE owner_id=? AND (loc=?) " + "ORDER BY object_id DESC");
			statement.setInt(1, ownerid);
			statement.setString(2, baseLocation);
			ResultSet inv = statement.executeQuery();

			L2ItemInstance item;

			while (inv.next())
			{
				final int objectId = inv.getInt(1);

				item = L2ItemInstance.restoreFromDb(objectId);

				if (item == null)
				{
					continue;
				}

				L2World.getInstance().storeObject(item);

				// If stackable item is found in inventory just add to current quantity
				if (item.isStackable() && (getItemByItemId(item.getItemId()) != null))
				{
					addItem("Restore", item, null, getOwner());
				}
				else
				{
					addItem(item);
				}
			}

			inv.close();
			DatabaseUtils.close(statement);

			inv = null;
			statement = null;
			item = null;
		}
		catch (final SQLException e)
		{
			LOGGER.warn("could not restore container:", e);
		}
		finally
		{
			CloseUtil.close(con);
		}

		refreshWeight();
	}

	public boolean validateCapacity(final int slots)
	{
		return true;
	}

	public boolean validateWeight(final int weight)
	{
		return true;
	}
}
