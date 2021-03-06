/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q634_InSearchOfFragmentsOfDimension;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;

public class Q634_InSearchOfFragmentsOfDimension extends Quest
{
	private static final String qn = "Q634_InSearchOfFragmentsOfDimension";
	
	// Items
	private static final int DIMENSION_FRAGMENT = 7079;
	
	public Q634_InSearchOfFragmentsOfDimension(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		// Dimensional Gate Keepers.
		for (int i = 31494; i < 31508; i++)
		{
			addStartNpc(i);
			addTalkId(i);
		}
		
		// All mobs.
		for (int i = 21208; i < 21256; i++)
			addKillId(i);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("02.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("05.htm"))
		{
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() >= 20)
					htmltext = "01.htm";
				else
				{
					htmltext = "01a.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				htmltext = "03.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player player, boolean isPet)
	{
		Player partyMember = getRandomPartyMemberState(player, npc, STATE_STARTED);
		if (partyMember == null)
			return null;
		
		QuestState st = partyMember.getQuestState(qn);
		
		st.dropItems(DIMENSION_FRAGMENT, (int) (npc.getLevel() * 0.15 + 2.6), -1, 80000);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q634_InSearchOfFragmentsOfDimension(634, qn, "In Search of Fragments of Dimension");
	}
}