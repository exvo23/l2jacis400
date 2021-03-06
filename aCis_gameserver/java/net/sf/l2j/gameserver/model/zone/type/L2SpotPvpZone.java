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
 * this program. If not, see <[url="http://www.gnu.org/licenses/>."]http://www.gnu.org/licenses/>.[/url]
 */
package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class L2SpotPvpZone extends L2ZoneType
{
	private boolean _checkClasses;
	private static List<String> _classes = new ArrayList<>();
	private static boolean _isReviveEnabled;
	
	public L2SpotPvpZone(int id)
	{
		super(id);

		_checkClasses = false;
		_isReviveEnabled = false;
	}
	
	L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("checkClasses"))
			_checkClasses = Boolean.parseBoolean(value);
		else if (name.equals("Classes"))
		{
			String[] propertySplit = value.split(",");
			for (String i : propertySplit)
				_classes.add(i);
		}
		else if (name.equals("isReviveEnabled"))
			_isReviveEnabled = Boolean.parseBoolean(value);
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.SPOT_PVP_ZONE, true);
		
		if (character instanceof Player)
		{
			final Player activeChar = (Player) character;

			if (_checkClasses)
			{
				if (_classes != null && _classes.contains(""+ activeChar.getClassId().getId()))
				{
					activeChar.teleToLocation(83597, 147888, -3405, 0);
					activeChar.sendMessage("Your class is not allowed in this zone.");
					return;
				}
			}
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.SPOT_PVP_ZONE, false);
	}

	@Override
	public void onDieInside(L2Character character)
	{
		
	}

	@Override
	public void onReviveInside(L2Character character)
	{

	}
	
	public static boolean isReviveEnabled()
	{
		return _isReviveEnabled;
	}
}