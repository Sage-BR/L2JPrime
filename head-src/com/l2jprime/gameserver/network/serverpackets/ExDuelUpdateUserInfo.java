/* l2jprime Project - 4teambr.com
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

import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: ch Sddddddddd.
 * @author KenM
 */
public class ExDuelUpdateUserInfo extends L2GameServerPacket
{

	/** The Constant _S__FE_4F_EXDUELUPDATEUSERINFO. */
	private static final String _S__FE_4F_EXDUELUPDATEUSERINFO = "[S] FE:4F ExDuelUpdateUserInfo";

	/** The _active char. */
	private final L2PcInstance _activeChar;

	/**
	 * Instantiates a new ex duel update user info.
	 * @param cha the cha
	 */
	public ExDuelUpdateUserInfo(final L2PcInstance cha)
	{
		_activeChar = cha;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x4f);
		writeS(_activeChar.getName());
		writeD(_activeChar.getObjectId());
		writeD(_activeChar.getClassId().getId());
		writeD(_activeChar.getLevel());
		writeD((int) _activeChar.getCurrentHp());
		writeD(_activeChar.getMaxHp());
		writeD((int) _activeChar.getCurrentMp());
		writeD(_activeChar.getMaxMp());
		writeD((int) _activeChar.getCurrentCp());
		writeD(_activeChar.getMaxCp());
	}

	/**
	 * Gets the type.
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return _S__FE_4F_EXDUELUPDATEUSERINFO;
	}

}
