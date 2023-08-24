package club.josn3rdev.pl.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.menu.protections.ProtectionMenu;
import club.josn3rdev.pl.menu.protections.ProtectionSelection;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.protections.ProtectionManager;
import club.josn3rdev.pl.protections.Protections;
import club.josn3rdev.pl.utils.Tools;

public class ProtectionCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;	
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (cmd.getName().equalsIgnoreCase("protection")) {			
			if (!p.hasPermission("msrp.protection.use")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes permiso para usar ese comando."));
				return true;
			}
			
			if (args.length < 1) {
				if (!sp.getProtectionOwner().isEmpty()) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fAbriendo el menú de protecciones..."));
					new ProtectionSelection(p).open(p);
				} else {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo eres dueño de ninguna protección!"));	
				}
			}
			
			if (args[0].equalsIgnoreCase("buy")) {
				new ProtectionMenu(p).open(p);
				return true;
			}
			

			if (args[0].equalsIgnoreCase("list")) {
				p.sendMessage(Tools.get().Text("&6&lPROTECTIONS LIST:"));
				for (Protections protes : ProtectionManager.get().ProtectionsSet()) {
					p.sendMessage(Tools.get().Text("&7-. &fProtection ID: &e" + protes.getUUID() + " &7- &fOwner: &e" + protes.getOwner().getName()));
				}
				return true;
			}
				
									
		}
		
		return true;
	}

}
