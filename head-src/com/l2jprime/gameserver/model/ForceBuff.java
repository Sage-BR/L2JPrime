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

package com.l2jprime.gameserver.model;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.datatables.SkillTable;
import com.l2jprime.gameserver.skills.effects.EffectForce;

/**
 * @author ProGramMoS, l2jprime
 */
public final class ForceBuff
{
	protected int _forceId;
	protected int _forceLevel;
	protected L2Character _caster;
	protected L2Character _target;

	static final Logger LOGGER = Logger.getLogger(ForceBuff.class);

	public L2Character getCaster()
	{
		return _caster;
	}

	public L2Character getTarget()
	{
		return _target;
	}

	public ForceBuff(final L2Character caster, final L2Character target, final L2Skill skill)
	{
		_caster = caster;
		_target = target;
		_forceId = skill.getTriggeredId();
		_forceLevel = skill.getTriggeredLevel();

		L2Effect effect = _target.getFirstEffect(_forceId);
		if (effect != null)
		{
			((EffectForce) effect).increaseForce();
		}
		else
		{
			final L2Skill force = SkillTable.getInstance().getInfo(_forceId, _forceLevel);
			if (force != null)
			{
				force.getEffects(_caster, _target, false, false, false);
			}
			else
			{
				LOGGER.warn("Triggered skill [" + _forceId + ";" + _forceLevel + "] not found!");
			}
		}
		effect = null;
	}

	public void onCastAbort()
	{
		_caster.setForceBuff(null);
		L2Effect effect = _target.getFirstEffect(_forceId);
		if (effect != null)
		{
			if (Config.DEVELOPER)
			{
				LOGGER.info(" -- Removing ForceBuff " + effect.getSkill().getId());
			}

			if (effect instanceof EffectForce)
			{
				((EffectForce) effect).decreaseForce();
			}
			else
			{
				effect.exit(false);
			}
		}
		effect = null;
	}
}
