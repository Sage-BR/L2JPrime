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
package com.l2jprime.gameserver.templates;

import java.io.IOException;
import java.util.List;

import com.l2jprime.Config;
import com.l2jprime.gameserver.datatables.SkillTable;
import com.l2jprime.gameserver.handler.ISkillHandler;
import com.l2jprime.gameserver.handler.SkillHandler;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Effect;
import com.l2jprime.gameserver.model.L2Skill;
import com.l2jprime.gameserver.model.L2Skill.SkillType;
import com.l2jprime.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jprime.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jprime.gameserver.model.actor.instance.L2PcInstance;
import com.l2jprime.gameserver.model.quest.Quest;
import com.l2jprime.gameserver.skills.Env;
import com.l2jprime.gameserver.skills.conditions.ConditionGameChance;
import com.l2jprime.gameserver.skills.funcs.Func;
import com.l2jprime.gameserver.skills.funcs.FuncTemplate;

import javolution.util.FastList;

/**
 * This class is dedicated to the management of weapons.
 * @version $Revision: 1.4.2.3.2.5 $ $Date: 2005/04/02 15:57:51 $
 */
public final class L2Weapon extends L2Item
{
	private final int _soulShotCount;
	private final int _spiritShotCount;
	private final int _pDam;
	private final int _rndDam;
	private final int _critical;
	private final double _hitModifier;
	private final int _avoidModifier;
	private final int _shieldDef;
	private final double _shieldDefRate;
	private final int _atkSpeed;
	private final int _atkReuse;
	private final int _mpConsume;
	private final int _mDam;
	private L2Skill _itemSkill = null; // for passive skill
	private L2Skill _enchant4Skill = null; // skill that activates when item is enchanted +4 (for duals)

	// Attached skills for Special Abilities
	protected L2Skill[] _skillsOnCast;
	protected L2Skill[] _skillsOnCrit;

	/**
	 * Constructor for Weapon.<BR>
	 * <BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>_soulShotCount & _spiritShotCount</LI>
	 * <LI>_pDam & _mDam & _rndDam</LI>
	 * <LI>_critical</LI>
	 * <LI>_hitModifier</LI>
	 * <LI>_avoidModifier</LI>
	 * <LI>_shieldDes & _shieldDefRate</LI>
	 * <LI>_atkSpeed & _AtkReuse</LI>
	 * <LI>_mpConsume</LI>
	 * @param type : L2ArmorType designating the type of armor
	 * @param set : StatsSet designating the set of couples (key,value) caracterizing the armor
	 * @see L2Item constructor
	 */
	public L2Weapon(final L2WeaponType type, final StatsSet set)
	{
		super(type, set);
		_soulShotCount = set.getInteger("soulshots");
		_spiritShotCount = set.getInteger("spiritshots");
		_pDam = set.getInteger("p_dam");
		_rndDam = set.getInteger("rnd_dam");
		_critical = set.getInteger("critical");
		_hitModifier = set.getDouble("hit_modify");
		_avoidModifier = set.getInteger("avoid_modify");
		_shieldDef = set.getInteger("shield_def");
		_shieldDefRate = set.getDouble("shield_def_rate");
		_atkSpeed = set.getInteger("atk_speed");
		_atkReuse = set.getInteger("atk_reuse", type == L2WeaponType.BOW ? 1500 : 0);
		_mpConsume = set.getInteger("mp_consume");
		_mDam = set.getInteger("m_dam");

		int sId = set.getInteger("item_skill_id");
		int sLv = set.getInteger("item_skill_lvl");
		if ((sId > 0) && (sLv > 0))
		{
			_itemSkill = SkillTable.getInstance().getInfo(sId, sLv);
		}

		sId = set.getInteger("enchant4_skill_id");
		sLv = set.getInteger("enchant4_skill_lvl");
		if ((sId > 0) && (sLv > 0))
		{
			_enchant4Skill = SkillTable.getInstance().getInfo(sId, sLv);
		}

		sId = set.getInteger("onCast_skill_id");
		sLv = set.getInteger("onCast_skill_lvl");
		int sCh = set.getInteger("onCast_skill_chance");
		if ((sId > 0) && (sLv > 0) && (sCh > 0))
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(sId, sLv);
			skill.attach(new ConditionGameChance(sCh), true);
			attachOnCast(skill);
		}

