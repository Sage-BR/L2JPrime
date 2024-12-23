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
package com.l2jprime.gameserver.network.clientpackets;

import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.model.L2World;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PetInstance;
import com.l2jprime.gameserver.model.actor.instance.L2SummonInstance;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;

public final class RequestPetGetItem extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2World world = L2World.getInstance();
		final L2ItemInstance item = (L2ItemInstance) world.findObject(_objectId);

		if ((item == null) || (getClient().getActiveChar() == null))
		{
			return;
		}

		if (getClient().getActiveChar().getPet() instanceof L2SummonInstance)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		final L2PetInstance pet = (L2PetInstance) getClient().getActiveChar().getPet();

		if ((pet == null) || pet.isDead() || pet.isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
	}

	@Override
	public String getType()
	{
		return "[C] 8F RequestPetGetItem";
	}
}
