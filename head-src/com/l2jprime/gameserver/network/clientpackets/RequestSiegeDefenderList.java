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

import com.l2jprime.gameserver.managers.CastleManager;
import com.l2jprime.gameserver.managers.FortManager;
import com.l2jprime.gameserver.model.entity.siege.Castle;
import com.l2jprime.gameserver.model.entity.siege.Fort;
import com.l2jprime.gameserver.network.serverpackets.FortSiegeDefenderList;
import com.l2jprime.gameserver.network.serverpackets.SiegeDefenderList;

/**
 * @author programmos
 */
public final class RequestSiegeDefenderList extends L2GameClientPacket
{
	private int _castleId;

	@Override
	protected void readImpl()
	{
		_castleId = readD();
	}

	@Override
	protected void runImpl()
	{
		if (_castleId < 100)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(_castleId);

			if (castle == null)
			{
				return;
			}

			final SiegeDefenderList sdl = new SiegeDefenderList(castle);
			sendPacket(sdl);
		}
		else
		{
			final Fort fort = FortManager.getInstance().getFortById(_castleId);

			if (fort == null)
			{
				return;
			}

			final FortSiegeDefenderList sdl = new FortSiegeDefenderList(fort);
			sendPacket(sdl);
		}
	}

	@Override
	public String getType()
	{
		return "[C] a3 RequestSiegeDefenderList";
	}
}
