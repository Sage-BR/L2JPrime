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
package com.l2jprime.gameserver.skills;

import java.util.ArrayList;
import java.util.Arrays;

import com.l2jprime.gameserver.skills.funcs.Func;

import javolution.util.FastList;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...). In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR>
 * <BR>
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
 * <BR>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>. Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order. The result of the calculation is stored in the
 * value property of an Env class instance.<BR>
 * <BR>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR>
 * <BR>
 */

public final class Calculator
{
	/** Empty Func table definition */
	private static final Func[] _emptyFuncs = new Func[0];

	/** Table of Func object */
	private Func[] _functions;

	/**
	 * Constructor of Calculator (Init value : emptyFuncs).<BR>
	 * <BR>
	 */
	public Calculator()
	{
		_functions = _emptyFuncs;
	}

	/**
	 * Constructor of Calculator (Init value : Calculator c).<BR>
	 * <BR>
	 * @param c
	 */
	public Calculator(final Calculator c)
	{
		_functions = c._functions;
	}

	/**
	 * Check if 2 calculators are equals.<BR>
	 * <BR>
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean equalsCals(final Calculator c1, final Calculator c2)
	{
		if (c1 == c2)
		{
			return true;
		}

		if ((c1 == null) || (c2 == null))
		{
			return false;
		}

		final Func[] funcs1 = c1._functions;
		final Func[] funcs2 = c2._functions;

		if (funcs1 == funcs2)
		{
			return true;
		}

		if (funcs1.length != funcs2.length)
		{
			return false;
		}

		if (funcs1.length == 0)
		{
			return true;
		}

		for (int i = 0; i < funcs1.length; i++)
		{
			if (funcs1[i] != funcs2[i])
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the number of Funcs in the Calculator.
	 */
	public int size()
	{
		return _functions.length;
	}

	/**
	 * Add a Func to the Calculator.
	 * @param f
	 */
	public synchronized void addFunc(final Func f)
	{
		final Func[] funcs = _functions;
		final Func[] tmp = new Func[funcs.length + 1];

		final int order = f.order;
		int i;

		for (i = 0; (i < funcs.length) && (order >= funcs[i].order); i++)
		{
			tmp[i] = funcs[i];
		}

		tmp[i] = f;
		for (; i < funcs.length; i++)
		{
			tmp[i + 1] = funcs[i];
		}

		_functions = tmp;
	}

	/**
	 * Remove a Func from the Calculator.
	 * @param f
	 */
	public synchronized void removeFunc(final Func f)
	{
		if (f == null)
		{
			return;
		}

		final ArrayList<Func> tmp_arraylist = new ArrayList<>();
		tmp_arraylist.addAll(Arrays.asList(_functions));

		if (tmp_arraylist.contains(f))
		{
			tmp_arraylist.remove(f);
		}

		_functions = tmp_arraylist.toArray(new Func[tmp_arraylist.size()]);

	}

	/**
	 * Remove each Func with the specified owner of the Calculator.
	 * @param owner
	 * @return
	 */
	public synchronized FastList<Stats> removeOwner(final Object owner)
	{
		final Func[] funcs = _functions;
		final FastList<Stats> modifiedStats = new FastList<>();

		for (final Func func : funcs)
		{
			if (func.funcOwner == owner)
			{
				modifiedStats.add(func.stat);
				removeFunc(func);
			}
		}
		return modifiedStats;
	}

	/**
	 * Run each Func of the Calculator.
	 * @param env
	 */
	public void calc(final Env env)
	{
		final Func[] funcs = _functions;

		for (final Func func : funcs)
		{
			func.calc(env);
		}
	}
}
