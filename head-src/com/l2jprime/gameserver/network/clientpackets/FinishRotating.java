/*
 * l2jprime Project - 4teambr.com
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
package com.l2jprime.gameserver.network.clientpackets;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.StopRotation;

@SuppressWarnings("unused")
public final class FinishRotating extends L2GameClientPacket
{
	private int _degree, _unknown;

	@Override
	protected void readImpl()
	{
		_degree = readD();
		_unknown = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (!Config.ALLOW_USE_CURSOR_FOR_WALK)
		{
			getClient().getActiveChar().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		player.broadcastPacket(new StopRotation(player, _degree, 0));
	}

	@Override
	public String getType()
	{
		return "[C] 4B FinishRotating";
	}
}