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
package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.skills.Env;

/**
 * @author Steuf
 */
public class ConditionWithSkill extends Condition
{
	private final boolean _skill;
	
	public ConditionWithSkill(boolean skill)
	{
		_skill = skill;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!_skill && env.getSkill() != null)
			return false;
		
		return true;
	}
}