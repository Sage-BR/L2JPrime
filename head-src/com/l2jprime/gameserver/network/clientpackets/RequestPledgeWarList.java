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

import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.serverpackets.PledgeReceiveWarList;

/**
 * Format: (ch) dd
 * @author -Wooden-
 */
public final class RequestPledgeWarList extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unk1;
	private int _tab;

	@Override
	protected void readImpl()
	{
		_unk1 = readD();
		_tab = readD();
	}

	@Override
	protected void runImpl()
	{
		// LOGGER.info("C5: RequestPledgeWarList d:"+_unk1);
		// LOGGER.info("C5: RequestPledgeWarList d:"+_tab);
		final L2PcInstance activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (activeChar.getClan() == null))
		{
			return;
		}

		// do we need powers to do that??
		activeChar.sendPacket(new PledgeReceiveWarList(activeChar.getClan(), _tab));
	}

	@Override
	public String getType()
	{
		return "[C] D0:1E RequestPledgeWarList";
	}

}
