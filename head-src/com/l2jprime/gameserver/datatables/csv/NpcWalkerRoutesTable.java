/*
 * l2jprime Project - www.4teambr.com
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
package com.l2jprime.gameserver.datatables.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.L2NpcWalkerNode;

import javolution.util.FastList;

/**
 * Main Table to Load Npc Walkers Routes and Chat SQL Table.<br>
 * @author Rayan RPG for L2Emu Project
 * @author ProGramMoS
 * @since 927
 */
public class NpcWalkerRoutesTable
{
	protected static final Logger LOGGER = Logger.getLogger(NpcWalkerRoutesTable.class);

	private static NpcWalkerRoutesTable _instance;

	private FastList<L2NpcWalkerNode> _routes;

	public static NpcWalkerRoutesTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new NpcWalkerRoutesTable();
			LOGGER.info("Initializing Walkers Routes Table.");
		}

		return _instance;
	}

	private NpcWalkerRoutesTable()
	{
		// not here
	}

	public void load()
	{
		_routes = new FastList<>();
		// java.sql.Connection con = null;

		FileReader reader = null;
		BufferedReader buff = null;
		LineNumberReader lnr = null;

		try
		{
			final File fileData = new File(Config.DATAPACK_ROOT + "/data/csv/walker_routes.csv");

			reader = new FileReader(fileData);
			buff = new BufferedReader(reader);
			lnr = new LineNumberReader(buff);

			L2NpcWalkerNode route;
			String line = null;

			// format:
			// route_id;npc_id;move_point;chatText;move_x;move_y;move_z;delay;running
			while ((line = lnr.readLine()) != null)
			{
				// ignore comments
				if ((line.trim().length() == 0) || line.startsWith("#"))
				{
					continue;
				}
				route = new L2NpcWalkerNode();
				final StringTokenizer st = new StringTokenizer(line, ";");

				final int route_id = Integer.parseInt(st.nextToken());
				final int npc_id = Integer.parseInt(st.nextToken());
				final String move_point = st.nextToken();
				final String chatText = st.nextToken();
				final int move_x = Integer.parseInt(st.nextToken());
				final int move_y = Integer.parseInt(st.nextToken());
				final int move_z = Integer.parseInt(st.nextToken());
				final int delay = Integer.parseInt(st.nextToken());
				final boolean running = Boolean.parseBoolean(st.nextToken());

				route.setRouteId(route_id);
				route.setNpcId(npc_id);
				route.setMovePoint(move_point);
				route.setChatText(chatText);
				route.setMoveX(move_x);
				route.setMoveY(move_y);
				route.setMoveZ(move_z);
				route.setDelay(delay);
				route.setRunning(running);

				_routes.add(route);
				route = null;
			}

			LOGGER.info("WalkerRoutesTable: Loaded " + _routes.size() + " Npc Walker Routes.");

		}
		catch (final FileNotFoundException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			LOGGER.warn("walker_routes.csv is missing in data folder");
		}
		catch (final IOException e0)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e0.printStackTrace();
			}

			LOGGER.warn("Error while creating table: " + e0.getMessage() + "\n" + e0);
		}
		finally
		{
			if (lnr != null)
			{
				try
				{
					lnr.close();
				}
				catch (final Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if (buff != null)
			{
				try
				{
					buff.close();
				}
				catch (final Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (final Exception e1)
				{
					e1.printStackTrace();
				}
			}

		}

	}

	public FastList<L2NpcWalkerNode> getRouteForNpc(final int id)
	{
		final FastList<L2NpcWalkerNode> _return = new FastList<>();

		for (FastList.Node<L2NpcWalkerNode> n = _routes.head(), end = _routes.tail(); (n = n.getNext()) != end;)
		{
			if (n.getValue().getNpcId() == id)
			{
				_return.add(n.getValue());
			}
		}

		return _return;
	}
}
