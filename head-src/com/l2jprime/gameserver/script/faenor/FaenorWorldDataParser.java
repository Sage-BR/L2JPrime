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
package com.l2jprime.gameserver.script.faenor;

import java.util.Map;

import javax.script.ScriptContext;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.l2jprime.Config;
import com.l2jprime.gameserver.script.IntList;
import com.l2jprime.gameserver.script.Parser;
import com.l2jprime.gameserver.script.ParserFactory;
import com.l2jprime.gameserver.script.ScriptEngine;

import javolution.util.FastMap;

/**
 * @author Luis Arias
 */
public class FaenorWorldDataParser extends FaenorParser
{
	static Logger LOGGER = Logger.getLogger(FaenorWorldDataParser.class);
	// Script Types
	private final static String PET_DATA = "PetData";

	@Override
	public void parseScript(final Node eventNode, final ScriptContext context)
	{
		if (Config.DEBUG)
		{
			LOGGER.info("Parsing WorldData");
		}

		for (Node node = eventNode.getFirstChild(); node != null; node = node.getNextSibling())
		{

			if (isNodeName(node, PET_DATA))
			{
				parsePetData(node, context);
			}
		}
	}

	public class PetData
	{
		public int petId;
		public int levelStart;
		public int levelEnd;
		Map<String, String> statValues;

		public PetData()
		{
			statValues = new FastMap<>();
		}
	}

	private void parsePetData(final Node petNode, final ScriptContext context)
	{
		// if (Config.DEBUG) LOGGER.info("Parsing PetData.");

		final PetData petData = new PetData();

		try
		{
			petData.petId = getInt(attribute(petNode, "ID"));
			final int[] levelRange = IntList.parse(attribute(petNode, "Levels"));
			petData.levelStart = levelRange[0];
			petData.levelEnd = levelRange[1];

			for (Node node = petNode.getFirstChild(); node != null; node = node.getNextSibling())
			{
				if (isNodeName(node, "Stat"))
				{
					parseStat(node, petData);
				}
			}
			_bridge.addPetData(context, petData.petId, petData.levelStart, petData.levelEnd, petData.statValues);
		}
		catch (final Exception e)
		{
			petData.petId = -1;
			LOGGER.warn("Error in pet Data parser.");
			e.printStackTrace();
		}
	}

	private void parseStat(final Node stat, final PetData petData)
	{
		// if (Config.DEBUG) LOGGER.info("Parsing Pet Statistic.");

		try
		{
			final String statName = attribute(stat, "Name");

			for (Node node = stat.getFirstChild(); node != null; node = node.getNextSibling())
			{
				if (isNodeName(node, "Formula"))
				{
					final String formula = parseForumla(node);
					petData.statValues.put(statName, formula);
				}
			}
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}

			petData.petId = -1;
			LOGGER.warn("ERROR(parseStat):" + e.getMessage());
		}
	}

	private String parseForumla(final Node formulaNode)
	{
		return formulaNode.getTextContent().trim();
	}

	static class FaenorWorldDataParserFactory extends ParserFactory
	{
		@Override
		public Parser create()
		{
			return new FaenorWorldDataParser();
		}
	}

	static
	{
		ScriptEngine.parserFactories.put(getParserName("WorldData"), new FaenorWorldDataParserFactory());
	}
}
