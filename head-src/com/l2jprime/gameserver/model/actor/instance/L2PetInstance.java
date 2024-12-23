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
package com.l2jprime.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.gameserver.ai.CtrlIntention;
import com.l2jprime.gameserver.datatables.sql.L2PetDataTable;
import com.l2jprime.gameserver.idfactory.IdFactory;
import com.l2jprime.gameserver.managers.CursedWeaponsManager;
import com.l2jprime.gameserver.managers.ItemsOnGroundManager;
import com.l2jprime.gameserver.model.Inventory;
import com.l2jprime.gameserver.model.L2Character;
import com.l2jprime.gameserver.model.L2Object;
import com.l2jprime.gameserver.model.L2PetData;
import com.l2jprime.gameserver.model.L2Skill;
import com.l2jprime.gameserver.model.L2Summon;
import com.l2jprime.gameserver.model.L2World;
import com.l2jprime.gameserver.model.PcInventory;
import com.l2jprime.gameserver.model.PetInventory;
import com.l2jprime.gameserver.model.actor.stat.PetStat;
import com.l2jprime.gameserver.model.entity.olympiad.Olympiad;
import com.l2jprime.gameserver.network.SystemMessageId;
import com.l2jprime.gameserver.network.serverpackets.ActionFailed;
import com.l2jprime.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jprime.gameserver.network.serverpackets.ItemList;
import com.l2jprime.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jprime.gameserver.network.serverpackets.PetInventoryUpdate;
import com.l2jprime.gameserver.network.serverpackets.PetItemList;
import com.l2jprime.gameserver.network.serverpackets.PetStatusShow;
import com.l2jprime.gameserver.network.serverpackets.StatusUpdate;
import com.l2jprime.gameserver.network.serverpackets.StopMove;
import com.l2jprime.gameserver.network.serverpackets.SystemMessage;
import com.l2jprime.gameserver.taskmanager.DecayTaskManager;
import com.l2jprime.gameserver.templates.L2Item;
import com.l2jprime.gameserver.templates.L2NpcTemplate;
import com.l2jprime.gameserver.templates.L2Weapon;
import com.l2jprime.gameserver.thread.ThreadPoolManager;
import com.l2jprime.util.CloseUtil;
import com.l2jprime.util.database.DatabaseUtils;
import com.l2jprime.util.database.L2DatabaseFactory;

/**
 * This class ...
 * @version $Revision: 1.15.2.10.2.16 $ $Date: 2009/04/13 09:18:40 $
 */
public class L2PetInstance extends L2Summon
{

	/** The Constant _logPet. */
	protected static final Logger LOGGER = Logger.getLogger(L2PetInstance.class);

	// private byte _pvpFlag;
	/** The _cur fed. */
	private int _curFed;

	/** The _inventory. */
	private final PetInventory _inventory;

	/** The _control item id. */
	private final int _controlItemId;

	/** The _respawned. */
	private boolean _respawned;

	/** The _mountable. */
	private final boolean _mountable;

	/** The _feed task. */
	private Future<?> _feedTask;

	/** The _feed time. */
	private int _feedTime;

	/** The _feed mode. */
	protected boolean _feedMode;

	/** The _data. */
	private L2PetData _data;

	/** The Experience before the last Death Penalty. */
	private long _expBeforeDeath = 0;

	/** The Constant FOOD_ITEM_CONSUME_COUNT. */
	private static final int FOOD_ITEM_CONSUME_COUNT = 5;

	/**
	 * Gets the pet data.
	 * @return the pet data
	 */
	public final L2PetData getPetData()
	{
		if (_data == null)
		{
			_data = L2PetDataTable.getInstance().getPetData(getTemplate().npcId, getStat().getLevel());
		}

		return _data;
	}

	/**
	 * Sets the pet data.
	 * @param value the new pet data
	 */
	public final void setPetData(final L2PetData value)
	{
		_data = value;
	}

	/**
	 * Manage Feeding Task.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Feed or kill the pet depending on hunger level</li>
	 * <li>If pet has food in inventory and feed level drops below 55% then consume food from inventory</li>
	 * <li>Send a broadcastStatusUpdate packet for this L2PetInstance</li> <BR>
	 * <BR>
	 */

