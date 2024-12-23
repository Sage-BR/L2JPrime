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
package com.l2jprime.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

/**
 * Format: (ch) just a trigger
 * @author -Wooden-
 */
public final class RequestExFishRanking extends L2GameClientPacket
{
	private final Logger LOGGER = Logger.getLogger(RequestExFishRanking.class);

	@Override
	protected void readImpl()
	{
		// trigger
	}

	@Override
	protected void runImpl()
	{
		LOGGER.info("C5: RequestExFishRanking");
	}

	@Override
	public String getType()
	{
		return "[C] D0:1F RequestExFishRanking";
	}

}
