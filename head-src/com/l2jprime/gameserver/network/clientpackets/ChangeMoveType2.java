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

import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

public final class ChangeMoveType2 extends L2GameClientPacket
{
	private boolean _typeRun;

	@Override
	protected void readImpl()
	{
		_typeRun = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (_typeRun)
		{
			player.setRunning();
		}
		else
		{
			player.setWalking();
		}
	}

	@Override
	public String getType()
	{
		return "[C] 1C ChangeMoveType2";
	}
}