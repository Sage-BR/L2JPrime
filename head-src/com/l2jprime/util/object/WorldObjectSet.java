/*
 * $Header: WorldObjectSet.java, 22/07/2005 14:11:29 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 22/07/2005 14:11:29 $
 * $Revision: 1 $
 * $Log: WorldObjectSet.java,v $
 * Revision 1  22/07/2005 14:11:29  luisantonioa
 * Added copyright notice
 *
 *
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
package com.l2jprime.util.object;

import java.util.Iterator;
import java.util.Map;

import com.l2jprime.gameserver.model.L2Object;

import javolution.util.FastMap;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 * @param <T>
 */
public class WorldObjectSet<T extends L2Object> extends L2ObjectSet<T>
{
	private final Map<Integer, T> _objectMap;

	public WorldObjectSet()
	{
		_objectMap = new FastMap<Integer, T>().shared();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#size()
	 */
	@Override
	public int size()
	{
		return _objectMap.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return _objectMap.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#clear()
	 */
	@Override
	public void clear()
	{
		_objectMap.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#put(T)
	 */
	@Override
	public void put(final T obj)
	{
		_objectMap.put(obj.getObjectId(), obj);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#remove(T)
	 */
	@Override
	public void remove(final T obj)
	{
		_objectMap.remove(obj.getObjectId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#contains(T)
	 */
	@Override
	public boolean contains(final T obj)
	{
		return _objectMap.containsKey(obj.getObjectId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.util.L2ObjectSet#iterator()
	 */
	@Override
	public Iterator<T> iterator()
	{
		return _objectMap.values().iterator();
	}

}
