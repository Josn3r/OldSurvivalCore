package club.josn3rdev.pl.marry.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.marry.MarryManager;
import club.josn3rdev.pl.utils.Tools;

public class PVPListener implements Listener {
	
	@EventHandler
	public void onDamageAdult(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) 
			return;
		if (!(e.getDamager() instanceof Player))
			return; 
		if (!(e.getEntity() instanceof Player))
			return; 
		UUID uuid = MarryManager.get().getRelationship(e.getDamager().getUniqueId());
		NamespacedKey pvpKey = new NamespacedKey(MSRP.get(), "pvpToggle");
		if (uuid == null)
			return; 
		if (!uuid.equals(e.getEntity().getUniqueId()))
			return; 
		if (Bukkit.getPlayer(uuid).isSneaking()) {
			e.setCancelled(true);
			return;
		} 
		if (Bukkit.getPlayer(uuid).getPersistentDataContainer().has(pvpKey, PersistentDataType.STRING))
			return; 
		if (!((Player)e.getDamager()).isSneaking())
			Tools.get().sendActionBar((Player)e.getDamager(), "&c¡Tu compañero tiene su PvP desactivado!");
		e.setCancelled(true);
	}
	
}
