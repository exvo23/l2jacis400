package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import net.sf.l2j.ThreadPool;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.events.partyfarm.PartyFarm;


/**
 * Juvenil Walker
 */

public class AdminPartyFarm implements IAdminCommandHandler
{
	public static L2Spawn _monster;
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ptfarm"

	};
	
	protected static final Logger _log = Logger.getLogger(AdminPartyFarm.class.getName());
	public static boolean _bestfarm_manual = true;
	public static boolean _arena_manual = false;

	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		

		
		if (command.startsWith("admin_ptfarm"))
			if (PartyFarm._started)
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Party Farm]: Event Finished.");
				_log.info("----------------------------------------------------------------------------");
				PartyFarm._aborted = true;
				unSpawnMonsters();
				finishEventPartyFarm();

				activeChar.sendMessage("SYS: Voce Finalizou o Party Farm Manualmente..");
			}
			else
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Party Farm]: Event Started.");
				_log.info("----------------------------------------------------------------------------");
				initEventPartyFarm();
				_bestfarm_manual = true;
				activeChar.sendMessage("SYS: Voce ativou o Best Farm Manualmente..");
			}
		return true;
	}



	/**
	 * 
	 */
	public static List<L2Spawn> monstersArea = new CopyOnWriteArrayList<>();



	protected static void unSpawnMonsters()
	{
		for (L2Spawn s : monstersArea)
		{
			if (s == null)
			{
				monstersArea.remove(s);
				continue;
			}
			
			s.getLastSpawn().deleteMe();
			s.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(s, true);
			monstersArea.remove(s);
		}
	}



	private static void initEventPartyFarm()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{

				PartyFarm.bossSpawnMonster();
			}
		}, 1L);
	}

	private static void finishEventPartyFarm()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{

				PartyFarm.Finish_Event();

			}
		}, 1L);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
