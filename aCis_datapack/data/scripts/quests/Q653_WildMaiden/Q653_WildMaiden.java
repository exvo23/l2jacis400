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
package quests.Q653_WildMaiden;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.util.Rnd;

public class Q653_WildMaiden extends Quest
{
	private static final String qn = "Q653_WildMaiden";
	
	// NPCs
	private static final int SUKI = 32013;
	private static final int GALIBREDO = 30181;
	
	// Item
	private static final int SOE = 736;
	
	// Table of possible spawns
	private static final int[][] spawns =
	{
		{
			66578,
			72351,
			-3731,
			0
		},
		{
			77189,
			73610,
			-3708,
			2555
		},
		{
			71809,
			67377,
			-3675,
			29130
		},
		{
			69166,
			88825,
			-3447,
			43886
		}
	};
	
	// Current position
	private int _currentPosition = 0;
	
	public Q653_WildMaiden(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(SUKI);
		addTalkId(SUKI, GALIBREDO);
		
		addSpawn(SUKI, 66578, 72351, -3731, 0, false, 0, false);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32013-03.htm"))
		{
			if (st.hasQuestItems(SOE))
			{
				st.set("cond", "1");
				st.setState(STATE_STARTED);
				st.takeItems(SOE, 1);
				st.playSound(QuestState.SOUND_ACCEPT);
				
				npc.broadcastPacket(new MagicSkillUse(npc, npc, 2013, 1, 3500, 0));
				startQuestTimer("apparition_npc", 4000, npc, player, false);
			}
			else
			{
				htmltext = "32013-03a.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("apparition_npc"))
		{
			int chance = Rnd.get(4);
			
			// Loop to avoid to spawn to the same place.
			while (chance == _currentPosition)
				chance = Rnd.get(4);
			
			// Register new position.
			_currentPosition = chance;
			
			npc.deleteMe();
			addSpawn(SUKI, spawns[chance][0], spawns[chance][1], spawns[chance][2], spawns[chance][3], false, 0, false);
			return null;
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
				if (player.getLevel() >= 36)
					htmltext = "32013-02.htm";
				else
				{
					htmltext = "32013-01.htm";
					st.exitQuest(true);
				}
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case GALIBREDO:
						htmltext = "30181-01.htm";
						st.rewardItems(57, 2883);
						st.playSound(QuestState.SOUND_FINISH);
						st.exitQuest(true);
						break;
					
					case SUKI:
						htmltext = "32013-04a.htm";
						break;
				}
				break;
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q653_WildMaiden(653, qn, "Wild Maiden");
	}
}