	class FeedTask implements Runnable
	{

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			try
			{
				// if pet is attacking
				if (isAttackingNow())
				{
					// if its not already on battleFeed mode
					if (!_feedMode)
					{
						startFeed(true); // switching to battle feed
					}
					else
					// if its on battleFeed mode
					if (_feedMode)
					{
						startFeed(false); // normal feed
					}
				}

				if (getCurrentFed() > FOOD_ITEM_CONSUME_COUNT)
				{
					// eat
					setCurrentFed(getCurrentFed() - FOOD_ITEM_CONSUME_COUNT);
				}
				else
				{
					// go back to pet control item, or simply said, unsummon it
					setCurrentFed(0);
					stopFeed();
					unSummon(getOwner());
					getOwner().sendMessage("Your pet is too hungry to stay summoned.");
				}

				final int foodId = L2PetDataTable.getFoodItemId(getTemplate().npcId);
				if (foodId == 0)
				{
					return;
				}

				L2ItemInstance food = null;
				food = getInventory().getItemByItemId(foodId);

				if ((food != null) && (getCurrentFed() < (0.55 * getMaxFed())))
				{
					if (destroyItem("Feed", food.getObjectId(), 1, null, false))
					{
						setCurrentFed(getCurrentFed() + 100);
						if (getOwner() != null)
						{
							SystemMessage sm = new SystemMessage(SystemMessageId.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY);
							sm.addItemName(foodId);
							getOwner().sendPacket(sm);
							sm = null;
						}
					}
				}

				food = null;

				broadcastStatusUpdate();
			}
			catch (final Throwable e)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e.printStackTrace();
				}

