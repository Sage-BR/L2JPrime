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

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.L2Summon;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.actor.instance.L2SiegeGuardInstance;

public class SiegeGuardKnownList extends AttackableKnownList
{
	// =========================================================
	// Data Field

	// =========================================================
	// Constructor
	public SiegeGuardKnownList(final L2SiegeGuardInstance activeChar)
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

		if (getActiveChar().getHomeX() == 0)
		{
			getActiveChar().getHomeLocation();
		}

		// Check if siege is in progress
		if ((getActiveChar().getCastle() != null) && getActiveChar().getCastle().getSiege().getIsInProgress())
		{
			L2PcInstance player = null;

			if (object instanceof L2PcInstance)
			{
				player = (L2PcInstance) object;
			}
			else if (object instanceof L2Summon)
			{
				player = ((L2Summon) object).getOwner();
			}

			// Check if player is not the defender
			if ((player != null) && ((player.getClan() == null) || (getActiveChar().getCastle().getSiege().getAttackerClan(player.getClan()) != null)))
			{
				// if (Config.DEBUG) LOGGER.fine(getObjectId()+": PK "+player.getObjectId()+" entered scan range");
				if (getActiveChar().getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				{
					getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);// (L2Character)object);
				}
			}

			player = null;

		}

		return true;
	}

	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public
	@Override
	public final L2SiegeGuardInstance getActiveChar()
	{
		return (L2SiegeGuardInstance) super.getActiveChar();
	}
}
