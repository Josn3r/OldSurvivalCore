package club.josn3rdev.pl.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.clanes.Clanes;
import club.josn3rdev.pl.clanes.ClanesManager;
import club.josn3rdev.pl.menu.clanes.ClanMenu;
import club.josn3rdev.pl.utils.Tools;

public class ClanCMD implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("clan")) {
						
			if (args.length < 1) {
				if (ClanesManager.get().playerHasClan(p)) {
					new ClanMenu(p, ClanesManager.get().getPlayerClan(p)).open(p);
					return true;
				}				
				
				p.sendMessage(Tools.get().Text("&6&lCLANS - COMMANDS"));
				p.sendMessage(Tools.get().Text("&f/clan crear <clanName> <clanTag>"));
				p.sendMessage(Tools.get().Text("&f/clan claim &7- &eClaimea una protección."));
				return true;
			}
			
			/*
			 * CREATE
			 */
			
			if (args[0].equalsIgnoreCase("crear")) {
				if (args.length < 3) {
					p.sendMessage(Tools.get().Text("&f/clan crear <clanName> <clanTag>"));
					p.sendMessage(Tools.get().Text("&eEl &6&lTAG &edebe ser de 3 letras!"));
					return true;
				}
				
				String clanName = args[1];
				String clanTag = args[2];
				
				if (clanTag.length() > 3) {
					p.sendMessage(Tools.get().Text("&eEl &6&lTAG &edebe ser de 3 letras!"));
					return true;
				}
				
				ClanesManager.get().createClan(p, clanName, clanTag);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("claim")) {
				
				if (ClanesManager.get().getPlayerClan(p) == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes ningún clan!"));
					return true;
				}
				
				Clanes clans = ClanesManager.get().getPlayerClan(p);
				
				if (!clans.getClanMembers().contains(p.getUniqueId().toString() + " : " + p.getName() + " : 1")) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo eres el lider del clan!"));
					return true;
				}
				
				if (ClanesManager.get().checkRadiusProtection(p.getLocation().getBlock())) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cHay una protección cerca, alejate más..."));
					return true;
				}
				
				/*if (clans.getClanProtection() != null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cYa haz claimeado una protección, no puedes ni tener más, ni moverla!"));
					return true;
				}*/
				
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&eSe ha claimeado correctamente la protección de tu clan!"));
				clans.setClanProtection(p.getLocation());				
			}
			
			if (args[0].equalsIgnoreCase("negocio")) {
				
				return true;
			}
			
			return true;
		}
		
		return false;
	}

}
