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

import java.util.List;

public class ShowBoard extends L2GameServerPacket
{
	private static final String _S__6E_SHOWBOARD = "[S] 6e ShowBoard";

	private final String _htmlCode;
	private final String _id;
	private List<String> _arg;

	public ShowBoard(final String htmlCode, final String id)
	{
		_id = id;
		_htmlCode = htmlCode; // html code must not exceed 8192 bytes
	}

	public ShowBoard(final List<String> arg)
	{
		_id = "1002";
		_htmlCode = null;
		_arg = arg;

	}

	private byte[] get1002()
	{
		int len = (_id.getBytes().length * 2) + 2;
		for (final String arg : _arg)
		{
			len += (arg.getBytes().length + 4) * 2;
		}
		final byte data[] = new byte[len];
		int i = 0;
		for (int j = 0; j < _id.getBytes().length; j++, i += 2)
		{
			data[i] = _id.getBytes()[j];
			data[i + 1] = 0;
		}
		data[i] = 8;
		i++;
		data[i] = 0;
		i++;
		for (final String arg : _arg)
		{
			for (int j = 0; j < arg.getBytes().length; j++, i += 2)
			{
				data[i] = arg.getBytes()[j];
				data[i + 1] = 0;
			}
			data[i] = 0x20;
			i++;
			data[i] = 0x0;
			i++;
			data[i] = 0x8;
			i++;
			data[i] = 0x0;
			i++;
		}
		return data;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6e);
		writeC(0x01); // c4 1 to show community 00 to hide
		writeS("bypass _bbshome"); // top
		writeS("bypass _bbsgetfav"); // favorite
		writeS("bypass _bbsloc"); // region
		writeS("bypass _bbsclan"); // clan
		writeS("bypass _bbsmemo"); // memo
		writeS("bypass _bbsmail"); // mail
		writeS("bypass _bbsfriends"); // friends
		writeS("bypass bbs_add_fav"); // add fav.
		if (!_id.equals("1002"))
		{
			// getBytes is a very costy operation, and should only be called once
			byte htmlBytes[] = null;
			if (_htmlCode != null)
			{
				htmlBytes = _htmlCode.getBytes();
			}
			final byte data[] = new byte[2 + 2 + 2 + (_id.getBytes().length * 2) + (2 * (htmlBytes != null ? htmlBytes.length : 0))];
			int i = 0;
			for (int j = 0; j < _id.getBytes().length; j++, i += 2)
			{
				data[i] = _id.getBytes()[j];
				data[i + 1] = 0;
			}
			data[i] = 8;
			i++;
			data[i] = 0;
			i++;
			if (htmlBytes != null)
			{
				for (int j = 0; j < htmlBytes.length; i += 2, j++)
				{
					data[i] = htmlBytes[j];
					data[i + 1] = 0;
				}
			}
			data[i] = 0;
			i++;
			data[i] = 0;
			// writeS(_htmlCode); // current page
			writeB(data);
		}
		else
		{
			writeB(get1002());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__6E_SHOWBOARD;
	}
}
