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
package com.l2jprime.gameserver.model.actor.knownlist;

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;

public class NullKnownList extends ObjectKnownList
{

	/**
	 * @param activeObject
	 */
	public NullKnownList(final L2Object activeObject)
	{
		super(activeObject);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#addKnownObject(com.l2jprime.gameserver.model.L2Object, com.l2jprime.gameserver.model.L2Character)
	 */
	@Override
	public boolean addKnownObject(final L2Object object, final L2Character dropper)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#addKnownObject(com.l2jprime.gameserver.model.L2Object)
	 */
	@Override
	public boolean addKnownObject(final L2Object object)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#getActiveObject()
	 */
	@Override
	public L2Object getActiveObject()
	{
		return super.getActiveObject();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#getDistanceToForgetObject(com.l2jprime.gameserver.model.L2Object)
	 */
	@Override
	public int getDistanceToForgetObject(final L2Object object)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#getDistanceToWatchObject(com.l2jprime.gameserver.model.L2Object)
	 */
	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#removeAllKnownObjects() no-op
	 */
	@Override
	public void removeAllKnownObjects()
	{
		// null
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.knownlist.ObjectKnownList#removeKnownObject(com.l2jprime.gameserver.model.L2Object)
	 */
	@Override
	public boolean removeKnownObject(final L2Object object)
	{
		return false;
	}
}
