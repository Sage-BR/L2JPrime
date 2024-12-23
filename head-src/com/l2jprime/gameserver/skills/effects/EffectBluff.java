/*
 * l2jprime Project - 4teambr.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jprime.gameserver.skills.effects;

import com.l2jprime.gameserver.model.L2Effect;
import com.l2jprime.gameserver.model.actor.instance.L2ArtefactInstance;
import com.l2jprime.gameserver.model.actor.instance.L2ControlTowerInstance;
import com.l2jprime.gameserver.model.actor.instance.L2EffectPointInstance;
import com.l2jprime.gameserver.model.actor.instance.L2FolkInstance;
import com.l2jprime.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jprime.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.l2jprime.gameserver.network.serverpackets.BeginRotation;
import com.l2jprime.gameserver.network.serverpackets.StopRotation;
import com.l2jprime.gameserver.network.serverpackets.ValidateLocation;
import com.l2jprime.gameserver.skills.Env;

/**
 * @author programmos, sword developers Implementation of the Bluff Effect
 */
public class EffectBluff extends L2Effect
{

	public EffectBluff(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLUFF;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	/*
	 * @Override public void onExit() { super.onExit(); }
	 */

	/** Notify started */

	@Override
	public void onStart()
	{
		if (getEffected().isDead() || getEffected().isAfraid())
		{
			return;
		}

		if ((getEffected() instanceof L2FolkInstance) || (getEffected() instanceof L2ControlTowerInstance) || (getEffected() instanceof L2ArtefactInstance) || (getEffected() instanceof L2EffectPointInstance) || (getEffected() instanceof L2SiegeFlagInstance) || (getEffected() instanceof L2SiegeSummonInstance))
		{
			return;
		}

		super.onStart();

		// break target
		getEffected().setTarget(null);
		// stop cast
		getEffected().breakCast();
		// stop attacking
		getEffected().breakAttack();
		// stop follow
		getEffected().getAI().stopFollow();
		// stop auto attack
		getEffected().getAI().clientStopAutoAttack();

		getEffected().broadcastPacket(new BeginRotation(getEffected(), getEffected().getHeading(), 1, 65535));
		getEffected().broadcastPacket(new StopRotation(getEffected(), getEffector().getHeading(), 65535));
		getEffected().setHeading(getEffector().getHeading());
		// sometimes rotation didn't showed correctly ??
		getEffected().sendPacket(new ValidateLocation(getEffector()));
		getEffector().sendPacket(new ValidateLocation(getEffected()));
		onActionTime();
	}
}
