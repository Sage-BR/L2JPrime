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
package com.l2jprime.gameserver.script;

/**
 * @author -Nemesiss-
 */
public class ShortList
{
	public static short[] parse(final String range)
	{
		if (range.contains("-"))
		{
			return getShortList(range.split("-"));
		}
		else if (range.contains(","))
		{
			return getShortList(range.split(","));
		}

		final short[] list =
		{
			getShort(range)
		};
		return list;
	}

	private static short getShort(final String number)
	{
		return Short.parseShort(number);
	}

	private static short[] getShortList(final String[] numbers)
	{
		final short[] list = new short[numbers.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = getShort(numbers[i]);
		}
		return list;
	}
}
