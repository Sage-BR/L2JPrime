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

import com.l2jprime.gameserver.model.L2Effect;
import com.l2jprime.gameserver.model.L2Summon;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.skills.Env;

/**
 * @author demonia
 */
final class EffectImobilePetBuff extends L2Effect
{
	private L2Summon _pet;

	public EffectImobilePetBuff(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	/** Notify started */
	@Override
	public void onStart()
	{
		_pet = null;

		if ((getEffected() instanceof L2Summon) && (getEffector() instanceof L2PcInstance) && (((L2Summon) getEffected()).getOwner() == getEffector()))
		{
			_pet = (L2Summon) getEffected();
			_pet.setIsImobilised(true);
		}
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
		if (_pet != null)
		{
			_pet.setIsImobilised(false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
