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
package quests.Q350_EnhanceYourWeapon;

import net.sf.l2j.commons.utils.ArraysUtil;
import net.sf.l2j.gameserver.datatables.custom.SoulCrystalData;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Attackable.AbsorbInfo;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.soulcrystal.LevelingInfo;
import net.sf.l2j.gameserver.model.soulcrystal.SoulCrystal;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.util.Rnd;

public class Q350_EnhanceYourWeapon extends Quest
{
	private static final String qn = "Q350_EnhanceYourWeapon";
	
	public Q350_EnhanceYourWeapon()
	{
		super(350, qn, "Enhance Your Weapon");
		
		addStartNpc(30115, 30194, 30856);
		addTalkId(30115, 30194, 30856);
		
		for (int npcId : SoulCrystalData.getInstance().getLevelingInfos().keySet())
			addKillId(npcId);
		
		for (int crystalId : SoulCrystalData.getInstance().getSoulCrystals().keySet())
			addItemUse(crystalId);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		// Start the quest.
		if (event.endsWith("-04.htm"))
		{
			st.setState(Quest.STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		// Give Red Soul Crystal.
		else if (event.endsWith("-09.htm"))
		{
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(4629, 1);
		}
		// Give Green Soul Crystal.
		else if (event.endsWith("-10.htm"))
		{
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(4640, 1);
		}
		// Give Blue Soul Crystal.
		else if (event.endsWith("-11.htm"))
		{
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(4651, 1);
		}
		// Terminate the quest.
		else if (event.endsWith("-exit.htm"))
			st.exitQuest(true);
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				if (player.getLevel() < 40)
					htmltext = npc.getNpcId() + "-lvl.htm";
				else
					htmltext = npc.getNpcId() + "-01.htm";
				break;
			
			case STATE_STARTED:
				// Check inventory for soul crystals.
				for (ItemInstance item : player.getInventory().getItems())
				{
					// Crystal found, show "how to" html.
					if (SoulCrystalData.getInstance().getSoulCrystals().get(item.getItemId()) != null)
						return npc.getNpcId() + "-03.htm";
				}
				// No crystal found, offer a new crystal.
				htmltext = npc.getNpcId() + "-21.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onItemUse(ItemInstance item, Player user, L2Object target)
	{
		// Caster is dead.
		if (user.isDead())
			return null;
		
		// No target, or target isn't an L2Attackable.
		if (target == null || !(target instanceof L2Attackable))
			return null;
		
		final L2Attackable mob = ((L2Attackable) target);
		
		// Mob is dead or not registered in _npcInfos.
		if (mob.isDead() || !SoulCrystalData.getInstance().getLevelingInfos().containsKey(mob.getNpcId()))
			return null;
		
		// Add user to mob's absorber list.
		mob.addAbsorber(user, item);
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isPet)
	{
		// Retrieve individual mob informations.
		final LevelingInfo npcInfo = SoulCrystalData.getInstance().getLevelingInfos().get(npc.getNpcId());
		if (npcInfo == null)
			return null;
		
		final int chance = Rnd.get(1000);
		
		// Handle npc leveling info type.
		switch (npcInfo.getAbsorbCrystalType())
		{
			case FULL_PARTY:
				final L2Attackable mob = (L2Attackable) npc;
				
				for (Player player : getPartyMembersState(killer, npc, Quest.STATE_STARTED))
					tryToStageCrystal(player, mob, npcInfo, chance);
				break;
			
			case PARTY_ONE_RANDOM:
				final Player player = getRandomPartyMemberState(killer, npc, Quest.STATE_STARTED);
				if (player != null)
					tryToStageCrystal(player, (L2Attackable) npc, npcInfo, chance);
				break;
			
			case LAST_HIT:
				if (checkPlayerState(killer, npc, Quest.STATE_STARTED) != null)
					tryToStageCrystal(killer, (L2Attackable) npc, npcInfo, chance);
				break;
		}
		
		return null;
	}
	
	/**
	 * Define the Soul Crystal and try to stage it. Checks for quest enabled, crystal(s) in inventory, required usage of crystal, mob's ability to level crystal and mob vs player level gap.
	 * @param player : The player to make checks on.
	 * @param mob : The mob to make checks on.
	 * @param npcInfo : The mob's leveling informations.
	 * @param chance : Input variable used to determine keep/stage/break of the crystal.
	 * @return Returns true only, when crystal is staged or broken (aka any type of crystal change is made), else returns false.
	 */
	private void tryToStageCrystal(Player player, L2Attackable mob, LevelingInfo npcInfo, int chance)
	{
		SoulCrystal crystalData = null;
		ItemInstance crystalItem = null;
		
		// Iterate through player's inventory to find crystal(s).
		for (ItemInstance item : player.getInventory().getItems())
		{
			SoulCrystal data = SoulCrystalData.getInstance().getSoulCrystals().get(item.getItemId());
			if (data == null)
				continue;
			
			// More crystals found.
			if (crystalData != null)
			{
				// Leveling requires soul crystal being used?
				if (npcInfo.isSkillRequired())
				{
					// Absorb list contains killer and his AbsorbInfo is registered.
					final AbsorbInfo ai = mob.getAbsorbInfo(player.getObjectId());
					if (ai != null && ai.isRegistered())
						player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
				}
				else
					player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
				
				return;
			}
			
			crystalData = data;
			crystalItem = item;
		}
		
		// No crystal found, return without any notification.
		if (crystalData == null || crystalItem == null)
			return;
		
		// Leveling requires soul crystal being used?
		if (npcInfo.isSkillRequired())
		{
			// Absorb list doesn't contain killer or his AbsorbInfo is not registered.
			final AbsorbInfo ai = mob.getAbsorbInfo(player.getObjectId());
			if (ai == null || !ai.isRegistered())
				return;
			
			// Check if Absorb list contains valid crystal and whether it was used properly.
			if (!ai.isValid(crystalItem.getObjectId()))
			{
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
				return;
			}
		}
		
		// Check, if npc stages this type of crystal.
		if (!ArraysUtil.contains(npcInfo.getLevelList(), crystalData.getLevel()))
		{
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		// Check level difference limitation, dark blue monsters does not stage.
		if (player.getLevel() - mob.getLevel() > 8)
		{
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		// Lucky, crystal successfully stages.
		if (chance < npcInfo.getChanceStage())
			exchangeCrystal(player, crystalData, true);
		// Bad luck, crystal accidentally breaks.
		else if (chance < (npcInfo.getChanceStage() + npcInfo.getChanceBreak()))
			exchangeCrystal(player, crystalData, false);
		// Bad luck, crystal doesn't stage.
		else
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED);
	}
	
	/**
	 * Remove the old crystal and add new one if stage, broken crystal if break. Send messages in both cases.
	 * @param player : The player to check on (inventory and send messages).
	 * @param sc : SoulCrystal of to take information form.
	 * @param stage : Switch to determine success or fail.
	 */
	private static void exchangeCrystal(Player player, SoulCrystal sc, boolean stage)
	{
		QuestState st = player.getQuestState(qn);
		
		st.takeItems(sc.getInitialItemId(), 1);
		if (stage)
		{
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_SUCCEEDED);
			st.giveItems(sc.getStagedItemId(), 1);
			st.playSound(QuestState.SOUND_ITEMGET);
		}
		else
		{
			int broken = sc.getBrokenItemId();
			if (broken != 0)
			{
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_BROKE);
				st.giveItems(broken, 1);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Q350_EnhanceYourWeapon();
	}
}