/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.item;

/**
 * Special thanks to nuocnam
 * @author LittleVexy
 */
public class DropData
{
	public static final int MAX_CHANCE = 1000000;
	
	private int _itemId;
	private int _minDrop;
	private int _maxDrop;
	private int _chance;
	private String _questID = null;
	private String[] _stateID = null;
	
	/**
	 * Returns the ID of the item dropped
	 * @return int
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * Sets the ID of the item dropped
	 * @param itemId : int designating the ID of the item
	 */
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	/**
	 * Returns the minimum quantity of items dropped
	 * @return int
	 */
	public int getMinDrop()
	{
		return _minDrop;
	}
	
	/**
	 * Returns the maximum quantity of items dropped
	 * @return int
	 */
	public int getMaxDrop()
	{
		return _maxDrop;
	}
	
	/**
	 * Returns the chance of having a drop
	 * @return int
	 */
	public int getChance()
	{
		return _chance;
	}
	
	/**
	 * Sets the value for minimal quantity of dropped items
	 * @param mindrop : int designating the quantity
	 */
	public void setMinDrop(int mindrop)
	{
		_minDrop = mindrop;
	}
	
	/**
	 * Sets the value for maximal quantity of dopped items
	 * @param maxdrop : int designating the quantity of dropped items
	 */
	public void setMaxDrop(int maxdrop)
	{
		_maxDrop = maxdrop;
	}
	
	/**
	 * Sets the chance of having the item for a drop
	 * @param chance : int designating the chance
	 */
	public void setChance(int chance)
	{
		_chance = chance;
	}
	
	/**
	 * Adds states of the dropped item
	 * @param list : String[]
	 */
	public void addStates(String[] list)
	{
		_stateID = list;
	}
	
	/**
	 * Returns the questID.
	 * @return String designating the ID of the quest
	 */
	public String getQuestID()
	{
		return _questID;
	}
	
	/**
	 * Sets the questID
	 * @param questID String designating the questID to set.
	 */
	public void setQuestID(String questID)
	{
		_questID = questID;
	}
	
	/**
	 * Returns if the dropped item is requested for a quest
	 * @return boolean
	 */
	public boolean isQuestDrop()
	{
		return _questID != null && _stateID != null;
	}
	
	/**
	 * Returns a report of the object
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "ItemID: " + _itemId + " Min: " + _minDrop + " Max: " + _maxDrop + " Chance: " + (_chance / 10000.0) + "%";
	}
	
	/**
	 * Returns if parameter "o" is a L2DropData and has the same itemID that the current object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof DropData)
		{
			DropData drop = (DropData) o;
			return drop.getItemId() == getItemId();
		}
		return false;
	}
}