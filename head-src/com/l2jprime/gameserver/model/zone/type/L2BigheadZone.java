/* l2jprime Project - 4teambr.com
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
package com.l2jprime.gameserver.model.zone.type;

import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.zone.L2ZoneType;

/**
 * Bighead zones give entering players big heads
 * @author durgus
 */
public class L2BigheadZone extends L2ZoneType
{
	public L2BigheadZone(final int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.startAbnormalEffect(0x2000);
		}
	}

	@Override
	protected void onExit(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.stopAbnormalEffect((short) 0x2000);
		}
	}

	@Override
	protected void onDieInside(final L2Character character)
	{
		onExit(character);
	}

	@Override
	protected void onReviveInside(final L2Character character)
	{
		onEnter(character);
	}

}
