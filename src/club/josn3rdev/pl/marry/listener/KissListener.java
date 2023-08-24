package club.josn3rdev.pl.marry.listener;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.marry.MarryManager;
import club.josn3rdev.pl.utils.Tools;

public class KissListener implements Listener {
    
	@EventHandler
	public void onKiss(PlayerAnimationEvent e) {
		if (!(e.getPlayer() instanceof Player))
			return; 
		if (!e.getAnimationType().equals(PlayerAnimationType.ARM_SWING))
			return; 
		Player damager = e.getPlayer();
		UUID damagerUUID = damager.getUniqueId();
		UUID victimUUID = returnVictim(damagerUUID);
		NamespacedKey pvpKey = new NamespacedKey(MSRP.get(), "pvpToggle");
		if (victimUUID == null)
			return; 
		Player victim = Bukkit.getPlayer(victimUUID);
		if (victim == null)
			return; 
		UUID uuid = MarryManager.get().getRelationship(damagerUUID);
		if (uuid == null)
			return; 
		if (!uuid.equals(victimUUID))
			return; 
		if (victim.getPersistentDataContainer().has(pvpKey, PersistentDataType.STRING) && damager.isSneaking()) {
			damager.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cTu pareja necesita desactivar PvP para poder besar."));
			return;
		} 
		if (!damager.isSneaking())
			return; 
		damager.playSound(damager.getLocation(), Sound.ENTITY_PANDA_EAT, 1.0F, 1.0F);
		damager.getWorld().spawnParticle(Particle.HEART, damager.getEyeLocation().add(0.0D, 0.5D, 0.0D), 5, 1.5D, 0.5D, 1.5D);
	}
  
	public UUID returnVictim(UUID damagerUUID) {
		Player damager = Bukkit.getPlayer(damagerUUID);
		for (Entity entity : damager.getNearbyEntities(1.5D, 1.5D, 1.5D)) {
			if (!(entity instanceof Player))
				continue; 
			RayTraceResult rayTraceResult = damager.getWorld().rayTraceEntities(damager.getEyeLocation(), damager.getEyeLocation().getDirection(), damager.getClientViewDistance(), e -> !e.equals(Bukkit.getEntity(damagerUUID)));
			if (rayTraceResult == null)
				continue; 
			if (rayTraceResult.getHitEntity() == null)
				continue; 
			return rayTraceResult.getHitEntity().getUniqueId();
		} 
		return null;
	}
	
}
