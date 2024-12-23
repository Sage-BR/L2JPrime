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

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.actor.instance.L2PetInstance;
import com.l2jprime.gameserver.model.actor.instance.L2SummonInstance;

import javolution.util.FastList;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartySpelled extends L2GameServerPacket
{
	private static final String _S__EE_PartySpelled = "[S] EE PartySpelled";
	private final List<Effect> _effects;
	private final L2Character _activeChar;

	private class Effect
	{
		protected int _skillId;
		protected int _dat;
		protected int _duration;

		public Effect(final int pSkillId, final int pDat, final int pDuration)
		{
			_skillId = pSkillId;
			_dat = pDat;
			_duration = pDuration;
		}
	}

	public PartySpelled(final L2Character cha)
	{
		_effects = new FastList<>();
		_activeChar = cha;
	}

	@Override
	protected final void writeImpl()
	{
		if (_activeChar == null)
		{
			return;
		}
		writeC(0xee);
		writeD(_activeChar instanceof L2SummonInstance ? 2 : _activeChar instanceof L2PetInstance ? 1 : 0);
		writeD(_activeChar.getObjectId());
		writeD(_effects.size());
		for (final Effect temp : _effects)
		{
			writeD(temp._skillId);
			writeH(temp._dat);
			writeD(temp._duration / 1000);
		}

	}

	public void addPartySpelledEffect(final int skillId, final int dat, final int duration)
	{
		_effects.add(new Effect(skillId, dat, duration));
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__EE_PartySpelled;
	}
}
