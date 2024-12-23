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
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.UserInfo;

/**
 * Appearing Packet Handler
 */
public final class Appearing extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();

		if ((activeChar == null) || (activeChar.isOnline() == 0))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (activeChar.isTeleporting())
		{
			activeChar.onTeleported();
		}

		sendPacket(new UserInfo(activeChar));
	}

	@Override
	public String getType()
	{
		return "[C] 30 Appearing";
	}
}