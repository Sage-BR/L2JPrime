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

package com.l2jprime.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author zabbix Lets drink to code!
 */
public final class RequestLinkHtml extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestLinkHtml.class);
	private String _link;

	@Override
	protected void readImpl()
	{
		_link = readS();
	}

	@Override
	public void runImpl()
	{
		final L2PcInstance actor = getClient().getActiveChar();
		if (actor == null)
		{
			return;
		}

		if (_link.contains("..") || !_link.contains(".htm"))
		{
			LOGGER.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + _link + "', skipped");
			return;
		}

		if (!actor.validateLink(_link))
		{
			return;
		}

		final NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setFile(_link);

		sendPacket(msg);
	}

	@Override
	public String getType()
	{
		return "[C] 20 RequestLinkHtml";
	}
}
