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
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

/**
 * 0000: 01 7a 73 10 4c b2 0b 00 00 a3 fc 00 00 e8 f1 ff .zs.L........... 0010: ff bd 0b 00 00 b3 fc 00 00 e8 f1 ff ff ............. ddddddd
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class CharMoveToLocation extends L2GameServerPacket
{
	private static final String _S__01_CHARMOVETOLOCATION = "[S] 01 CharMoveToLocation";
	private final int _charObjId, _x, _y, _z, _xDst, _yDst, _zDst;

	public CharMoveToLocation(final L2Character cha)
	{
		_charObjId = cha.getObjectId();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_xDst = cha.getXdestination();
		_yDst = cha.getYdestination();
		_zDst = cha.getZdestination();
	}

	@Override
	protected final void writeImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();

		// reset old Moving task
		if ((activeChar != null) && activeChar.isMovingTaskDefined())
		{
			activeChar.setMovingTaskDefined(false);
		}

		writeC(0x01);

		writeD(_charObjId);

		writeD(_xDst);
		writeD(_yDst);
		writeD(_zDst);

		writeD(_x);
		writeD(_y);
		writeD(_z);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__01_CHARMOVETOLOCATION;
	}

}
