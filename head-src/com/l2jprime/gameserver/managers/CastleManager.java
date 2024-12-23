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
package com.l2jprime.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.model.L2Clan;
import com.l2jprime.gameserver.model.L2ClanMember;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.entity.sevensigns.SevenSigns;
import com.l2jprime.gameserver.model.entity.siege.Castle;
import com.l2jprime.util.CloseUtil;
import com.l2jprime.util.database.DatabaseUtils;
import com.l2jprime.util.database.L2DatabaseFactory;

import javolution.util.FastList;

public class CastleManager
{

	protected static final Logger LOGGER = Logger.getLogger(CastleManager.class);

	public static final CastleManager getInstance()
	{
		return SingletonHolder._instance;
	}

	// =========================================================

	// =========================================================
	// Data Field
	private List<Castle> _castles;

	// =========================================================
	// Constructor
	private static final int _castleCirclets[] =
	{
		0,
		6838,
		6835,
		6839,
		6837,
		6840,
		6834,
		6836,
		8182,
		8183
	};

	public CastleManager()
	{
		load();
	}

	// =========================================================
	// Method - Public

	public final int findNearestCastlesIndex(final L2Object obj)
	{
		int index = getCastleIndex(obj);
		if (index < 0)
		{
			double closestDistance = 99999999;
			double distance;
			Castle castle;
			for (int i = 0; i < getCastles().size(); i++)
			{
				castle = getCastles().get(i);

				if (castle == null)
				{
					continue;
				}

				distance = castle.getDistance(obj);

				if (closestDistance > distance)
				{
					closestDistance = distance;
					index = i;
				}
			}
			castle = null;
		}
		return index;
	}

	// =========================================================
	// Method - Private
	private final void load()
	{
		LOGGER.info("Initializing CastleManager");
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			final PreparedStatement statement = con.prepareStatement("Select id from castle order by id");
			final ResultSet rs = statement.executeQuery();

			while (rs.next())
			{
				getCastles().add(new Castle(rs.getInt("id")));
			}

			rs.close();
			DatabaseUtils.close(statement);

			LOGGER.info("Loaded: " + getCastles().size() + " castles");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}

	// =========================================================
	// Property - Public

	public final Castle getCastleById(final int castleId)
	{
		for (final Castle temp : getCastles())
		{
			if (temp.getCastleId() == castleId)
			{
				return temp;
			}
		}

		return null;
	}

	public final Castle getCastleByOwner(final L2Clan clan)
	{
		if (clan == null)
		{
			return null;
		}

		for (final Castle temp : getCastles())
		{
			if ((temp != null) && (temp.getOwnerId() == clan.getClanId()))
			{
				return temp;
			}
		}

		return null;
	}

	public final Castle getCastle(final String name)
	{
		if (name == null)
		{
			return null;
		}

		for (final Castle temp : getCastles())
		{
			if (temp.getName().equalsIgnoreCase(name.trim()))
			{
				return temp;
			}
		}

		return null;
	}

	public final Castle getCastle(final int x, final int y, final int z)
	{
		for (final Castle temp : getCastles())
		{
			if (temp.checkIfInZone(x, y, z))
			{
				return temp;
			}
		}

		return null;
	}

	public final Castle getCastle(final L2Object activeObject)
	{
		if (activeObject == null)
		{
			return null;
		}

		return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}

	public final int getCastleIndex(final int castleId)
	{
		Castle castle;
		for (int i = 0; i < getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if ((castle != null) && (castle.getCastleId() == castleId))
			{
				castle = null;
				return i;
			}
		}
		castle = null;
		return -1;
	}

	public final int getCastleIndex(final L2Object activeObject)
	{
		return getCastleIndex(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}

	public final int getCastleIndex(final int x, final int y, final int z)
	{
		Castle castle;
		for (int i = 0; i < getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if ((castle != null) && castle.checkIfInZone(x, y, z))
			{
				castle = null;
				return i;
			}
		}
		castle = null;
		return -1;
	}

	public final List<Castle> getCastles()
	{
		if (_castles == null)
		{
			_castles = new FastList<>();
		}
		return _castles;
	}

	public final void validateTaxes(final int sealStrifeOwner)
	{
		int maxTax;

		switch (sealStrifeOwner)
		{
			case SevenSigns.CABAL_DUSK:
				maxTax = 5;
				break;
			case SevenSigns.CABAL_DAWN:
				maxTax = 25;
				break;
			default: // no owner
				maxTax = 15;
				break;
		}

		for (final Castle castle : _castles)
		{
			if (castle.getTaxPercent() > maxTax)
			{
				castle.setTaxPercent(maxTax);
			}
		}
	}

	int _castleId = 1; // from this castle

	public int getCirclet()
	{
		return getCircletByCastleId(_castleId);
	}

	public int getCircletByCastleId(final int castleId)
	{
		if ((castleId > 0) && (castleId < 10))
		{
			return _castleCirclets[castleId];
		}

		return 0;
	}

	// remove this castle's circlets from the clan
	public void removeCirclet(final L2Clan clan, final int castleId)
	{
		for (final L2ClanMember member : clan.getMembers())
		{
			removeCirclet(member, castleId);
		}
	}

	// TODO:
	// added: remove clan cirlet for clan leaders
	public void removeCirclet(final L2ClanMember member, final int castleId)
	{
		if (member == null)
		{
			return;
		}

		L2PcInstance player = member.getPlayerInstance();
		final int circletId = getCircletByCastleId(castleId);

		if (circletId != 0)
		{
			// online-player circlet removal
			if (player != null)
			{
				try
				{
					if (player.isClanLeader())
					{
						L2ItemInstance crown = player.getInventory().getItemByItemId(6841);

						if (crown != null)
						{
							if (crown.isEquipped())
							{
								player.getInventory().unEquipItemInSlotAndRecord(crown.getEquipSlot());
							}
							player.destroyItemByItemId("CastleCrownRemoval", 6841, 1, player, true);
							crown = null;
						}
					}

					L2ItemInstance circlet = player.getInventory().getItemByItemId(circletId);
					if (circlet != null)
					{
						if (circlet.isEquipped())
						{
							player.getInventory().unEquipItemInSlotAndRecord(circlet.getEquipSlot());
						}
						player.destroyItemByItemId("CastleCircletRemoval", circletId, 1, player, true);
						circlet = null;
					}
					return;
				}
				catch (final NullPointerException e)
				{
					// continue removing offline
					if (Config.ENABLE_ALL_EXCEPTIONS)
					{
						e.printStackTrace();
					}
				}
			}
			// else offline-player circlet removal
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(false);
				statement = con.prepareStatement("DELETE FROM items WHERE owner_id = ? and item_id = ?");
				statement.setInt(1, member.getObjectId());
				statement.setInt(2, 6841);
				statement.execute();
				DatabaseUtils.close(statement);
				statement = null;

				statement = con.prepareStatement("DELETE FROM items WHERE owner_id = ? and item_id = ?");
				statement.setInt(1, member.getObjectId());
				statement.setInt(2, circletId);
				statement.execute();
				DatabaseUtils.close(statement);
				statement = null;
			}
			catch (final Exception e)
			{
				LOGGER.info("Failed to remove castle circlets offline for player " + member.getName());
				e.printStackTrace();
			}
			finally
			{
				CloseUtil.close(con);
				con = null;
			}
		}
		player = null;
	}

	private static class SingletonHolder
	{
		protected static final CastleManager _instance = new CastleManager();
	}
}
