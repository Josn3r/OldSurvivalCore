package club.josn3rdev.pl.cmds;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.departaments.Departament;
import club.josn3rdev.pl.menu.hospital.HospitalMenu;
import club.josn3rdev.pl.utils.Tools;

public class ComprarCMD implements CommandExecutor {
	
	private HashMap<Player, Integer> countdown = new HashMap<Player, Integer>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("comprar")) {
						
			if (args.length < 1) {
				p.sendMessage(Tools.get().Text("&6&lCOMPRAR - COMMANDS"));
				p.sendMessage(Tools.get().Text("&f/comprar <edificio/negocio>"));
				return true;
			}
			
			/*
			 * CREATE
			 */
			
			if (args[0].equalsIgnoreCase("edificio")) {
				Departament.get().buyBuilding(p);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("negocio")) {
				
				return true;
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("vender")) {
			
			if (args.length < 1) {
				p.sendMessage(Tools.get().Text("&6&lVENDER - COMMANDS"));
				p.sendMessage(Tools.get().Text("&f/vender <edificio/negocio/sc> <jugador> <precio>"));
				return true;
			}
						
			if (args[0].equalsIgnoreCase("edificio")) {
				if (args.length < 3) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&eUso correcto: &f/vender edificio <jugador> <precio>"));
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&eEjemplo del comando: &f/vender edificio Kleit01 20500499.50"));
					return true;
				}
				
				String jugador = args[1];
				String value = args[2];
				
				Player target = Bukkit.getPlayer(jugador);
				
				if (target == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + jugador + " &cno está conectado."));
					return true;
				}
				
				if (!Tools.get().isDouble(value)) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl valor/precio debe estar en formato de números &7(1234.56)"));
					return true;
				}
				
				Double precio = Double.valueOf(value);
								
				if (countdown.containsKey(target)) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + jugador + " &cya tiene una oferta pendiente por aceptar o rechazar."));
					return true;
				}
				
				target.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fRecibiste una oferta de &e" + p.getName() + "&f."));
				target.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fTipo de venta: &eEdificio"));
				target.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fPrecio: &6&l$&e" + Tools.get().formatMoney(precio)));
				target.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fUsa el comando &e/aceptar &fpara completar la compra/venta."));
				Tools.get().playSound(target, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				
				countdown.put(target, 15);
				new BukkitRunnable() {
					Player juga = target;
					@Override
					public void run() {
						if (!countdown.containsKey(juga)) {
							cancel();
							return;
						}
						if (countdown.get(juga).intValue() > 0) {
							countdown.put(juga, countdown.get(juga).intValue()-1);
						} else {
							cancel();
							target.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fVenció la oferta que te hizo &e" + p.getName() + "&f."));
							Tools.get().playSound(target, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
							countdown.remove(juga);
						}
					}
				}.runTaskTimerAsynchronously(MSRP.get(), 0, 20);
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("negocio")) {
				
				return true;
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("aceptar")) {
			
			if (args.length < 1) {
				p.sendMessage(Tools.get().Text("&6&lCOMPRAR - COMMANDS"));
				return true;
			}
						
			if (args[0].equalsIgnoreCase("negocio")) {
				
				return true;
			}
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("alquilar")) {
			Departament.get().startAndPayAlquiler(p);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("opciones")) {
			Departament.get().checkPosition(p);
			return true;
		}
	
		if (cmd.getName().equalsIgnoreCase("hospital")) {
			new HospitalMenu(p).open(p);
			return true;
		}
		
		return false;
	}

}
