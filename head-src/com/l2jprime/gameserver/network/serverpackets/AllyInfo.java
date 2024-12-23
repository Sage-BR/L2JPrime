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
package com.l2jprime.gameserver.network.serverpackets;

import com.l2jprime.gameserver.datatables.sql.ClanTable;
import com.l2jprime.gameserver.model.L2Clan;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;

public class AllyInfo extends L2GameServerPacket
{
	private static final String _S__7A_FRIENDLIST = "[S] 7a AllyInfo";
	private final L2PcInstance _cha;

	public AllyInfo(final L2PcInstance cha)
	{
		_cha = cha;
	}

	@Override
	protected final void writeImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.getAllyId() == 0)
		{
			_cha.sendPacket(new SystemMessage(SystemMessageId.NO_CURRENT_ALLIANCES));
			return;
		}

		// ======<AllyInfo>======
		SystemMessage sm = new SystemMessage(SystemMessageId.ALLIANCE_INFO_HEAD);
		_cha.sendPacket(sm);
		// ======<Ally Name>======
		sm = new SystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
		sm.addString(_cha.getClan().getAllyName());
		_cha.sendPacket(sm);
		int online = 0;
		int count = 0;
		int clancount = 0;
		for (final L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == _cha.getAllyId())
			{
				clancount++;
				online += clan.getOnlineMembers("").length;
				count += clan.getMembers().length;
			}
		}
		// Connection
		sm = new SystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
		sm.addString("" + online);
		sm.addString("" + count);
		_cha.sendPacket(sm);
		final L2Clan leaderclan = ClanTable.getInstance().getClan(_cha.getAllyId());
		sm = new SystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
		sm.addString(leaderclan.getName());
		sm.addString(leaderclan.getLeaderName());
		_cha.sendPacket(sm);
		// clan count
		sm = new SystemMessage(SystemMessageId.ALLIANCE_CLAN_TOTAL_S1);
		sm.addString("" + clancount);
		_cha.sendPacket(sm);
		// clan information
		sm = new SystemMessage(SystemMessageId.CLAN_INFO_HEAD);
		_cha.sendPacket(sm);
		for (final L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == _cha.getAllyId())
			{
				// clan name
				sm = new SystemMessage(SystemMessageId.CLAN_INFO_NAME);
				sm.addString(clan.getName());
				_cha.sendPacket(sm);
				// clan leader name
				sm = new SystemMessage(SystemMessageId.CLAN_INFO_LEADER);
				sm.addString(clan.getLeaderName());
				_cha.sendPacket(sm);
				// clan level
				sm = new SystemMessage(SystemMessageId.CLAN_INFO_LEVEL);
				sm.addNumber(clan.getLevel());
				_cha.sendPacket(sm);
				// ---------
				sm = new SystemMessage(SystemMessageId.CLAN_INFO_SEPARATOR);
				_cha.sendPacket(sm);
			}
		}
		// =========================
		sm = new SystemMessage(SystemMessageId.CLAN_INFO_FOOT);
		_cha.sendPacket(sm);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__7A_FRIENDLIST;
	}
}
