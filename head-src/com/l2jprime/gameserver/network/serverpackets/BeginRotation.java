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

import com.l2jprime.gameserver.model.L2Character;

public class BeginRotation extends L2GameServerPacket
{
	private static final String _S__62_BEGINROTATION = "[S] 62 BeginRotation";
	private final int _charObjId;
	private final int _degree;
	private final int _side;
	private final int _speed;

	public BeginRotation(final L2Character player, final int degree, final int side, final int speed)
	{
		_charObjId = player.getObjectId();
		_degree = degree;
		_side = side;
		_speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x62);
		writeD(_charObjId);
		writeD(_degree);
		writeD(_side);
		if (_speed != 0)
		{
			writeD(_speed);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__62_BEGINROTATION;
	}
}
