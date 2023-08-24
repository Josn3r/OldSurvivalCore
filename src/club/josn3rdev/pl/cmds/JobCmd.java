package club.josn3rdev.pl.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.jobs.GarbageCollector;
import club.josn3rdev.pl.jobs.Harvester;
import club.josn3rdev.pl.jobs.Jobs;
import club.josn3rdev.pl.jobs.Miner;
import club.josn3rdev.pl.jobs.Woodcutter;
import club.josn3rdev.pl.menu.jobs.GarbageMenu;
import club.josn3rdev.pl.menu.jobs.HarvesterMenu;
import club.josn3rdev.pl.menu.jobs.MinerMenu;
import club.josn3rdev.pl.menu.jobs.WCMenu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class JobCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;	
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (cmd.getName().equalsIgnoreCase("jobs")) {
			
			if (!p.hasPermission("msrp.jobs.use")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes permiso para usar ese comando."));
				return true;
			}
			
			if (args.length < 1) {
				Tools.get().sendCenteredMessage(p, Tools.get().Text("&7-= &6&lMS-RP COMANDOS &7=-"));
				p.sendMessage(Tools.get().Text("&71.- &b/jobs open <job_name>"));
				p.sendMessage(Tools.get().Text("&72.- &b/jobs list"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("open")) {
				
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&71.- &b/jobs open <job_name>"));
					return true;
				}
				
				Jobs job = Jobs.valueOf(args[1].toUpperCase());
				Boolean correctJob = false;
				
				for (Jobs exists : Jobs.values()) {
					if (job == exists) {
						correctJob = true;
					}
				}
				
				if (!correctJob) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse trabajo no existe..."));
					return true;
				}
				
				if (job == Jobs.WOODCUTTER) {
					if (sp.getJob1() == Jobs.WOODCUTTER || sp.getJob2() == Jobs.WOODCUTTER) {
						if (Woodcutter.get().getMission(p) != null) {
							Woodcutter.get().finishMission(p);
							return true;
						}
					}
					new WCMenu(p).open(p);
				}	
				if (job == Jobs.MINER) {
					if (sp.getJob1() == Jobs.MINER || sp.getJob2() == Jobs.MINER) {
						if (Miner.get().getMission(p) != null) {
							Miner.get().finishMission(p);
							return true;
						}
					}
					new MinerMenu(p).open(p);
				}	
				if (job == Jobs.HARVESTER) {
					if (sp.getJob1() == Jobs.HARVESTER || sp.getJob2() == Jobs.HARVESTER) {
						if (Harvester.get().getMission(p) != null) {
							Harvester.get().finishMission(p);
							return true;
						}
					}
					new HarvesterMenu(p).open(p);
				}
				if (job == Jobs.GARBAGE) {
					if (sp.getJob1() == Jobs.GARBAGE || sp.getJob2() == Jobs.GARBAGE) {
						if (GarbageCollector.get().getMission(p) != null) {
							GarbageCollector.get().payPerJobPlayer(p);
							return true;
						}
					}
					new GarbageMenu(p).open(p);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				Tools.get().sendCenteredMessage(p, Tools.get().Text("&7-= &6&lMS-RP JOBS LIST &7=-"));
				for (Jobs job : Jobs.values()) {
					p.sendMessage(Tools.get().Text("&71.- &b" + job.name().toUpperCase()));
				}
				return true;
			}
						
		}
		
		return true;
	}

}
