/*
 * l2jprime Project - www.4teambr.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jprime.gameserver.datatables;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * This class stores references to all online game masters. (access level > 100)
 * @version $Revision: 1.2.2.1.2.7 $ $Date: 2005/04/05 19:41:24 $
 */
public class GmListTable
{
	protected static final Logger LOGGER = Logger.getLogger(GmListTable.class);
	private static GmListTable _instance;

	/** Set(L2PcInstance>) containing all the GM in game */
	private final FastMap<L2PcInstance, Boolean> _gmList;

	public static GmListTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new GmListTable();
		}

		return _instance;
	}

	public static void reload()
	{
		_instance = null;
		getInstance();
	}

	public FastList<L2PcInstance> getAllGms(final boolean includeHidden)
	{
		final FastList<L2PcInstance> tmpGmList = new FastList<>();

		for (FastMap.Entry<L2PcInstance, Boolean> n = _gmList.head(), end = _gmList.tail(); (n = n.getNext()) != end;)
		{
			if (includeHidden || !n.getValue())
			{
				tmpGmList.add(n.getKey());
			}
		}
		return tmpGmList;
	}

	public FastList<String> getAllGmNames(final boolean includeHidden)
	{
		final FastList<String> tmpGmList = new FastList<>();

		for (FastMap.Entry<L2PcInstance, Boolean> n = _gmList.head(), end = _gmList.tail(); (n = n.getNext()) != end;)
		{
			if (!n.getValue())
			{
				tmpGmList.add(n.getKey().getName());
			}
			else if (includeHidden)
			{
				tmpGmList.add(n.getKey().getName() + " (invis)");
			}
		}
		return tmpGmList;
	}

	private GmListTable()
	{
		LOGGER.info("GmListTable: initalized.");
		_gmList = new FastMap<L2PcInstance, Boolean>().shared();
	}

	/**
	 * Add a L2PcInstance player to the Set _gmList
	 * @param player
	 * @param hidden
	 */
	public void addGm(final L2PcInstance player, final boolean hidden)
	{
		if (Config.DEBUG)
		{
			LOGGER.debug("added gm: " + player.getName());
		}

		_gmList.put(player, hidden);
	}

	public void deleteGm(final L2PcInstance player)
	{
		if (Config.DEBUG)
		{
			LOGGER.debug("deleted gm: " + player.getName());
		}

		_gmList.remove(player);
	}

	/**
	 * GM will be displayed on clients gmlist
	 * @param player
	 */
	public void showGm(final L2PcInstance player)
	{
		final FastMap.Entry<L2PcInstance, Boolean> gm = _gmList.getEntry(player);

		if (gm != null)
		{
			gm.setValue(false);
		}
	}

	/**
	 * GM will no longer be displayed on clients gmlist
	 * @param player
	 */
	public void hideGm(final L2PcInstance player)
	{
		final FastMap.Entry<L2PcInstance, Boolean> gm = _gmList.getEntry(player);

		if (gm != null)
		{
			gm.setValue(true);
		}
	}

	public boolean isGmOnline(final boolean includeHidden)
	{
		for (final boolean b : _gmList.values())
		{
			if (includeHidden || !b)
			{
				return true;
			}
		}

		return false;
	}

	public void sendListToPlayer(final L2PcInstance player)
	{
		if (isGmOnline(player.isGM()))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.GM_LIST);
			player.sendPacket(sm);
			sm = null;

			for (final String name : getAllGmNames(player.isGM()))
			{
				final SystemMessage sm1 = new SystemMessage(SystemMessageId.GM_S1);
				sm1.addString(name);
				player.sendPacket(sm1);
			}
		}
		else
		{
			SystemMessage sm2 = new SystemMessage(SystemMessageId.NO_GM_PROVIDING_SERVICE_NOW);
			player.sendPacket(sm2);
			sm2 = null;
		}
	}

	public static void broadcastToGMs(final L2GameServerPacket packet)
	{
		for (final L2PcInstance gm : getInstance().getAllGms(true))
		{
			gm.sendPacket(packet);
		}
	}

	public static void broadcastMessageToGMs(final String message)
	{
		for (final L2PcInstance gm : getInstance().getAllGms(true))
		{
			// prevents a NPE.
			if (gm != null)
			{
				gm.sendPacket(SystemMessage.sendString(message));
			}
		}
	}
}
