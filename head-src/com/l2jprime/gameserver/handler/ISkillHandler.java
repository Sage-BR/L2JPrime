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
package com.l2jprime.gameserver.handler;

import java.io.IOException;

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.L2Skill;
import com.l2jprime.gameserver.model.L2Skill.SkillType;

/**
 * an IItemHandler implementation has to be stateless
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/03 15:55:06 $
 */

public interface ISkillHandler
{
	/**
	 * this is the worker method that is called when using an item.
	 * @param activeChar
	 * @param skill
	 * @param targets
	 * @throws IOException
	 */
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets) throws IOException;

	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	public SkillType[] getSkillIds();
}
