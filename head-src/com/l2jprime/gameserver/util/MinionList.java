/*
 * $Header: MinionList.java, 25/10/2005 18:42:48 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 25/10/2005 18:42:48 $
 * $Revision: 1 $
 * $Log: MinionList.java,v $
 * Revision 1  25/10/2005 18:42:48  luisantonioa
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
package com.l2jprime.gameserver.util;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.datatables.sql.NpcTable;
import com.l2jprime.gameserver.idfactory.IdFactory;
import com.l2jprime.gameserver.model.L2MinionData;
import com.l2jprime.gameserver.model.actor.instance.L2MinionInstance;
import com.l2jprime.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jprime.gameserver.templates.L2NpcTemplate;
import com.l2jprime.util.random.Rnd;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public class MinionList
{
	private static Logger LOGGER = Logger.getLogger(L2MonsterInstance.class);

	/** List containing the current spawned minions for this L2MonsterInstance */
	private final List<L2MinionInstance> minionReferences;
	protected FastMap<Long, Integer> _respawnTasks = new FastMap<Long, Integer>().shared();
	private final L2MonsterInstance master;

	public MinionList(final L2MonsterInstance pMaster)
	{
		minionReferences = new FastList<>();
		master = pMaster;
	}

	public int countSpawnedMinions()
	{
		synchronized (minionReferences)
		{
			return minionReferences.size();
		}
	}

	public int countSpawnedMinionsById(final int minionId)
	{
		int count = 0;
		synchronized (minionReferences)
		{
			for (final L2MinionInstance minion : getSpawnedMinions())
			{
				if (minion.getNpcId() == minionId)
				{
					count++;
				}
			}
		}
		return count;
	}

	public boolean hasMinions()
	{
		return getSpawnedMinions().size() > 0;
	}

	public List<L2MinionInstance> getSpawnedMinions()
	{
		return minionReferences;
	}

	public void addSpawnedMinion(final L2MinionInstance minion)
	{
		synchronized (minionReferences)
		{
			minionReferences.add(minion);
		}
	}

	public int lazyCountSpawnedMinionsGroups()
	{
		final Set<Integer> seenGroups = new FastSet<>();
		for (final L2MinionInstance minion : getSpawnedMinions())
		{
			seenGroups.add(minion.getNpcId());
		}
		return seenGroups.size();
	}

	public void removeSpawnedMinion(final L2MinionInstance minion)
	{
		synchronized (minionReferences)
		{
			minionReferences.remove(minion);
		}
	}

	public void moveMinionToRespawnList(final L2MinionInstance minion)
	{
		final Long current = System.currentTimeMillis();
		synchronized (minionReferences)
		{
			minionReferences.remove(minion);
			if (_respawnTasks.get(current) == null)
			{
				_respawnTasks.put(current, minion.getNpcId());
			}
			else
			{
				// nice AoE
				for (int i = 1; i < 30; i++)
				{
					if (_respawnTasks.get(current + i) == null)
					{
						_respawnTasks.put(current + i, minion.getNpcId());
						break;
					}
				}
			}
		}
	}

	public void clearRespawnList()
	{
		_respawnTasks.clear();
	}

	/**
	 * Manage respawning of minions for this RaidBoss.<BR>
	 * <BR>
	 */
	public void maintainMinions()
	{
		if ((master == null) || master.isAlikeDead())
		{
			return;
		}

		final Long current = System.currentTimeMillis();

		if (_respawnTasks != null)
		{
			for (final long deathTime : _respawnTasks.keySet())
			{
				final double delay = Config.RAID_MINION_RESPAWN_TIMER;

				if ((current - deathTime) > delay)
				{
					spawnSingleMinion(_respawnTasks.get(deathTime));
					_respawnTasks.remove(deathTime);
				}
			}
		}
	}

	/**
	 * Manage the spawn of all Minions of this RaidBoss.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the Minion data of all Minions that must be spawn</li>
	 * <li>For each Minion type, spawn the amount of Minion needed</li><BR>
	 */
	public void spawnMinions()
	{
		if ((master == null) || master.isAlikeDead())
		{
			return;
		}

		final List<L2MinionData> minions = master.getTemplate().getMinionData();

		synchronized (minionReferences)
		{
			int minionCount, minionId, minionsToSpawn;

			for (final L2MinionData minion : minions)
			{
				minionCount = minion.getAmount();
				minionId = minion.getMinionId();

				minionsToSpawn = minionCount - countSpawnedMinionsById(minionId);

				for (int i = 0; i < minionsToSpawn; i++)
				{
					spawnSingleMinion(minionId);
				}
			}
		}
	}

	/**
	 * Init a Minion and add it in the world as a visible object.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the template of the Minion to spawn</li>
	 * <li>Create and Init the Minion and generate its Identifier</li>
	 * <li>Set the Minion HP, MP and Heading</li>
	 * <li>Set the Minion leader to this RaidBoss</li>
	 * <li>Init the position of the Minion and add it in the world as a visible object</li><BR>
	 * <BR>
	 * @param minionid The I2NpcTemplate Identifier of the Minion to spawn
	 */
	public void spawnSingleMinion(final int minionid)
	{
		// Get the template of the Minion to spawn
		final L2NpcTemplate minionTemplate = NpcTable.getInstance().getTemplate(minionid);

		// Create and Init the Minion and generate its Identifier
		final L2MinionInstance monster = new L2MinionInstance(IdFactory.getInstance().getNextId(), minionTemplate);

		// Set the Minion HP, MP and Heading
		monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
		monster.setHeading(master.getHeading());

		// Set the Minion leader to this RaidBoss
		monster.setLeader(master);

		// Init the position of the Minion and add it in the world as a visible object
		int spawnConstant;
		final int randSpawnLim = 170;
		int randPlusMin = 1;
		spawnConstant = Rnd.nextInt(randSpawnLim);
		// randomize +/-
		randPlusMin = Rnd.nextInt(2);
		if (randPlusMin == 1)
		{
			spawnConstant *= -1;
		}

		final int newX = master.getX() + spawnConstant;
		spawnConstant = Rnd.nextInt(randSpawnLim);
		// randomize +/-
		randPlusMin = Rnd.nextInt(2);

		if (randPlusMin == 1)
		{
			spawnConstant *= -1;
		}

		final int newY = master.getY() + spawnConstant;

		monster.spawnMe(newX, newY, master.getZ());

		if (Config.DEBUG)
		{
			LOGGER.debug("Spawned minion template " + minionTemplate.npcId + " with objid: " + monster.getObjectId() + " to boss " + master.getObjectId() + " ,at: " + monster.getX() + " x, " + monster.getY() + " y, " + monster.getZ() + " z");
		}
	}
}
