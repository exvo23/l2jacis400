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
package quests.Q650_ABrokenDream;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.util.Rnd;

public class Q650_ABrokenDream extends Quest
{
	private static final String qn = "Q650_ABrokenDream";
	
	// NPC
	private static final int GHOST = 32054;
	
	// Item
	private static final int DREAM_FRAGMENT = 8514;
	
	// Monsters
	private static final int CREWMAN = 22027;
	private static final int VAGABOND = 22028;
	
	public Q650_ABrokenDream(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		setItemsIds(DREAM_FRAGMENT);
		
		addStartNpc(GHOST);
		addTalkId(GHOST);
		addKillId(CREWMAN, VAGABOND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32054-01a.htm"))
		{
			st.set("cond", "1");
			st.setState(STATE_STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32054-03.htm"))
		{
			if (!st.hasQuestItems(DREAM_FRAGMENT))
				htmltext = "32054-04.htm";
		}
		else if (event.equalsIgnoreCase("32054-05.htm"))
		{
			st.exitQuest(true);
			st.playSound(QuestState.SOUND_GIVEUP);
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
				QuestState st2 = player.getQuestState("Q117_TheOceanOfDistantStars");
				if (st2 != null && st2.isCompleted() && player.getLevel() >= 39)
					htmltext = "32054-01.htm";
				else
				{
					htmltext = "32054-00.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				htmltext = "32054-02.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerState(player, npc, STATE_STARTED);
		if (st == null)
			return null;
		
		if (Rnd.get(100) < 25)
		{
			st.giveItems(DREAM_FRAGMENT, 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		new Q650_ABrokenDream(650, qn, "A Broken Dream");
	}
}