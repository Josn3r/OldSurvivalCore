package club.josn3rdev.pl.marry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class MarryManager {

	private static MarryManager ins;
	
	public static MarryManager get() {
		if (ins == null) {
			ins = new MarryManager();
		}
		return ins;
	}
	
	public void addRelationship (Player player, Player target) {
		SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
		SPlayer st = PlayerManager.get().getPlayer(target.getUniqueId());
		
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
	    Date date = Calendar.getInstance(timeZone).getTime();
	    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
	    
		sp.setMarryUUID(target.getUniqueId().toString());
		sp.setMarryName(target.getName());
		sp.setMarryDate(format.format(date));
		
		st.setMarryUUID(player.getUniqueId().toString());
		st.setMarryName(player.getName());
		st.setMarryDate(format.format(date));
		
	}
	
	public void removeRelationship(Player player, Player target) {
		SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
		SPlayer st = PlayerManager.get().getPlayer(target.getUniqueId());
		
		sp.setMarryUUID(null);
		sp.setMarryName(null);
		sp.setMarryDate(null);
		
		st.setMarryUUID(null);
		st.setMarryName(null);
		st.setMarryDate(null);
	}
	
	public UUID getRelationship (UUID receiver) {
		SPlayer sp = PlayerManager.get().getPlayer(receiver);
		if (sp.getMarryUUID() == null)
			return null; 
	    return UUID.fromString(sp.getMarryUUID());
	}
	
	public void checkRelationship(Player player, Player target, RequestTypes requestType) {
		SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
		if (player == target) {
			player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo te puedes hacer eso contigo mismo."));
			return;
		} 
		switch (requestType) {
			case MARRY:
				if (sp.getMarryUUID() != null) {
					player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo te puedes casar con más de una persona!"));
					return;
				} 
				break;
			case DIVORCE:
				if (sp.getMarryUUID() == null) {
					player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo te puedes divorciar si no estás casado."));
					return;
				} 
				if (sp.getMarryUUID().equalsIgnoreCase(target.getUniqueId().toString())) {
					player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo estás casado con esa persona!"));
					return;
				} 
				break;
		} 
	    player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&eSolicitud enviada con éxito!"));
	    new Request(player.getUniqueId(), target.getUniqueId(), requestType);
	}
	
	public void togglePvP (UUID playerUUID) {
		Player player = Bukkit.getPlayer(playerUUID);
		NamespacedKey pvpKey = new NamespacedKey(MSRP.get(), "pvpToggle");
		PersistentDataContainer playerPDC = player.getPersistentDataContainer();
		if (!playerPDC.has(pvpKey, PersistentDataType.STRING)) {
			player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&aActivaste el PvP entre parejas."));
		    playerPDC.set(pvpKey, PersistentDataType.STRING, "ON");
		} else {
			player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cDesactivaste el PvP entre parejas!"));
		    playerPDC.remove(pvpKey);
		} 
	}
	
	//
	//
	///
	//
	//
	private BukkitTask celebrationTask;
	  
	public void announceMarriage(UUID lover1UUID, UUID lover2UUID) {
		Player lover1 = Bukkit.getPlayer(lover1UUID);
		Player lover2 = Bukkit.getPlayer(lover2UUID);
		//PlayerInteract.sendToAll("Congratulations to the new couple! " + ChatColor.GOLD + ChatColor.BOLD + lover1.getName().toUpperCase() + ChatColor.GRAY + " and " + ChatColor.GOLD + ChatColor.BOLD + lover2.getName().toUpperCase() + ChatColor.GRAY + " has just married!", UtilTypes.FANCY);
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fFelicidades a la nueva pareja! &6&l" + lover1.getName() + " &fy &6&l" + lover2.getName() + " &fse han casado!"));
		}
		AtomicInteger fireworkFired = new AtomicInteger();
		celebrationTask = Bukkit.getScheduler().runTaskTimer(MSRP.get(), () -> {
			fireworkFired.addAndGet(1);
			if (lover1.isOnline()) {
				spawnFirework(lover1UUID);
				lover1.getWorld().spawnParticle(Particle.HEART, lover1.getEyeLocation().add(0.0D, 0.5D, 0.0D), 5, 1.5D, 0.5D, 1.5D);
			} 
			if (lover2.isOnline()) {
				spawnFirework(lover2UUID);
				lover2.getWorld().spawnParticle(Particle.HEART, lover2.getEyeLocation().add(0.0D, 0.5D, 0.0D), 5, 1.5D, 0.5D, 1.5D);
			} 
			if (fireworkFired.get() == 5)
				celebrationTask.cancel(); 
		}, 0L, 20L);
	}
	  
	public void announceDivorce(UUID lover1UUID, UUID lover2UUID) {
		Player lover1 = Bukkit.getPlayer(lover1UUID);
		Player lover2 = Bukkit.getPlayer(lover2UUID);
		ArrayList<Player> coupleList = new ArrayList<>();
	    coupleList.add(lover1);
	    coupleList.add(lover2);
	    for (Player online : Bukkit.getOnlinePlayers()) {
			online.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fD'oh! &6&l" + lover1.getName() + " &fy &6&l" + lover2.getName() + " &fse han divorciado!"));
		}
	    for (Player player : coupleList) {
	    	player.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cTe sientes deprimido/a después del reciente divorcio..."));
	    	player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1, true, true));
	    	player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 5, true, true));
	    	player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 10, true, true));
	    	player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 10, true, true));
	    	player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SKELETON_DEATH, 1.0F, 2.0F);
	    } 
	}
	  
	public static void spawnFirework(UUID playerUUID) {
	    Player player = Bukkit.getPlayer(playerUUID);
	    Firework firework = (Firework)player.getWorld().spawn(player.getLocation(), Firework.class);
	    FireworkMeta fireworkMeta = firework.getFireworkMeta();
	    fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.NAVY).withColor(Color.GREEN).withColor(Color.YELLOW).withColor(Color.ORANGE).withColor(Color.BLUE).withColor(Color.PURPLE).with(FireworkEffect.Type.BALL_LARGE).build());
	    fireworkMeta.setPower(1);
	    firework.setFireworkMeta(fireworkMeta);
	}
	
}
