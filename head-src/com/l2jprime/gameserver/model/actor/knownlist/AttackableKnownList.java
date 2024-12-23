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

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.ai.L2CharacterAI;
import com.l2jprime.gameserver.model.L2Attackable;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.actor.instance.L2FolkInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PlayableInstance;

public class AttackableKnownList extends NpcKnownList
{
	// =========================================================
	// Data Field

	// =========================================================
	// Constructor
	public AttackableKnownList(final L2Attackable activeChar)
	{
		super(activeChar);
	}

	// =========================================================
	// Method - Public
	@Override
	public boolean removeKnownObject(final L2Object object)
	{
		if (!super.removeKnownObject(object))
		{
			return false;
		}

		// Remove the L2Object from the _aggrolist of the L2Attackable
		if ((object != null) && (object instanceof L2Character))
		{
			getActiveChar().getAggroList().remove(object);
		}

		// Set the L2Attackable Intention to AI_INTENTION_IDLE
		final Collection<L2PcInstance> known = getKnownPlayers().values();

		// FIXME: This is a temporary solution
		L2CharacterAI ai = getActiveChar().getAI();
		if ((ai != null) && ((known == null) || known.isEmpty()))
		{
			ai.setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}

		ai = null;

		return true;
	}

	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public
	@Override
	public L2Attackable getActiveChar()
	{
		return (L2Attackable) super.getActiveChar();
	}

	@Override
	public int getDistanceToForgetObject(final L2Object object)
	{
		if (getActiveChar().getAggroListRP() != null)
		{
			if (getActiveChar().getAggroListRP().get(object) != null)
			{
				return 3000;
			}
		}
		return Math.min(2200, 2 * getDistanceToWatchObject(object));
	}

	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		if ((object instanceof L2FolkInstance) || !(object instanceof L2Character))
		{
			return 0;
		}

		if (object instanceof L2PlayableInstance)
		{
			return 1500;
		}

		if (getActiveChar().getAggroRange() > getActiveChar().getFactionRange())
		{
			return getActiveChar().getAggroRange();
		}

		if (getActiveChar().getFactionRange() > 200)
		{
			return getActiveChar().getFactionRange();
		}

		return 200;
	}
}
