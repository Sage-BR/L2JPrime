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

import com.l2jprime.gameserver.model.L2RecipeList;

/**
 * format d d(dd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeBookItemList extends L2GameServerPacket
{
	private static final String _S__D6_RECIPEBOOKITEMLIST = "[S] D6 RecipeBookItemList";
	private L2RecipeList[] _recipes;
	private final boolean _isDwarvenCraft;
	private final int _maxMp;

	public RecipeBookItemList(final boolean isDwarvenCraft, final int maxMp)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_maxMp = maxMp;
	}

	public void addRecipes(final L2RecipeList[] recipeBook)
	{
		_recipes = recipeBook;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xD6);

		writeD(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
		writeD(_maxMp);

		if (_recipes == null)
		{
			writeD(0);
		}
		else
		{
			writeD(_recipes.length);// number of items in recipe book

			for (int i = 0; i < _recipes.length; i++)
			{
				final L2RecipeList temp = _recipes[i];
				writeD(temp.getId());
				writeD(i + 1);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__D6_RECIPEBOOKITEMLIST;
	}
}