		sId = set.getInteger("onCrit_skill_id");
		sLv = set.getInteger("onCrit_skill_lvl");
		sCh = set.getInteger("onCrit_skill_chance");
		if ((sId > 0) && (sLv > 0) && (sCh > 0))
		{
			final L2Skill skill = SkillTable.getInstance().getInfo(sId, sLv);
			skill.attach(new ConditionGameChance(sCh), true);
			attachOnCrit(skill);
		}
	}

	/**
	 * Returns the type of Weapon
	 * @return L2WeaponType
	 */
	@Override
	public L2WeaponType getItemType()
	{
		return (L2WeaponType) super._type;
	}

	/**
	 * Returns the ID of the Etc item after applying the mask.
	 * @return int : ID of the Weapon
	 */
	@Override
	public int getItemMask()
	{
		return getItemType().mask();
	}

	/**
	 * Returns the quantity of SoulShot used.
	 * @return int
	 */
	public int getSoulShotCount()
	{
		return _soulShotCount;
	}

	/**
	 * Returns the quatity of SpiritShot used.
	 * @return int
	 */
	public int getSpiritShotCount()
	{
		return _spiritShotCount;
	}

	/**
	 * Returns the physical damage.
	 * @return int
	 */
	public int getPDamage()
	{
		return _pDam;
	}

	/**
	 * Returns the random damage inflicted by the weapon
	 * @return int
	 */
	public int getRandomDamage()
	{
		return _rndDam;
	}

	/**
	 * Returns the attack speed of the weapon
	 * @return int
	 */
	public int getAttackSpeed()
	{
		return _atkSpeed;
	}

	/**
	 * Return the Attack Reuse Delay of the L2Weapon.<BR>
	 * <BR>
	 * @return int
	 */
	public int getAttackReuseDelay()
	{
		return _atkReuse;
	}

	/**
	 * Returns the avoid modifier of the weapon
	 * @return int
	 */
	public int getAvoidModifier()
	{
		return _avoidModifier;
	}

	/**
	 * Returns the rate of critical hit
	 * @return int
	 */
	public int getCritical()
	{
		return _critical;
	}

	/**
	 * Returns the hit modifier of the weapon
	 * @return double
	 */
	public double getHitModifier()
	{
		return _hitModifier;
	}

	/**
	 * Returns the magical damage inflicted by the weapon
	 * @return int
	 */
	public int getMDamage()
	{
		return _mDam;
	}

	/**
	 * Returns the MP consumption with the weapon
	 * @return int
	 */
	public int getMpConsume()
	{
		return _mpConsume;
	}

	/**
	 * Returns the shield defense of the weapon
	 * @return int
	 */
	public int getShieldDef()
	{
		return _shieldDef;
	}

	/**
	 * Returns the rate of shield defense of the weapon
	 * @return double
	 */
	public double getShieldDefRate()
	{
		return _shieldDefRate;
	}

	/**
	 * Returns passive skill linked to that weapon
	 * @return
	 */
	public L2Skill getSkill()
	{
		return _itemSkill;
	}

	/**
	 * Returns skill that player get when has equiped weapon +4 or more (for duals SA)
	 * @return
	 */
	public L2Skill getEnchant4Skill()
	{
		return _enchant4Skill;
	}

	/**
	 * Returns array of Func objects containing the list of functions used by the weapon
	 * @param instance : L2ItemInstance pointing out the weapon
	 * @param player : L2Character pointing out the player
	 * @return Func[] : array of functions
	 */
	@Override
	public Func[] getStatFuncs(final L2ItemInstance instance, final L2Character player)
	{
		final List<Func> funcs = new FastList<>();
		if (_funcTemplates != null)
		{
			for (final FuncTemplate t : _funcTemplates)
			{
				final Env env = new Env();
				env.player = player;
				env.item = instance;
				final Func f = t.getFunc(env, instance);
				if (f != null)
				{
					funcs.add(f);
				}
			}
		}
		return funcs.toArray(new Func[funcs.size()]);
	}

	/**
	 * Returns effects of skills associated with the item to be triggered onHit.
	 * @param caster : L2Character pointing out the caster
	 * @param target : L2Character pointing out the target
	 * @param crit : boolean tells whether the hit was critical
	 * @return L2Effect[] : array of effects generated by the skill
	 */
	public L2Effect[] getSkillEffects(final L2Character caster, final L2Character target, final boolean crit)
	{
		if ((_skillsOnCrit == null) || !crit)
		{
			return _emptyEffectSet;
		}
		final List<L2Effect> effects = new FastList<>();

		for (final L2Skill skill : _skillsOnCrit)
		{
			if (target.isRaid() && ((skill.getSkillType() == SkillType.CONFUSION) || (skill.getSkillType() == SkillType.MUTE) || (skill.getSkillType() == SkillType.PARALYZE) || (skill.getSkillType() == SkillType.ROOT)))
			{
				continue; // These skills should not work on RaidBoss
			}

			if (!skill.checkCondition(caster, target, true))
			{
				continue; // Skill condition not met
			}

			if (target.getFirstEffect(skill.getId()) != null)
			{
				target.getFirstEffect(skill.getId()).exit(false);
			}
			for (final L2Effect e : skill.getEffects(caster, target, false, false, false))
			{
				effects.add(e);
			}
		}
		if (effects.size() == 0)
		{
			return _emptyEffectSet;
		}
		return effects.toArray(new L2Effect[effects.size()]);
	}

	/**
	 * Returns effects of skills associated with the item to be triggered onCast.
	 * @param caster : L2Character pointing out the caster
	 * @param target : L2Character pointing out the target
	 * @param trigger : L2Skill pointing out the skill triggering this action
	 * @return L2Effect[] : array of effects generated by the skill
	 */
	public boolean getSkillEffects(final L2Character caster, final L2Character target, final L2Skill trigger)
	{
		boolean output = false;

		if (_skillsOnCast == null)
		{
			return output;
		}
		
		// return _emptyEffectSet;
		// List<L2Effect> effects = new FastList<L2Effect>();

		for (final L2Skill skill : _skillsOnCast)
		{
			if ((trigger.isOffensive() != skill.isOffensive()) || ((trigger.getId() >= 1320) && (trigger.getId() <= 1322)) || trigger.isPotion())
			{
				continue; // No buff with potions
			}

			if (target.isRaid() && ((skill.getSkillType() == SkillType.CONFUSION) || (skill.getSkillType() == SkillType.MUTE) || (skill.getSkillType() == SkillType.PARALYZE) || (skill.getSkillType() == SkillType.ROOT)))
			{
				continue; // These skills should not work on RaidBoss
			}

			if (trigger.isToggle()/* && skill.getSkillType() == SkillType.BUFF */)
			{
				continue; // No buffing with toggle skills
			}

			if (!skill.checkCondition(caster, target, true)) // check skill condition and chance
			{
				continue; // Skill condition not met
			}

			try
			{
				// Get the skill handler corresponding to the skill type
				final ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());

				final L2Character[] targets = new L2Character[1];
				targets[0] = target;

				// Launch the magic skill and calculate its effects
				if (handler != null)
				{
					handler.useSkill(caster, skill, targets);
				}
				else
				{
					skill.useSkill(caster, targets);
				}

				if ((caster instanceof L2PcInstance) && (target instanceof L2NpcInstance))
				{
					for (final Quest quest : ((L2NpcInstance) target).getTemplate().getEventQuests(Quest.QuestEventType.ON_SKILL_USE))
					{
						quest.notifySkillUse((L2NpcInstance) target, (L2PcInstance) caster, skill);
					}
				}

				output = true;
			}
			catch (final IOException e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e.printStackTrace();
				}
			}
		}
		// if(effects.size() == 0)
		// return _emptyEffectSet;
		// return effects.toArray(new L2Effect[effects.size()]);

		return output;
	}

	/**
	 * Add the L2Skill skill to the list of skills generated by the item triggered by critical hit
	 * @param skill : L2Skill
	 */
	public void attachOnCrit(final L2Skill skill)
	{
		if (_skillsOnCrit == null)
		{
			_skillsOnCrit = new L2Skill[]
			{
				skill
			};
		}
		else
		{
			final int len = _skillsOnCrit.length;
			final L2Skill[] tmp = new L2Skill[len + 1];
			// Definition : arraycopy(array source, begins copy at this position of source, array destination, begins copy at this position in dest,
			// number of components to be copied)
			System.arraycopy(_skillsOnCrit, 0, tmp, 0, len);
			tmp[len] = skill;
			_skillsOnCrit = tmp;
		}
	}

	/**
	 * Add the L2Skill skill to the list of skills generated by the item triggered by casting spell
	 * @param skill : L2Skill
	 */
	public void attachOnCast(final L2Skill skill)
	{
		if (_skillsOnCast == null)
		{
			_skillsOnCast = new L2Skill[]
			{
				skill
			};
		}
		else
		{
			final int len = _skillsOnCast.length;
			final L2Skill[] tmp = new L2Skill[len + 1];
			// Definition : arraycopy(array source, begins copy at this position of source, array destination, begins copy at this position in dest,
			// number of components to be copied)
			System.arraycopy(_skillsOnCast, 0, tmp, 0, len);
			tmp[len] = skill;
			_skillsOnCast = tmp;
		}
	}
}
