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

/**
 * Format: ch dddd.
 * @author KenM
 */
public class ExUseSharedGroupItem extends L2GameServerPacket
{
	/** The Constant _S__FE_49_EXUSESHAREDGROUPITEM. */
	private static final String _S__FE_49_EXUSESHAREDGROUPITEM = "[S] FE:49 ExUseSharedGroupItem";

	/** The _unk4. */
	private final int _unk1, _unk2, _unk3, _unk4;

	/**
	 * Instantiates a new ex use shared group item.
	 * @param unk1 the unk1
	 * @param unk2 the unk2
	 * @param unk3 the unk3
	 * @param unk4 the unk4
	 */
	public ExUseSharedGroupItem(final int unk1, final int unk2, final int unk3, final int unk4)
	{
		_unk1 = unk1;
		_unk2 = unk2;
		_unk3 = unk3;
		_unk4 = unk4;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x49);

		writeD(_unk1);
		writeD(_unk2);
		writeD(_unk3);
		writeD(_unk4);
	}

	/**
	 * Gets the type.
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return _S__FE_49_EXUSESHAREDGROUPITEM;
	}
}
