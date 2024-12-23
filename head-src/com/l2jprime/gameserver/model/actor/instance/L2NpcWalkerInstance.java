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
package com.l2jprime.gameserver.model.actor.instance;

import java.util.Map;

import com.l2jprime.Config;
import com.l2jprime.gameserver.ai.L2CharacterAI;
import com.l2jprime.gameserver.ai.L2NpcWalkerAI;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.network.serverpackets.CreatureSay;
import com.l2jprime.gameserver.templates.L2NpcTemplate;

/**
 * This class manages some npcs can walk in the city. <br>
 * It inherits all methods from L2NpcInstance. <br>
 * <br>
 * @original author Rayan RPG
 * @since 819
 */
public class L2NpcWalkerInstance extends L2NpcInstance
{

	/**
	 * Constructor of L2NpcWalkerInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <BR>
	 * @param objectId the object id
	 * @param template the template
	 */
	public L2NpcWalkerInstance(final int objectId, final L2NpcTemplate template)
	{
		super(objectId, template);
		setAI(new L2NpcWalkerAI(new L2NpcWalkerAIAccessor()));
	}

	/**
	 * AI can't be deattached, npc must move always with the same AI instance.
	 * @param newAI AI to set for this L2NpcWalkerInstance
	 */
	@Override
	public void setAI(final L2CharacterAI newAI)
	{
		if (_ai == null)
		{
			super.setAI(newAI);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.actor.instance.L2NpcInstance#onSpawn()
	 */
	@Override
	public void onSpawn()
	{
		((L2NpcWalkerAI) getAI()).setHomeX(getX());
		((L2NpcWalkerAI) getAI()).setHomeY(getY());
		((L2NpcWalkerAI) getAI()).setHomeZ(getZ());
	}

	/**
	 * Sends a chat to all _knowObjects.
	 * @param chat message to say
	 */
	public void broadcastChat(final String chat)
	{
		final Map<Integer, L2PcInstance> _knownPlayers = getKnownList().getKnownPlayers();

		if (_knownPlayers == null)
		{
			if (Config.DEVELOPER)
			{
				LOGGER.info("broadcastChat _players == null");
			}
			return;
		}

		// we send message to known players only!
		if (_knownPlayers.size() > 0)
		{
			CreatureSay cs = new CreatureSay(getObjectId(), 0, getName(), chat);

			// we interact and list players here
			for (final L2PcInstance players : _knownPlayers.values())
			{
				// finally send packet :D
				players.sendPacket(cs);
			}

			cs = null;
		}
	}

	/**
	 * NPCs are immortal.
	 * @param i ignore it
	 * @param attacker ignore it
	 * @param awake ignore it
	 */
	@Override
	public void reduceCurrentHp(final double i, final L2Character attacker, final boolean awake)
	{
	}

	/**
	 * NPCs are immortal.
	 * @param killer ignore it
	 * @return false
	 */
	@Override
	public boolean doDie(final L2Character killer)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getAI()
	 */
	@Override
	public L2CharacterAI getAI()
	{
		return super.getAI();
	}

	/**
	 * The Class L2NpcWalkerAIAccessor.
	 */
	protected class L2NpcWalkerAIAccessor extends L2Character.AIAccessor
	{
		/**
		 * AI can't be deattached.
		 */
		@Override
		public void detachAI()
		{
		}
	}
}
