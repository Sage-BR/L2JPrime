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
package com.l2jprime.gameserver.skills.conditions;

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionTargetAggro extends Condition
{

	private final boolean _isAggro;

	public ConditionTargetAggro(final boolean isAggro)
	{
		_isAggro = isAggro;
	}

	@Override
	public boolean testImpl(final Env env)
	{
		final L2Character target = env.target;
		if (target instanceof L2MonsterInstance)
		{
			return ((L2MonsterInstance) target).isAggressive() == _isAggro;
		}
		if (target instanceof L2PcInstance)
		{
			return ((L2PcInstance) target).getKarma() > 0;
		}
		return false;
	}
}
