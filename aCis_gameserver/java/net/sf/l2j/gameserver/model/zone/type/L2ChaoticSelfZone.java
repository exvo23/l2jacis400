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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class L2ChaoticSelfZone extends L2SpawnZone
{
	public L2ChaoticSelfZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.FLAG_AREA_SELF, true);
		
		if (character instanceof Player)
		{
			final Player activeChar = (Player) character;
			
			activeChar.updatePvPStatus();
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.FLAG_AREA_SELF, false);
		
		if (character instanceof Player)
		{
			final Player activeChar = (Player) character;
			
			PvpFlagTaskManager.getInstance().add(activeChar, Config.PVP_NORMAL_TIME);
			
			// Set pvp flag
			if (activeChar.getPvpFlag() == 0)
				activeChar.updatePvPFlag(1);
		}
	}

	@Override
	public void onDieInside(L2Character character)
	{

	}

	@Override
	public void onReviveInside(L2Character character)
	{

	}
}