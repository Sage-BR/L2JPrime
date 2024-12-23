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

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.ai.L2CharacterAI;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.actor.instance.L2GuardNoHTMLInstance;
import com.l2jprime.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

public class GuardNoHTMLKnownList extends AttackableKnownList
{
	private static Logger LOGGER = Logger.getLogger(GuardKnownList.class);

	// =========================================================
	// Data Field

	// =========================================================
	// Constructor
	public GuardNoHTMLKnownList(final L2GuardNoHTMLInstance activeChar)
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

		// Set home location of the L2GuardInstance (if not already done)
		if (getActiveChar().getHomeX() == 0)
		{
			getActiveChar().getHomeLocation();
		}

		if (object instanceof L2PcInstance)
		{
			// Check if the object added is a L2PcInstance that owns Karma
			L2PcInstance player = (L2PcInstance) object;

			if (player.getKarma() > 0)
			{
				if (Config.DEBUG)
				{
					LOGGER.debug(getActiveChar().getObjectId() + ": PK " + player.getObjectId() + " entered scan range");
				}

				// Set the L2GuardInstance Intention to AI_INTENTION_ACTIVE
				if (getActiveChar().getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				{
					getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);
				}
			}

			player = null;
		}
		else if (Config.ALLOW_GUARDS && (object instanceof L2MonsterInstance))
		{
			// Check if the object added is an aggressive L2MonsterInstance
			L2MonsterInstance mob = (L2MonsterInstance) object;

			if (mob.isAggressive())
			{
				if (Config.DEBUG)
				{
					LOGGER.debug(getActiveChar().getObjectId() + ": Aggressive mob " + mob.getObjectId() + " entered scan range");
				}

				// Set the L2GuardInstance Intention to AI_INTENTION_ACTIVE
				if (getActiveChar().getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				{
					getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);
				}
			}

			mob = null;
		}

		return true;
	}

	@Override
	public boolean removeKnownObject(final L2Object object)
	{
		if (!super.removeKnownObject(object))
		{
			return false;
		}

		// Check if the _aggroList of the L2GuardInstance is Empty
		if (getActiveChar().noTarget())
		{
			// removeAllKnownObjects();
			// Set the L2GuardInstance to AI_INTENTION_IDLE
			final L2CharacterAI ai = getActiveChar().getAI();
			if (ai != null)
			{
				ai.setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
			}
		}

		return true;
	}

	// =========================================================
	// Method - Private
	// =========================================================
	// Property - Public
	@Override
	public final L2GuardNoHTMLInstance getActiveChar()
	{
		return (L2GuardNoHTMLInstance) super.getActiveChar();
	}
}