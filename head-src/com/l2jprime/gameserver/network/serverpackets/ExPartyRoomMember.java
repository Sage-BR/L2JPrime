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
package com.l2jprime.gameserver.network.serverpackets;

import com.l2jprime.gameserver.managers.TownManager;
import com.l2jprime.gameserver.model.PartyMatchRoom;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gnacik
 */
public class ExPartyRoomMember extends L2GameServerPacket
{
	private final PartyMatchRoom _room;
	private final int _mode;

	public ExPartyRoomMember(final L2PcInstance player, final PartyMatchRoom room, final int mode)
	{
		_room = room;
		_mode = mode;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x0e);
		writeD(_mode);
		writeD(_room.getMembers());
		for (final L2PcInstance _member : _room.getPartyMembers())
		{
			writeD(_member.getObjectId());
			writeS(_member.getName());
			writeD(_member.getActiveClass());
			writeD(_member.getLevel());
			writeD(TownManager.getClosestLocation(_member));
			if (_room.getOwner().equals(_member))
			{
				writeD(1);
			}
			else
			{
				if ((_room.getOwner().isInParty() && _member.isInParty()) && (_room.getOwner().getParty().getPartyLeaderOID() == _member.getParty().getPartyLeaderOID()))
				{
					writeD(2);
				}
				else
				{
					writeD(0);
				}
			}
		}
	}

	@Override
	public String getType()
	{
		return "[S] FE:0E ExPartyRoomMember";
	}
}