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
package com.l2jprime.gameserver.model.actor.knownlist;

import java.util.Collection;
import java.util.Map;

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jprime.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.util.Util;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CharKnownList extends ObjectKnownList
{
	// =========================================================
	// Data Field
	private Map<Integer, L2PcInstance> _knownPlayers;
	private Map<Integer, Integer> _knownRelations;

	// =========================================================
	// Constructor
	public CharKnownList(final L2Character activeChar)
	{
		super(activeChar);
	}

	// =========================================================
	// Method - Public
	@Override
	public boolean addKnownObject(final L2Object object)
	{
		return addKnownObject(object, null);
	}

	@Override
	public boolean addKnownObject(final L2Object object, final L2Character dropper)
	{
		if (!super.addKnownObject(object, dropper))
		{
			return false;
		}

		if (object instanceof L2PcInstance)
		{
			getKnownPlayers().put(object.getObjectId(), (L2PcInstance) object);
			getKnownRelations().put(object.getObjectId(), -1);
		}
		return true;
	}

	/**
	 * @param player The L2PcInstance to search in _knownPlayer
	 * @return True if the L2PcInstance is in _knownPlayer of the L2Character.
	 */
	public final boolean knowsThePlayer(final L2PcInstance player)
	{
		return (getActiveChar() == player) || getKnownPlayers().containsKey(player.getObjectId());
	}

	/**
	 * Remove all L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI.
	 */
	@Override
	public final void removeAllKnownObjects()
	{
		super.removeAllKnownObjects();
		getKnownPlayers().clear();
		getKnownRelations().clear();

		// Set _target of the L2Character to null
		// Cancel Attack or Cast
		getActiveChar().setTarget(null);

		// Cancel AI Task
		if (getActiveChar().hasAI())
		{
			getActiveChar().setAI(null);
		}
	}

	@Override
	public boolean removeKnownObject(final L2Object object)
	{
		if (!super.removeKnownObject(object))
		{
			return false;
		}

		if (object instanceof L2PcInstance)
		{
			getKnownPlayers().remove(object.getObjectId());
			getKnownRelations().remove(object.getObjectId());
		}
		// If object is targeted by the L2Character, cancel Attack or Cast
		if (object == getActiveChar().getTarget())
		{
			getActiveChar().setTarget(null);
		}

		return true;
	}

	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public
	public L2Character getActiveChar()
	{
		return (L2Character) super.getActiveObject();
	}

	@Override
	public int getDistanceToForgetObject(final L2Object object)
	{
		return 0;
	}

	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		return 0;
	}

	public Collection<L2Character> getKnownCharacters()
	{
		final FastList<L2Character> result = new FastList<>();

		for (final L2Object obj : getKnownObjects().values())
		{
			if ((obj != null) && (obj instanceof L2Character))
			{
				result.add((L2Character) obj);
			}
		}

		return result;
	}

	public Collection<L2Character> getKnownCharactersInRadius(final long radius)
	{
		final FastList<L2Character> result = new FastList<>();

		for (final L2Object obj : getKnownObjects().values())
		{
			if (obj instanceof L2PcInstance)
			{
				if (Util.checkIfInRange((int) radius, getActiveChar(), obj, true))
				{
					result.add((L2PcInstance) obj);
				}
			}
			else if (obj instanceof L2MonsterInstance)
			{
				if (Util.checkIfInRange((int) radius, getActiveChar(), obj, true))
				{
					result.add((L2MonsterInstance) obj);
				}
			}
			else if (obj instanceof L2NpcInstance)
			{
				if (Util.checkIfInRange((int) radius, getActiveChar(), obj, true))
				{
					result.add((L2NpcInstance) obj);
				}
			}
		}

		return result;
	}

	public final Map<Integer, L2PcInstance> getKnownPlayers()
	{
		if (_knownPlayers == null)
		{
			_knownPlayers = new FastMap<Integer, L2PcInstance>().shared();
		}

		return _knownPlayers;
	}

	public final Map<Integer, Integer> getKnownRelations()
	{
		if (_knownRelations == null)
		{
			_knownRelations = new FastMap<Integer, Integer>().shared();
		}

		return _knownRelations;
	}

	public final Collection<L2PcInstance> getKnownPlayersInRadius(final long radius)
	{
		final FastList<L2PcInstance> result = new FastList<>();

		for (final L2PcInstance player : getKnownPlayers().values())
		{
			if (Util.checkIfInRange((int) radius, getActiveChar(), player, true))
			{
				result.add(player);
			}
		}

		return result;
	}
}
