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
package com.l2jprime.gameserver.model.zone.form;

import com.l2jprime.gameserver.model.zone.L2ZoneForm;

/**
 * A primitive rectangular zone
 * @author durgus
 */
public class ZoneCuboid extends L2ZoneForm
{
	private int _x1, _x2, _y1, _y2, _z1, _z2;

	public ZoneCuboid(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2)
	{
		_x1 = x1;
		_x2 = x2;
		if (_x1 > _x2) // switch them if alignment is wrong
		{
			_x1 = x2;
			_x2 = x1;
		}

		_y1 = y1;
		_y2 = y2;
		if (_y1 > _y2) // switch them if alignment is wrong
		{
			_y1 = y2;
			_y2 = y1;
		}

		_z1 = z1;
		_z2 = z2;
		if (_z1 > _z2) // switch them if alignment is wrong
		{
			_z1 = z2;
			_z2 = z1;
		}
	}

	@Override
	public boolean isInsideZone(final int x, final int y, final int z)
	{
		if ((x < _x1) || (x > _x2) || (y < _y1) || (y > _y2) || (z < _z1) || (z > _z2))
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean intersectsRectangle(final int ax1, final int ax2, final int ay1, final int ay2)
	{
		// Check if any point inside this rectangle
		if (isInsideZone(ax1, ay1, (_z2 - 1)) || isInsideZone(ax1, ay2, (_z2 - 1)) || isInsideZone(ax2, ay1, (_z2 - 1)) || isInsideZone(ax2, ay2, (_z2 - 1)))
		{
			return true;
		}

		// Check if any point from this rectangle is inside the other one
		if ((_x1 > ax1) && (_x1 < ax2) && (_y1 > ay1) && (_y1 < ay2))
		{
			return true;
		}

		if ((_x1 > ax1) && (_x1 < ax2) && (_y2 > ay1) && (_y2 < ay2))
		{
			return true;
		}

		if ((_x2 > ax1) && (_x2 < ax2) && (_y1 > ay1) && (_y1 < ay2))
		{
			return true;
		}

		if ((_x2 > ax1) && (_x2 < ax2) && (_y2 > ay1) && (_y2 < ay2))
		{
			return true;
		}

		// Horizontal lines may intersect vertical lines
		if (lineIntersectsLine(_x1, _y1, _x2, _y1, ax1, ay1, ax1, ay2))
		{
			return true;
		}

		if (lineIntersectsLine(_x1, _y1, _x2, _y1, ax2, ay1, ax2, ay2))
		{
			return true;
		}

		if (lineIntersectsLine(_x1, _y2, _x2, _y2, ax1, ay1, ax1, ay2))
		{
			return true;
		}

		if (lineIntersectsLine(_x1, _y2, _x2, _y2, ax2, ay1, ax2, ay2))
		{
			return true;
		}

		// Vertical lines may intersect horizontal lines
		if (lineIntersectsLine(_x1, _y1, _x1, _y2, ax1, ay1, ax2, ay1))
		{
			return true;
		}

		if (lineIntersectsLine(_x1, _y1, _x1, _y2, ax1, ay2, ax2, ay2))
		{
			return true;
		}

		if (lineIntersectsLine(_x2, _y1, _x2, _y2, ax1, ay1, ax2, ay1))
		{
			return true;
		}

		if (lineIntersectsLine(_x2, _y1, _x2, _y2, ax1, ay2, ax2, ay2))
		{
			return true;
		}

		return false;
	}

	@Override
	public double getDistanceToZone(final int x, final int y)
	{
		// Since we aren't given a z coordinate to test against
		// we just use the minimum z coordinate to prevent the
		// function from saying we aren't in the zone because
		// of a bad z coordinate.
		if (isInsideZone(x, y, _z1))
		{
			return 0; // If you are inside the zone distance to zone is 0.
		}
		
		double test, shortestDist = Math.pow(_x1 - x, 2) + Math.pow(_y1 - y, 2);

		test = Math.pow(_x1 - x, 2) + Math.pow(_y2 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
		}

		test = Math.pow(_x2 - x, 2) + Math.pow(_y1 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
		}

		test = Math.pow(_x2 - x, 2) + Math.pow(_y2 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
		}

		return Math.sqrt(shortestDist);
	}

	/*
	 * getLowZ() / getHighZ() - These two functions were added to cope with the demand of the new fishing algorithms, wich are now able to correctly place the hook in the water, thanks to getHighZ(). getLowZ() was added, considering potential future modifications.
	 */
	@Override
	public int getLowZ()
	{
		return _z1;
	}

	@Override
	public int getHighZ()
	{
		return _z2;
	}
}
