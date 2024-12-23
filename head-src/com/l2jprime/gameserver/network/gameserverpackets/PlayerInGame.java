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
package com.l2jprime.gameserver.network.gameserverpackets;

import javolution.util.FastList;

/**
 * @author -Wooden-
 */
public class PlayerInGame extends GameServerBasePacket
{
	public PlayerInGame(final String player)
	{
		writeC(0x02);
		writeH(1);
		writeS(player);
	}

	public PlayerInGame(final FastList<String> players)
	{
		writeC(0x02);
		writeH(players.size());
		for (final String pc : players)
		{
			writeS(pc);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.gameserverpackets.GameServerBasePacket#getContent()
	 */
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}

}
