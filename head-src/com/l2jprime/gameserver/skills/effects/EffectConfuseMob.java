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
package com.l2jprime.gameserver.skills.effects;

import java.util.List;

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.model.L2Attackable;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Effect;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.skills.Env;
import com.l2jprime.util.random.Rnd;

import javolution.util.FastList;

/**
 * @author littlecrow Implementation of the Confusion Effect
 */
final class EffectConfuseMob extends L2Effect
{

	public EffectConfuseMob(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSE_MOB_ONLY;
	}

	/** Notify started */
	@Override
	public void onStart()
	{
		getEffected().startConfused();
		onActionTime();
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
		getEffected().stopConfused(this);
	}

	@Override
	public boolean onActionTime()
	{
		final List<L2Character> targetList = new FastList<>();

		// Getting the possible targets

		for (final L2Object obj : getEffected().getKnownList().getKnownObjects().values())
		{
			if (obj == null)
			{
				continue;
			}

			if ((obj instanceof L2Attackable) && (obj != getEffected()))
			{
				targetList.add((L2Character) obj);
			}
		}
		// if there is no target, exit function
		if (targetList.size() == 0)
		{
			return true;
		}

		// Choosing randomly a new target
		final int nextTargetIdx = Rnd.nextInt(targetList.size());
		final L2Object target = targetList.get(nextTargetIdx);

		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);

		return true;
	}
}
