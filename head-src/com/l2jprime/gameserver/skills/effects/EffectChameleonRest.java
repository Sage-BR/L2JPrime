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

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Effect;
import com.l2jprime.gameserver.model.L2Skill.SkillType;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;
import com.l2jprime.gameserver.skills.Env;

public final class EffectChameleonRest extends L2Effect
{
	public EffectChameleonRest(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.RELAXING;
	}

	/** Notify started */
	@Override
	public void onStart()
	{
		final L2Character effected = getEffected();
		if (effected instanceof L2PcInstance)
		{
			setChameleon(true);
			((L2PcInstance) effected).setSilentMoving(true);
			((L2PcInstance) effected).sitDown();
		}
		else
		{
			effected.getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		setChameleon(false);

		final L2Character effected = getEffected();
		if (effected instanceof L2PcInstance)
		{
			((L2PcInstance) effected).setSilentMoving(false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		final L2Character effected = getEffected();
		boolean retval = true;

		if (effected.isDead())
		{
			retval = false;
		}

		// Only cont skills shouldn't end
		if (getSkill().getSkillType() != SkillType.CONT)
		{
			return false;
		}

		if (effected instanceof L2PcInstance)
		{
			if (!((L2PcInstance) effected).isSitting())
			{
				retval = false;
			}
		}

		final double manaDam = calc();

		if (manaDam > effected.getStatus().getCurrentMp())
		{
			effected.sendPacket(new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}

		if (!retval)
		{
			setChameleon(retval);
		}
		else
		{
			effected.reduceCurrentMp(manaDam);
		}

		return retval;
	}

	private void setChameleon(final boolean val)
	{
		final L2Character effected = getEffected();
		if (effected instanceof L2PcInstance)
		{
			((L2PcInstance) effected).setRelax(val);
		}
	}
}
