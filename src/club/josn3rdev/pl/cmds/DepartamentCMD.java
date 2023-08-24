package club.josn3rdev.pl.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.departaments.Departament;
import club.josn3rdev.pl.utils.Tools;

public class DepartamentCMD implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("departament")) {
			if (!p.hasPermission("msrp.admin")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes permiso para usar ese comando."));
				return true;
			}
			
			if (args.length < 1) {
				p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
				p.sendMessage(Tools.get().Text("&f/departament create building <edifName>"));
				p.sendMessage(Tools.get().Text("&f/departament editRegion building <edifName>"));
				p.sendMessage(Tools.get().Text("&f/departament delete building <edifName>"));
				p.sendMessage(Tools.get().Text("&f/departament hologram building [set/remove/move] <edifName>"));
				p.sendMessage(Tools.get().Text(" "));
				p.sendMessage(Tools.get().Text("&f/departament create rental <edifName> <rentalCode>"));
				p.sendMessage(Tools.get().Text("&f/departament editRegion rental <edifName> <rentalCode>"));
				p.sendMessage(Tools.get().Text("&f/departament delete rental <edifName> <rentalCode>"));
				p.sendMessage(Tools.get().Text("&f/departament hologram rental [set/remove/move] <edifName> <rentalCode>"));
				return true;
			}
			
			/*
			 * CREATE
			 */
			
			if (args[0].equalsIgnoreCase("debug")) {
				for (String depa : Departament.get().getEdificios()) {
					Departament.get().deleteEdificioHologram(depa);
					if (!Departament.get().getRentals().get(depa).isEmpty()) {
						for (String retals : Departament.get().getRentals().get(depa)) {
							Departament.get().deleteRentalHologram(depa, retals);
						}
					}
				}	
				
				for (String depa : Departament.get().getEdificios()) {
					Location locDepaHolo = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + depa + ".location.hologramLoc"));
					Departament.get().createEdificioHologram(locDepaHolo, depa);
					if (!Departament.get().getRentals().get(depa).isEmpty()) {
						for (String rentals : Departament.get().getRentals().get(depa)) {
							Location locRentHolo = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + depa + ".rentals." + rentals + ".location.hologramLoc"));
							Departament.get().createRentalHologram(locRentHolo, depa, rentals);
						}
					}
				}
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&eDepartament Debug Holograms in 2ms"));
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
					p.sendMessage(Tools.get().Text("&f/departament create building <edifName>"));
					p.sendMessage(Tools.get().Text("&f/departament create rental <edifName> <rentalCode>"));
					return true;
				}
				
				if (args[1].equalsIgnoreCase("building")) {
					if (args.length < 3) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament create building <edifName>"));
						return true;
					}
					
					String edifName = args[2];
					Departament.get().createEdificio(p, edifName);					
					return true;
				}
				
				if (args[1].equalsIgnoreCase("rental")) {
					if (args.length < 4) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament create rental <edifName> <rentalCode>"));
						return true;
					}
						
					String edifName = args[2];
					String rentalCode = args[3];
					Departament.get().createRental(p, rentalCode, edifName);
					return true;
				}
			}
			
			/*
			 * EDIT REGION
			 */
			
			if (args[0].equalsIgnoreCase("editRegion")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
					p.sendMessage(Tools.get().Text("&f/departament editRegion building <edifName>"));
					p.sendMessage(Tools.get().Text("&f/departament editRegion rental <edifName> <rentalCode>"));
					return true;
				}
				
				if (args[1].equalsIgnoreCase("building")) {
					if (args.length < 3) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament editRegion building <edifName>"));
						return true;
					}
					
					String edifName = args[2];
					Departament.get().editRegionEdificio(p, edifName);				
					return true;
				}
				
				if (args[1].equalsIgnoreCase("rental")) {
					if (args.length < 4) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament editRegion rental <edifName> <rentalCode>"));
						return true;
					}
						
					String edifName = args[2];
					String rentalCode = args[3];
					Departament.get().editRegionRental(p, rentalCode, edifName);
					return true;
				}
			}
			
			/*
			 * HOLOGRAM
			 */
			
			if (args[0].equalsIgnoreCase("hologram")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
					p.sendMessage(Tools.get().Text("&f/departament hologram building [set/remove/move] <edifName>"));
					p.sendMessage(Tools.get().Text("&f/departament hologram rental [set/remove/move] <edifName> <rentalCode>"));
					return true;
				}
				
				if (args[1].equalsIgnoreCase("building")) {
					if (args.length < 4) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament hologram building [set/remove/move] <edifName>"));
						return true;
					}
					
					if (args[2].equalsIgnoreCase("set")) {
						if (args.length < 4) {
							p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
							p.sendMessage(Tools.get().Text("&f/departament hologram building set <edifName>"));
							return true;
						}
						
						String edifName = args[3];	
						Departament.get().setEdificioHologram(p, edifName);
						return true;
					}
					
					if (args[2].equalsIgnoreCase("move")) {
						if (args.length < 4) {
							p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
							p.sendMessage(Tools.get().Text("&f/departament hologram building move <edifName>"));
							return true;
						}
						
						String edifName = args[3];	
						Departament.get().moveEdificioHologram(p, edifName);
						return true;
					}
					
					return true;
				}
				
				if (args[1].equalsIgnoreCase("rental")) {
					if (args.length < 5) {
						p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
						p.sendMessage(Tools.get().Text("&f/departament hologram rental [set/remove/move] <edifName> <rentalCode>"));
						return true;
					}
						
					if (args[2].equalsIgnoreCase("set")) {
						if (args.length < 5) {
							p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
							p.sendMessage(Tools.get().Text("&f/departament hologram rental [set/remove/move] <edifName> <rentalCode>"));
							return true;
						}
						
						String edifName = args[3];	
						String rentalCode = args[4];	
						Departament.get().setRentalHologram(p, edifName, rentalCode);
						return true;
					}
					
					if (args[2].equalsIgnoreCase("move")) {
						if (args.length < 5) {
							p.sendMessage(Tools.get().Text("&6&lDEPARTAMENTS - COMMANDS"));
							p.sendMessage(Tools.get().Text("&f/departament hologram rental move <edifName> <rentalCode>"));
							return true;
						}
						
						String edifName = args[3];	
						String rentalCode = args[4];
						Departament.get().moveRentalHologram(p, edifName, rentalCode);
						return true;
					}
					return true;
				}
			}
			return true;
		}
		
		return false;
	}

}
