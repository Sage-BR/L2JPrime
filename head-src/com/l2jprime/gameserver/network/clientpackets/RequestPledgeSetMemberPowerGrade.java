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

import com.l2jprime.gameserver.model.L2Clan;
import com.l2jprime.gameserver.model.L2ClanMember;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch) Sd
 * @author -Wooden-
 */
public final class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	private int _powerGrade;
	private String _member;

	@Override
	protected void readImpl()
	{
		_member = readS();
		_powerGrade = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		final L2Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}

		final L2ClanMember member = clan.getClanMember(_member);
		if (member == null)
		{
			return;
		}

		if (member.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
		{
			// also checked from client side
			activeChar.sendMessage("You cannot change academy member grade");
			return;
		}

		member.setPowerGrade(_powerGrade);
		clan.broadcastClanStatus();
	}

	@Override
	public String getType()
	{
		return "[C] D0:1C RequestPledgeSetMemberPowerGrade";
	}

}