				if (Config.DEBUG)
				{
					LOGGER.debug("Pet [#" + getObjectId() + "] a feed task error has occurred: " + e);
				}
			}
		}
	}

	/**
	 * Spawn pet.
	 * @param template the template
	 * @param owner the owner
	 * @param control the control
	 * @return the l2 pet instance
	 */
	public synchronized static L2PetInstance spawnPet(final L2NpcTemplate template, final L2PcInstance owner, final L2ItemInstance control)
	{
		if (L2World.getInstance().getPet(owner.getObjectId()) != null)
		{
			return null; // owner has a pet listed in world
		}
		
		final L2PetInstance pet = restore(control, template, owner);
		// add the pet instance to world
		if (pet != null)
		{
			// fix pet title
			pet.setTitle(owner.getName());
			L2World.getInstance().addPet(owner.getObjectId(), pet);
		}

		return pet;
	}

	/**
	 * Instantiates a new l2 pet instance.
	 * @param objectId the object id
	 * @param template the template
	 * @param owner the owner
	 * @param control the control
	 */
	public L2PetInstance(final int objectId, final L2NpcTemplate template, final L2PcInstance owner, final L2ItemInstance control)
	{
		super(objectId, template, owner);
		super.setStat(new PetStat(this));

		_controlItemId = control.getObjectId();

		// Pet's initial level is supposed to be read from DB
		// Pets start at :
		// Wolf : Level 15
		// Hatcling : Level 35
		// Tested and confirmed on official servers
		// Sin-eaters are defaulted at the owner's level
		if (template.npcId == 12564)
		{
			getStat().setLevel((byte) getOwner().getLevel());
		}
		else
		{
			getStat().setLevel(template.level);
		}

		_inventory = new PetInventory(this);

		final int npcId = template.npcId;
		_mountable = L2PetDataTable.isMountable(npcId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getStat()
	 */
	@Override
	public PetStat getStat()
	{
		if ((super.getStat() == null) || !(super.getStat() instanceof PetStat))
		{
			setStat(new PetStat(this));
		}
		return (PetStat) super.getStat();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getLevelMod()
	 */
	@Override
	public double getLevelMod()
	{
		return ((100.0 - 11) + getLevel()) / 100.0;
	}

	/**
	 * Checks if is respawned.
	 * @return true, if is respawned
	 */
	public boolean isRespawned()
	{
		return _respawned;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getSummonType()
	 */
	@Override
	public int getSummonType()
	{
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#onAction(com.l2jprime.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public void onAction(final L2PcInstance player)
	{
		final boolean isOwner = player.getObjectId() == getOwner().getObjectId();
		final boolean thisIsTarget = (player.getTarget() != null) && (player.getTarget().getObjectId() == getObjectId());

		if (isOwner && thisIsTarget)
		{
			if (isOwner && (player != getOwner()))
			{
				// update owner
				updateRefOwner(player);
			}
			player.sendPacket(new PetStatusShow(this));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			if (Config.DEBUG)
			{
				LOGGER.debug("new target selected:" + getObjectId());
			}

			player.setTarget(this);
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			my = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getControlItemId()
	 */
	@Override
	public int getControlItemId()
	{
		return _controlItemId;
	}

	/**
	 * Gets the control item.
	 * @return the control item
	 */
	public L2ItemInstance getControlItem()
	{
		return getOwner().getInventory().getItemByObjectId(_controlItemId);
	}

	/**
	 * Gets the current fed.
	 * @return the current fed
	 */
	public int getCurrentFed()
	{
		return _curFed;
	}

	/**
	 * Sets the current fed.
	 * @param num the new current fed
	 */
	public void setCurrentFed(final int num)
	{
		_curFed = num > getMaxFed() ? getMaxFed() : num;
	}

	// public void setPvpFlag(byte pvpFlag) { _pvpFlag = pvpFlag; }

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#setPkKills(int)
	 */
	@Override
	public void setPkKills(final int pkKills)
	{
		_pkKills = pkKills;
	}

	/**
	 * Returns the pet's currently equipped weapon instance (if any).
	 * @return the active weapon instance
	 */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		for (final L2ItemInstance item : getInventory().getItems())
		{
			if ((item.getLocation() == L2ItemInstance.ItemLocation.PET_EQUIP) && (item.getItem().getBodyPart() == L2Item.SLOT_R_HAND))
			{
				return item;
			}
		}
		
		return null;
	}

	/**
	 * Returns the pet's currently equipped weapon (if any).
	 * @return the active weapon item
	 */
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		final L2ItemInstance weapon = getActiveWeaponInstance();

		if (weapon == null)
		{
			return null;
		}

		return (L2Weapon) weapon.getItem();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getSecondaryWeaponInstance()
	 */
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		// temporary? unavailable
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getSecondaryWeaponItem()
	 */
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		// temporary? unavailable
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getInventory()
	 */
	@Override
	public PetInventory getInventory()
	{
		return _inventory;
	}

	/**
	 * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItem(final String process, final int objectId, final int count, final L2Object reference, final boolean sendMessage)
	{
		L2ItemInstance item = _inventory.destroyItem(process, objectId, count, getOwner(), reference);

		if (item == null)
		{
			if (sendMessage)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			}

			return false;
		}

		// Send Pet inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		getOwner().sendPacket(petIU);
		petIU = null;

		if (sendMessage)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
			sm.addNumber(count);
			sm.addItemName(item.getItemId());
			getOwner().sendPacket(sm);
			sm = null;
		}

		item = null;
		return true;
	}

	/**
	 * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItemByItemId(final String process, final int itemId, final int count, final L2Object reference, final boolean sendMessage)
	{
		L2ItemInstance item = _inventory.destroyItemByItemId(process, itemId, count, getOwner(), reference);

		if (item == null)
		{
			if (sendMessage)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
			}
			return false;
		}

		// Send Pet inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		getOwner().sendPacket(petIU);
		item = null;
		petIU = null;

		if (sendMessage)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
			sm.addNumber(count);
			sm.addItemName(itemId);
			getOwner().sendPacket(sm);
			sm = null;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#doPickupItem(com.l2jprime.gameserver.model.L2Object)
	 */
	@Override
	protected void doPickupItem(final L2Object object)
	{
		getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		StopMove sm = new StopMove(getObjectId(), getX(), getY(), getZ(), getHeading());

		if (Config.DEBUG)
		{
			LOGGER.debug("Pet pickup pos: " + object.getX() + " " + object.getY() + " " + object.getZ());
		}

		broadcastPacket(sm);
		sm = null;

		if (!(object instanceof L2ItemInstance))
		{
			// dont try to pickup anything that is not an item :)
			LOGGER.warn("Trying to pickup wrong target." + object);
			getOwner().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		L2ItemInstance target = (L2ItemInstance) object;

		// Herbs
		// Cursed weapons
		if (((target.getItemId() > 8599) && (target.getItemId() < 8615)) || CursedWeaponsManager.getInstance().isCursed(target.getItemId()))
		{
			SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
			smsg.addItemName(target.getItemId());
			getOwner().sendPacket(smsg);
			smsg = null;
			return;
		}

		synchronized (target)
		{
			if (!target.isVisible())
			{
				getOwner().sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}

			if (!target.getDropProtection().tryPickUp(this))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				final SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
				smsg.addItemName(target.getItemId());
				getOwner().sendPacket(smsg);
				return;
			}

			if ((target.getOwnerId() != 0) && (target.getOwnerId() != getOwner().getObjectId()) && !getOwner().isInLooterParty(target.getOwnerId()))
			{
				getOwner().sendPacket(ActionFailed.STATIC_PACKET);

				if (target.getItemId() == 57)
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA);
					smsg.addNumber(target.getCount());
					getOwner().sendPacket(smsg);
					smsg = null;
				}
				else if (target.getCount() > 1)
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S);
					smsg.addItemName(target.getItemId());
					smsg.addNumber(target.getCount());
					getOwner().sendPacket(smsg);
					smsg = null;
				}
				else
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1);
					smsg.addItemName(target.getItemId());
					getOwner().sendPacket(smsg);
					smsg = null;
				}

				return;
			}
			if ((target.getItemLootShedule() != null) && ((target.getOwnerId() == getOwner().getObjectId()) || getOwner().isInLooterParty(target.getOwnerId())))
			{
				target.resetOwnerTimer();
			}

			target.pickupMe(this);

			if (Config.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().removeObject(target);
			}
		}

		getInventory().addItem("Pickup", target, getOwner(), this);
		// FIXME Just send the updates if possible (old way wasn't working though)
		PetItemList iu = new PetItemList(this);
		getOwner().sendPacket(iu);
		iu = null;

		getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

		if (getFollowStatus())
		{
			followOwner();
		}

		target = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#deleteMe(com.l2jprime.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public void deleteMe(final L2PcInstance owner)
	{
		super.deleteMe(owner);
		destroyControlItem(owner); // this should also delete the pet from the db
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#doDie(com.l2jprime.gameserver.model.L2Character)
	 */
	@Override
	public boolean doDie(final L2Character killer)
	{
		if (!super.doDie(killer, true))
		{
			return false;
		}
		stopFeed();
		DecayTaskManager.getInstance().addDecayTask(this, 1200000);
		deathPenalty();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#doRevive()
	 */
	@Override
	public void doRevive()
	{
		if (_curFed > (getMaxFed() / 10))
		{
			_curFed = getMaxFed() / 10;
		}

		getOwner().removeReviving();

		super.doRevive();

		// stopDecay
		DecayTaskManager.getInstance().cancelDecayTask(this);
		startFeed(false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#doRevive(double)
	 */
	@Override
	public void doRevive(final double revivePower)
	{
		// Restore the pet's lost experience,
		// depending on the % return of the skill used (based on its power).
		restoreExp(revivePower);
		doRevive();
	}

	/**
	 * Transfers item to another inventory.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId the object id
	 * @param count : int Quantity of items to be transfered
	 * @param target the target
	 * @param actor : L2PcInstance Player requesting the item transfer
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public L2ItemInstance transferItem(final String process, final int objectId, final int count, final Inventory target, final L2PcInstance actor, final L2Object reference)
	{
		L2ItemInstance oldItem = getInventory().getItemByObjectId(objectId);
		final L2ItemInstance newItem = getInventory().transferItem(process, objectId, count, target, actor, reference);

		if (newItem == null)
		{
			return null;
		}

		// Send inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		if ((oldItem.getCount() > 0) && (oldItem != newItem))
		{
			petIU.addModifiedItem(oldItem);
		}
		else
		{
			petIU.addRemovedItem(oldItem);
		}

		getOwner().sendPacket(petIU);

		oldItem = null;
		petIU = null;

		// Send target update packet
		if (target instanceof PcInventory)
		{
			L2PcInstance targetPlayer = ((PcInventory) target).getOwner();
			InventoryUpdate playerUI = new InventoryUpdate();
			if (newItem.getCount() > count)
			{
				playerUI.addModifiedItem(newItem);
			}
			else
			{
				playerUI.addNewItem(newItem);
			}
			targetPlayer.sendPacket(playerUI);
			playerUI = null;

			// Update current load as well
			StatusUpdate playerSU = new StatusUpdate(targetPlayer.getObjectId());
			playerSU.addAttribute(StatusUpdate.CUR_LOAD, targetPlayer.getCurrentLoad());
			targetPlayer.sendPacket(playerSU);
			playerSU = null;
			targetPlayer = null;
		}
		else if (target instanceof PetInventory)
		{
			petIU = new PetInventoryUpdate();
			if (newItem.getCount() > count)
			{
				petIU.addRemovedItem(newItem);
			}
			else
			{
				petIU.addNewItem(newItem);
			}
			((PetInventory) target).getOwner().getOwner().sendPacket(petIU);
			petIU = null;
		}
		return newItem;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#giveAllToOwner()
	 */
	@Override
	public void giveAllToOwner()
	{
		try
		{
			Inventory petInventory = getInventory();
			L2ItemInstance[] items = petInventory.getItems();
			petInventory = null;
			for (final L2ItemInstance item : items)
			{
				L2ItemInstance giveit = item;
				if (((giveit.getItem().getWeight() * giveit.getCount()) + getOwner().getInventory().getTotalWeight()) < getOwner().getMaxLoad())
				{
					// If the owner can carry it give it to them
					giveItemToOwner(giveit);
				}
				else
				{
					// If they can't carry it, chuck it on the floor :)
					dropItemHere(giveit);
				}
				giveit = null;
			}
			items = null;
		}
		catch (final Exception e)
		{
			LOGGER.error("Give all items error", e);
		}
	}

	/**
	 * Give item to owner.
	 * @param item the item
	 */
	public void giveItemToOwner(final L2ItemInstance item)
	{
		try
		{
			getInventory().transferItem("PetTransfer", item.getObjectId(), item.getCount(), getOwner().getInventory(), getOwner(), this);
			PetInventoryUpdate petiu = new PetInventoryUpdate();
			ItemList PlayerUI = new ItemList(getOwner(), false);
			petiu.addRemovedItem(item);
			getOwner().sendPacket(petiu);
			getOwner().sendPacket(PlayerUI);

			petiu = null;
			PlayerUI = null;
		}
		catch (final Exception e)
		{
			LOGGER.error("Error while giving item to owner", e);
		}
	}

	/**
	 * Remove the Pet from DB and its associated item from the player inventory.
	 * @param owner The owner from whose invenory we should delete the item
	 */
	public void destroyControlItem(final L2PcInstance owner)
	{
		// remove the pet instance from world
		L2World.getInstance().removePet(owner.getObjectId());

		// delete from inventory
		try
		{
			L2ItemInstance removedItem = owner.getInventory().destroyItem("PetDestroy", getControlItemId(), 1, getOwner(), this);

			InventoryUpdate iu = new InventoryUpdate();
			iu.addRemovedItem(removedItem);
			owner.sendPacket(iu);

			iu = null;

			StatusUpdate su = new StatusUpdate(owner.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, owner.getCurrentLoad());
			owner.sendPacket(su);
			su = null;

			owner.broadcastUserInfo();

			L2World world = L2World.getInstance();
			world.removeObject(removedItem);

			removedItem = null;
			world = null;
		}
		catch (final Exception e)
		{
			LOGGER.error("Error while destroying control item", e);
		}

		// pet control item no longer exists, delete the pet from the db
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			statement.setInt(1, getControlItemId());
			statement.execute();
			DatabaseUtils.close(statement);
			statement = null;
		}
		catch (final Exception e)
		{
			LOGGER.error("could not delete pet", e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}

	/**
	 * Drop all items.
	 */
	public void dropAllItems()
	{
		try
		{
			L2ItemInstance[] items = getInventory().getItems();
			for (final L2ItemInstance item : items)
			{
				dropItemHere(item);
			}
			items = null;
		}
		catch (final Exception e)
		{
			LOGGER.error("Pet Drop Error", e);
		}
	}

	/**
	 * Drop item here.
	 * @param dropit the dropit
	 */
	public void dropItemHere(final L2ItemInstance dropit)
	{
		dropItemHere(dropit, false);
	}

	/**
	 * Drop item here.
	 * @param dropit the dropit
	 * @param protect the protect
	 */
	public void dropItemHere(L2ItemInstance dropit, final boolean protect)
	{

		dropit = getInventory().dropItem("Drop", dropit.getObjectId(), dropit.getCount(), getOwner(), this);

		if (dropit != null)
		{

			if (protect)
			{
				dropit.getDropProtection().protect(getOwner());
			}

			LOGGER.debug("Item id to drop: " + dropit.getItemId() + " amount: " + dropit.getCount());
			dropit.dropMe(this, getX(), getY(), getZ() + 100);
		}
	}

	// public void startAttack(L2Character target)
	// {
	// if (!knownsObject(target))
	// {
	// target.addKnownObject(this);
	// this.addKnownObject(target);
	// }
	// if (!target.knownsObject(this))
	// {
	// target.addKnownObject(this);
	// this.addKnownObject(target);
	// }
	//
	// if (!isRunning())
	// {
	// setRunning(true);
	// ChangeMoveType move = new ChangeMoveType(this, ChangeMoveType.RUN);
	// broadcastPacket(move);
	// }
	//
	// super.startAttack(target);
	// }
	//
	/**
	 * Checks if is mountable.
	 * @return Returns the mountable.
	 */
	@Override
	public boolean isMountable()
	{
		return _mountable;
	}

	/**
	 * Restore.
	 * @param control the control
	 * @param template the template
	 * @param owner the owner
	 * @return the l2 pet instance
	 */
	private static L2PetInstance restore(final L2ItemInstance control, final L2NpcTemplate template, final L2PcInstance owner)
	{
		Connection con = null;

		L2PetInstance pet = null;

		try
		{
			if (template.type.compareToIgnoreCase("L2BabyPet") == 0)
			{
				pet = new L2BabyPetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
			}
			else
			{
				pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
			}

			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement("SELECT item_obj_id, name, level, curHp, curMp, exp, sp, karma, pkkills, fed FROM pets WHERE item_obj_id=?");
			statement.setInt(1, control.getObjectId());
			ResultSet rset = statement.executeQuery();
			if (!rset.next())
			{
				DatabaseUtils.close(rset);
				DatabaseUtils.close(statement);
				rset = null;
				statement = null;

				CloseUtil.close(con);
				con = null;

				return pet;
			}

			pet._respawned = true;
			pet.setName(rset.getString("name"));

			pet.getStat().setLevel(rset.getByte("level"));
			pet.getStat().setExp(rset.getLong("exp"));
			pet.getStat().setSp(rset.getInt("sp"));

			pet.getStatus().setCurrentHp(rset.getDouble("curHp"));
			pet.getStatus().setCurrentMp(rset.getDouble("curMp"));
			pet.getStatus().setCurrentCp(pet.getMaxCp());

			// pet.setKarma(rset.getInt("karma"));
			pet.setPkKills(rset.getInt("pkkills"));
			pet.setCurrentFed(rset.getInt("fed"));

			DatabaseUtils.close(rset);
			DatabaseUtils.close(statement);
			rset = null;
			statement = null;

		}
		catch (final Exception e)
		{
			LOGGER.error("Could not restore pet data", e);

		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}

		return pet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#store()
	 */
	@Override
	public void store()
	{
		if (getControlItemId() == 0)
		{
			// this is a summon, not a pet, don't store anything
			return;
		}

		String req;
		if (!isRespawned())
		{
			req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,karma,pkkills,fed,item_obj_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?)";
		}
		else
		{
			req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,karma=?,pkkills=?,fed=? " + "WHERE item_obj_id = ?";
		}
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			PreparedStatement statement = con.prepareStatement(req);
			statement.setString(1, getName());
			statement.setInt(2, getStat().getLevel());
			statement.setDouble(3, getStatus().getCurrentHp());
			statement.setDouble(4, getStatus().getCurrentMp());
			statement.setLong(5, getStat().getExp());
			statement.setInt(6, getStat().getSp());
			statement.setInt(7, getKarma());
			statement.setInt(8, getPkKills());
			statement.setInt(9, getCurrentFed());
			statement.setInt(10, getControlItemId());
			statement.executeUpdate();
			DatabaseUtils.close(statement);
			statement = null;
			_respawned = true;
		}
		catch (final Exception e)
		{
			LOGGER.error("could not store pet data", e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}

		L2ItemInstance itemInst = getControlItem();
		if ((itemInst != null) && (itemInst.getEnchantLevel() != getStat().getLevel()))
		{
			itemInst.setEnchantLevel(getStat().getLevel());
			itemInst.updateDatabase();
		}
		itemInst = null;
	}

	/**
	 * Stop feed.
	 */
	public synchronized void stopFeed()
	{
		if (_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
			if (Config.DEBUG)
			{
				LOGGER.debug("Pet [#" + getObjectId() + "] feed task stop");
			}
		}
	}

	/**
	 * Start feed.
	 * @param battleFeed the battle feed
	 */
	public synchronized void startFeed(final boolean battleFeed)
	{
		// stop feeding task if its active

		stopFeed();
		if (!isDead())
		{
			if (battleFeed)
			{
				_feedMode = true;
				_feedTime = _data.getPetFeedBattle();
			}
			else
			{
				_feedMode = false;
				_feedTime = _data.getPetFeedNormal();
			}
			// pet feed time must be different than 0. Changing time to bypass divide by 0
			if (_feedTime <= 0)
			{
				_feedTime = 1;
			}

			_feedTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new FeedTask(), 60000 / _feedTime, 60000 / _feedTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#unSummon(com.l2jprime.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public synchronized void unSummon(final L2PcInstance owner)
	{
		stopFeed();
		stopHpMpRegeneration();
		super.unSummon(owner);

		if (!isDead())
		{
			L2World.getInstance().removePet(owner.getObjectId());
		}
	}

	/**
	 * Restore the specified % of experience this L2PetInstance has lost.<BR>
	 * <BR>
	 * @param restorePercent the restore percent
	 */
	public void restoreExp(final double restorePercent)
	{
		if (_expBeforeDeath > 0)
		{
			// Restore the specified % of lost experience.
			getStat().addExp(Math.round(((_expBeforeDeath - getStat().getExp()) * restorePercent) / 100));
			_expBeforeDeath = 0;
		}
	}

	/**
	 * Death penalty.
	 */
	private void deathPenalty()
	{
		// TODO Need Correct Penalty

		final int lvl = getStat().getLevel();
		final double percentLost = (-0.07 * lvl) + 6.5;

		// Calculate the Experience loss
		final long lostExp = Math.round(((getStat().getExpForLevel(lvl + 1) - getStat().getExpForLevel(lvl)) * percentLost) / 100);

		// Get the Experience before applying penalty
		_expBeforeDeath = getStat().getExp();

		// Set the new Experience value of the L2PetInstance
		getStat().addExp(-lostExp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#addExpAndSp(long, int)
	 */
	@Override
	public void addExpAndSp(final long addToExp, final int addToSp)
	{
		if (getNpcId() == 12564)
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.SINEATER_XP_RATE), addToSp);
		}
		else
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.PET_XP_RATE), addToSp);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getExpForThisLevel()
	 */
	@Override
	public long getExpForThisLevel()
	{
		return getStat().getExpForLevel(getLevel());
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Summon#getExpForNextLevel()
	 */
	@Override
	public long getExpForNextLevel()
	{
		return getStat().getExpForLevel(getLevel() + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getLevel()
	 */
	@Override
	public final int getLevel()
	{
		return getStat().getLevel();
	}

	/**
	 * Gets the max fed.
	 * @return the max fed
	 */
	public int getMaxFed()
	{
		return getStat().getMaxFeed();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getAccuracy()
	 */
	@Override
	public int getAccuracy()
	{
		return getStat().getAccuracy();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getCriticalHit(com.l2jprime.gameserver.model.L2Character, com.l2jprime.gameserver.model.L2Skill)
	 */
	@Override
	public int getCriticalHit(final L2Character target, final L2Skill skill)
	{
		return getStat().getCriticalHit(target, skill);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getEvasionRate(com.l2jprime.gameserver.model.L2Character)
	 */
	@Override
	public int getEvasionRate(final L2Character target)
	{
		return getStat().getEvasionRate(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getRunSpeed()
	 */
	@Override
	public int getRunSpeed()
	{
		return getStat().getRunSpeed();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getPAtkSpd()
	 */
	@Override
	public int getPAtkSpd()
	{
		return getStat().getPAtkSpd();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getMAtkSpd()
	 */
	@Override
	public int getMAtkSpd()
	{
		return getStat().getMAtkSpd();
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getMAtk(com.l2jprime.gameserver.model.L2Character, com.l2jprime.gameserver.model.L2Skill)
	 */
	@Override
	public int getMAtk(final L2Character target, final L2Skill skill)
	{
		return getStat().getMAtk(target, skill);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getMDef(com.l2jprime.gameserver.model.L2Character, com.l2jprime.gameserver.model.L2Skill)
	 */
	@Override
	public int getMDef(final L2Character target, final L2Skill skill)
	{
		return getStat().getMDef(target, skill);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getPAtk(com.l2jprime.gameserver.model.L2Character)
	 */
	@Override
	public int getPAtk(final L2Character target)
	{
		return getStat().getPAtk(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getPDef(com.l2jprime.gameserver.model.L2Character)
	 */
	@Override
	public int getPDef(final L2Character target)
	{
		return getStat().getPDef(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#getSkillLevel(int)
	 */
	@Override
	public final int getSkillLevel(final int skillId)
	{
		if ((_skills == null) || (_skills.get(skillId) == null))
		{
			return -1;
		}
		final int lvl = getLevel();
		return lvl > 70 ? 7 + ((lvl - 70) / 5) : lvl / 10;
	}

	/**
	 * Update ref owner.
	 * @param owner the owner
	 */
	public void updateRefOwner(final L2PcInstance owner)
	{
		final int oldOwnerId = getOwner().getObjectId();

		setOwner(owner);
		L2World.getInstance().removePet(oldOwnerId);
		L2World.getInstance().addPet(oldOwnerId, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.model.L2Character#sendDamageMessage(com.l2jprime.gameserver.model.L2Character, int, boolean, boolean, boolean)
	 */
	@Override
	public final void sendDamageMessage(final L2Character target, final int damage, final boolean mcrit, final boolean pcrit, final boolean miss)
	{
		if (miss)
		{
			return;
		}

		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
			{
				getOwner().sendPacket(new SystemMessage(SystemMessageId.CRITICAL_HIT_BY_PET));
			}

			SystemMessage sm = new SystemMessage(SystemMessageId.PET_HIT_FOR_S1_DAMAGE);
			sm.addNumber(damage);
			getOwner().sendPacket(sm);
			sm = null;
		}

		if (getOwner().isInOlympiadMode() && (target instanceof L2PcInstance) && ((L2PcInstance) target).isInOlympiadMode() && (((L2PcInstance) target).getOlympiadGameId() == getOwner().getOlympiadGameId()))
		{
			Olympiad.getInstance().notifyCompetitorDamage(getOwner(), damage, getOwner().getOlympiadGameId());
		}
	}
}
