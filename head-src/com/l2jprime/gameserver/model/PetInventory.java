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
package com.l2jprime.gameserver.model;

import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance.ItemLocation;
import com.l2jprime.gameserver.model.actor.instance.L2PetInstance;

public class PetInventory extends Inventory
{
	private final L2PetInstance _owner;

	public PetInventory(final L2PetInstance owner)
	{
		_owner = owner;
	}

	@Override
	public L2PetInstance getOwner()
	{
		return _owner;
	}

	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.PET;
	}

	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PET_EQUIP;
	}
}
