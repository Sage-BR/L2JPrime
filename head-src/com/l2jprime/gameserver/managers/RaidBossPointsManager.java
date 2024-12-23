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
package com.l2jprime.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.util.CloseUtil;
import com.l2jprime.util.database.DatabaseUtils;
import com.l2jprime.util.database.L2DatabaseFactory;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Kerberos
 */

public class RaidBossPointsManager
{
	private final static Logger LOGGER = Logger.getLogger(RaidBossPointsManager.class);
	protected static FastMap<Integer, Map<Integer, Integer>> _list;

	private static final Comparator<Map.Entry<Integer, Integer>> _comparator = new Comparator<Map.Entry<Integer, Integer>>()
	{
		@Override
		public int compare(final Map.Entry<Integer, Integer> entry, final Map.Entry<Integer, Integer> entry1)
		{
			return entry.getValue().equals(entry1.getValue()) ? 0 : entry.getValue() < entry1.getValue() ? 1 : -1;
		}
	};

	public final static void init()
	{
		_list = new FastMap<>();
		final FastList<Integer> _chars = new FastList<>();
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("SELECT * FROM `character_raid_points`");
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				_chars.add(rset.getInt("charId"));
			}
			DatabaseUtils.close(rset);
			DatabaseUtils.close(statement);
			for (FastList.Node<Integer> n = _chars.head(), end = _chars.tail(); (n = n.getNext()) != end;)
			{
				final int charId = n.getValue();
				final FastMap<Integer, Integer> values = new FastMap<>();
				statement = con.prepareStatement("SELECT * FROM `character_raid_points` WHERE `charId`=?");
				statement.setInt(1, charId);
				rset = statement.executeQuery();
				while (rset.next())
				{
					values.put(rset.getInt("boss_id"), rset.getInt("points"));
				}
				DatabaseUtils.close(rset);
				DatabaseUtils.close(statement);
				_list.put(charId, values);
			}
		}
		catch (final SQLException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.warn("RaidPointsManager: Couldnt load raid points ");
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.warn(e.getMessage());
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}

	public final static void updatePointsInDB(final L2PcInstance player, final int raidId, final int points)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement;
			statement = con.prepareStatement("REPLACE INTO character_raid_points (`charId`,`boss_id`,`points`) VALUES (?,?,?)");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, raidId);
			statement.setInt(3, points);
			statement.executeUpdate();
			DatabaseUtils.close(statement);
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.warn("could not update char raid points:", e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}

	public final static void addPoints(final L2PcInstance player, final int bossId, final int points)
	{
		final int ownerId = player.getObjectId();
		Map<Integer, Integer> tmpPoint = new FastMap<>();
		if (_list == null)
		{
			_list = new FastMap<>();
		}
		tmpPoint = _list.get(ownerId);
		if ((tmpPoint == null) || tmpPoint.isEmpty())
		{
			tmpPoint = new FastMap<>();
			tmpPoint.put(bossId, points);
			updatePointsInDB(player, bossId, points);
		}
		else
		{
			final int currentPoins = tmpPoint.containsKey(bossId) ? tmpPoint.get(bossId).intValue() : 0;
			tmpPoint.remove(bossId);
			tmpPoint.put(bossId, currentPoins == 0 ? points : currentPoins + points);
			updatePointsInDB(player, bossId, currentPoins == 0 ? points : currentPoins + points);
		}
		_list.remove(ownerId);
		_list.put(ownerId, tmpPoint);
	}

	public final static int getPointsByOwnerId(final int ownerId)
	{
		Map<Integer, Integer> tmpPoint = new FastMap<>();
		if (_list == null)
		{
			_list = new FastMap<>();
		}
		tmpPoint = _list.get(ownerId);
		int totalPoints = 0;

		if ((tmpPoint == null) || tmpPoint.isEmpty())
		{
			return 0;
		}

		for (final int bossId : tmpPoint.keySet())
		{
			totalPoints += tmpPoint.get(bossId);
		}
		return totalPoints;
	}

	public final static Map<Integer, Integer> getList(final L2PcInstance player)
	{
		return _list.get(player.getObjectId());
	}

	public final static void cleanUp()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement;
			statement = con.prepareStatement("DELETE from character_raid_points WHERE charId > 0");
			statement.executeUpdate();
			DatabaseUtils.close(statement);
			_list.clear();
			_list = new FastMap<>();
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.warn("could not clean raid points: ", e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}

	public final static int calculateRanking(final int playerObjId)
	{
		final Map<Integer, Integer> tmpRanking = new FastMap<>();
		final Map<Integer, Integer> tmpPoints = new FastMap<>();
		int totalPoints;

		for (final int ownerId : _list.keySet())
		{
			totalPoints = getPointsByOwnerId(ownerId);
			if (totalPoints != 0)
			{
				tmpPoints.put(ownerId, totalPoints);
			}
		}
		final ArrayList<Entry<Integer, Integer>> list = new ArrayList<>(tmpPoints.entrySet());

		Collections.sort(list, _comparator);

		int ranking = 1;
		for (final Map.Entry<Integer, Integer> entry : list)
		{
			tmpRanking.put(entry.getKey(), ranking++);
		}

		if (tmpRanking.containsKey(playerObjId))
		{
			return tmpRanking.get(playerObjId);
		}
		return 0;
	}

	public static Map<Integer, Integer> getRankList()
	{
		final Map<Integer, Integer> tmpRanking = new FastMap<>();
		final Map<Integer, Integer> tmpPoints = new FastMap<>();
		int totalPoints;

		for (final int ownerId : _list.keySet())
		{
			totalPoints = getPointsByOwnerId(ownerId);
			if (totalPoints != 0)
			{
				tmpPoints.put(ownerId, totalPoints);
			}
		}
		final ArrayList<Entry<Integer, Integer>> list = new ArrayList<>(tmpPoints.entrySet());

		Collections.sort(list, _comparator);

		int ranking = 1;
		for (final Map.Entry<Integer, Integer> entry : list)
		{
			tmpRanking.put(entry.getKey(), ranking++);
		}

		return tmpRanking;
	}
}