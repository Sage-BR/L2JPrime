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
 * Format: (ch)ddd.
 */
public class ExVariationCancelResult extends L2GameServerPacket
{
	/** The Constant _S__FE_57_EXVARIATIONCANCELRESULT. */
	private static final String _S__FE_57_EXVARIATIONCANCELRESULT = "[S] FE:57 ExVariationCancelResult";

	/** The _close window. */
	private final int _closeWindow;

	/** The _unk1. */
	private final int _unk1;

	/**
	 * Instantiates a new ex variation cancel result.
	 * @param result the result
	 */
	public ExVariationCancelResult(final int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x57);
		writeD(_closeWindow);
		writeD(_unk1);
	}

	/**
	 * Gets the type.
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return _S__FE_57_EXVARIATIONCANCELRESULT;
	}
}
