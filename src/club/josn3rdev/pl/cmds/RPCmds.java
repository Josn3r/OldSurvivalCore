package club.josn3rdev.pl.cmds;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.jobs.Jobs;
import club.josn3rdev.pl.menu.jobs.garbage.GarbageCreator;
import club.josn3rdev.pl.menu.jobs.garbage.GarbageNewRoute;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class RPCmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (cmd.getName().equalsIgnoreCase("hora")) {
			p.sendMessage(Tools.get().Text("&6&lM&e&lS &7» &fLa fecha del servidor es: &e" + getFecha() + "&f- Hora: &e" + getHour()));
			Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("minespazioroleplay")) {
			
			if (!p.hasPermission("msrp.admin")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes permiso para usar ese comando."));
				return true;
			}
			
			if (args.length < 1) {
				Tools.get().sendCenteredMessage(p, Tools.get().Text("&7-= &6&lMS-RP COMANDOS &7=-"));
				p.sendMessage(" ");
				p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob"));
				p.sendMessage(Tools.get().Text(" "));
				p.sendMessage(Tools.get().Text(" "));
				p.sendMessage(Tools.get().Text(" "));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("testing")) {
				p.getInventory().setItem(0, ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&6Proteccion: &fx10", ""));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("wand")) {
				p.getInventory().setItem(0, ItemBuilder.crearItem(Material.GOLDEN_AXE, 1, "&6Corner Wand", "&7- Click izquierdo = Punto 1", "&7- Click derecho = Punto 2"));
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("configJob")) {
				if (args.length < 2) {
					Tools.get().sendCenteredMessage(p, Tools.get().Text("&7-= &6&lMS-RP - CONFIG JOB &7=-"));
					p.sendMessage(" ");
					p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob wand"));
					p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob setCorners &7<Job>"));
					p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob config &7<Job>"));
					p.sendMessage(Tools.get().Text(" "));
					p.sendMessage(Tools.get().Text(" "));
					p.sendMessage(Tools.get().Text(" "));
					return true;
				}
				
				if (args[1].equalsIgnoreCase("setcorners")) {
					
					if (args.length < 3) {
						p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob setCorners &7<Job>"));
						return true;
					}
					
					Jobs job = Jobs.valueOf(args[2].toUpperCase());
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
					
					if (sp.getCorner1() == null && sp.getCorner2() == null) {
						p.sendMessage(Tools.get().Text("&c&lERROR » &fDebes seleccionar los dos corners primero!"));
						return true;
					}
					
					String loc1 = Tools.get().setLocToStringBlock(sp.getCorner1());			
					String loc2 = Tools.get().setLocToStringBlock(sp.getCorner2());
					
					if (job == Jobs.WOODCUTTER) {
						MSRP.talador.set("config.location-job.corner1", loc1);
						MSRP.talador.set("config.location-job.corner2", loc2);
					}
					if (job == Jobs.MINER) {
						MSRP.miner.set("config.location-job.corner1", loc1);
						MSRP.miner.set("config.location-job.corner2", loc2);
					}
					if (job == Jobs.HARVESTER) {
						MSRP.harvester.set("config.location-job.corner1", loc1);
						MSRP.harvester.set("config.location-job.corner2", loc2);
					}
					if (job == Jobs.GARBAGE) {
						MSRP.garbage.set("config.location-job.corner1", loc1);
						MSRP.garbage.set("config.location-job.corner2", loc2);
					}
					
					p.sendMessage(Tools.get().Text("&bSe ha guardado correctamente los corners del trabajo &f" + args[2]));
				    
					sp.setCorner1(null);
					sp.setCorner2(null);
					
					return true;
				}
				
				if (args[1].equalsIgnoreCase("config")) {

					if (args.length < 3) {
						p.sendMessage(Tools.get().Text("&71.- &b/msrp configJob config &7<Job>"));
						return true;
					}
					
					Jobs job = Jobs.valueOf(args[2].toUpperCase());
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
						p.getInventory().setItem(0, ItemBuilder.crearItem(Material.OAK_LOG, 1, "&6Woodcutter Logs", "&7Coloca este item y se registrará el bloque", "&7en el trabajo de Talador."));
					}
					if (job == Jobs.MINER) {
						p.getInventory().setItem(0, ItemBuilder.crearItem(Material.BLAZE_ROD, 1, "&6Miner Selector", "&7- Left Click to Added Block", "&7- Right Click to Remove Block"));
					}
					if (job == Jobs.HARVESTER) {
						p.getInventory().setItem(0, ItemBuilder.crearItem(Material.BLAZE_ROD, 1, "&6Harvest Selector", "&7- Left Click to Added Block", "&7- Right Click to Remove Block"));
					}
					if (job == Jobs.GARBAGE) {
						
						if (!sp.getGarbagePoints().isEmpty()) {
							for (String str : sp.getGarbagePoints()) {
								Location loc = Tools.get().setStringToLocBlock(str);
								Block b = loc.getBlock();
								if (b.getType() == Material.BEACON) {
									b.setType(Material.AIR);
								}
							}
							new GarbageNewRoute(p).open(p);
							return true;
						}
						
						new GarbageCreator(p).open(p);
					}
					
					return true;
				}
				
				return true;
			}
						
		}
		
		return true;
	}

	
	public String getFecha() {		
		Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");    	
		return format.format(now);
	}
	
	public String getHour() {		
		Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");    	
		return format.format(now);
	}
	
	
}
