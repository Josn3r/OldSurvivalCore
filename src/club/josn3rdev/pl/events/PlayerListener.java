package club.josn3rdev.pl.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.database.Stats;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoinEvent (PlayerJoinEvent e) {
		Player p = e.getPlayer();		
		if (PlayerManager.get().getPlayer(p.getUniqueId()) == null) {
			PlayerManager.get().createPlayer(p);
		}
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
        Stats.get().loadStats(p);
        
        sp.setBossbar("", 0.0D);
        sp.updateBossbar();
	}
		
	@EventHandler
	public void onInteractEvent (PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (Tools.get().existName(p, Tools.get().Text("&6Corner Wand"))) {
			// CLICK IZQUIERDO
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				e.setCancelled(true);
				Location loc1 = e.getClickedBlock().getLocation();
				sp.setCorner1(loc1);			    
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &fSe ha guardado la localidad del corner #1."));
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &e" + Tools.get().setLocToStringBlock(loc1)));
			}
			// CLICK DERECHO
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				Location loc2 = e.getClickedBlock().getLocation();
				sp.setCorner2(loc2);			    
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &fSe ha guardado la localidad del corner #2."));
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &e" + Tools.get().setLocToStringBlock(loc2)));
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawnEvent (PlayerRespawnEvent e) {
		MSRP.get().getServer().getScheduler().runTaskLaterAsynchronously(MSRP.get(), new Runnable() {
			@Override
			public void run() {
				Player p = e.getPlayer();
				SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
				
				if (sp.getHospital() == 2) {
					Vault.removeMoney(p, 150.0);
					p.sendMessage(" ");
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fMoriste y fuiste rehabilitado por el &eHospital&f, Eres socio por lo que solo pagaste &a$150 &f de gastos médicos."));
				} else if (sp.getHospital() == 1) {
					Vault.removeMoney(p, 500.0);
					p.sendMessage(" ");
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fMoriste y fuiste rehabilitado por el &eHospital&f, Tienes seguro por lo que solo pagaste &a$500 &f de gastos médicos."));
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&f¿Quieres ahorrar mucho más los gastos del Hospital? Usa el comando &e/hospital &fy adquiere la membresía Socio!"));
				} else {
					Vault.removeMoney(p, 850.0);
					p.sendMessage(" ");
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fMoriste y fuiste rehabilitado por el &eHospital&f, pagaste &a$850 &fpor concepto de gastos médicos."));
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&f¿Quieres pagar menos al Hospital? Usa el comando &e/hospital &fy mira las opciones!"));
				}
			}
		}, 1);
	}

}
