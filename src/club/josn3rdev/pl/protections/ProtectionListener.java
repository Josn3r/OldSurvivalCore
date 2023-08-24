package club.josn3rdev.pl.protections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class ProtectionListener implements Listener {
	
	private final Map<Player, Protections> playerRegionMap = new HashMap<Player, Protections>();
	  
	@EventHandler
	public void onBlockBreakEvent (BlockBreakEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
    			
		for (Protections protes : ProtectionManager.get().ProtectionsSet()) {			
			if (protes.containsBlock(e.getBlock())) {
				
				if (!sp.getProtectionMember().contains(protes.getUUID()) && !sp.getProtectionOwner().contains(protes.getUUID())) {
					e.setCancelled(true);
					p.sendMessage(Tools.get().Text("&c&lHey! &7no puedes romper en esta zona protegida por &e" + protes.getOwner().getName() + "&7. (ID: " + protes.getUUID() + ")"));
					return;
				}
				
				if (protes.getSelection().getCenterPoint().equals(e.getBlock())) {
					if (!sp.getProtectionOwner().contains(protes.getUUID())) {
						e.setCancelled(true);
						p.sendMessage(Tools.get().Text("&c&lHey! &7no puedes romper la protección si no eres el lider &e" + protes.getOwner().getName() + "&7. (ID: " + protes.getUUID() + ")"));
						return;
					}
					
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					
					Integer size = protes.getSize();
					Double cost = (1450.00 * size);
					Double tax = (cost/10);
					
					p.getInventory().addItem(ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&6Proteccion: &fx" + size, 
							"&7Bloque de protección.",
							" ",
							"&fTamaño de Protección: &e" + size + "x" + size,
							"&fCosto de Impuestos: &6&l$&e" + Tools.get().formatMoney(tax),
							""));
					
					MSRP.get().getServer().getScheduler().runTaskLaterAsynchronously(MSRP.get(), new Runnable() {
						@Override
						public void run() {
							Tools.get().playSound(p, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);					
							p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fEliminaste la proteccion con el ID &e<UUID>&f.".replace("<UUID>", protes.getUUID())));
							
							sp.getProtectionOwner().remove(protes.getUUID());
							for (String members : protes.getMembers()) {
								SPlayer sm = PlayerManager.get().getPlayer(UUID.fromString(members.split(" : ")[0]));
						    	if (sm != null) {
									sm.getProtectionMember().remove(protes.getUUID());
								}
							}
							ProtectionManager.get().removeProtection(protes.getUUID());
						}
					}, 1L);
					
					break;
				}
			}
		}		
	}
	
	@EventHandler
	public void onBlockPlaceEvent (BlockPlaceEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		for (Protections protes : ProtectionManager.get().ProtectionsSet()) {
			if (protes.containsBlock(e.getBlock())) {
				
				if (!sp.getProtectionMember().contains(protes.getUUID()) && !sp.getProtectionOwner().contains(protes.getUUID())) {
					e.setCancelled(true);
					p.sendMessage(Tools.get().Text("&c&lHey! &7no puedes construir en esta zona protegida por &e" + protes.getOwner().getName() + "&7. (ID: " + protes.getUUID() + ")"));
					return;
				}
			}
		}
		
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && 
			item.getItemMeta().getDisplayName().contains(Tools.get().Text("&6Proteccion: &fx"))) {
			
			if (sp.getProtectionOwner().size() >= ProtectionManager.get().maxProtections(p)) {
				e.setCancelled(true);
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEstás al límite de protecciones!"));
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo puedes tener más de &e" + ProtectionManager.get().maxProtections(p) + " &cprotecciones, elimina una o adquiere un rango superior para aumentar el limite."));
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
				return;
			}
			
			String displayName = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
			Integer size = Integer.valueOf(displayName.replace(Tools.get().Text("&6Proteccion: &fx"), ""));
			Double cost = (1450.00 * size);
			Double tax = (cost/10);
			
			if (ProtectionManager.get().checkRadiusProtection(e.getBlockPlaced().getLocation(), size)) {
				e.setCancelled(true);
				Selection selection = new Selection(e.getBlockPlaced(), size);    	
				Protections prote = ProtectionManager.get().getOverlap(selection);
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo puedes colocar esa protección ahi porque está cerca de la protección de &e" + prote.getOwner().getName() + "&c, aléjate un poco más."));
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
				return;
			}
			
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&a&lFELICIDADES! &fHas protegido una nueva zona! Podrás construir sin miedo a que destruyan tu casa!"));
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cRecuerda que por tu protección deberás pagar &6&l$&e" + Tools.get().formatMoney(tax) + " &cde impuestos cada 24 horas!"));
			Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
			ProtectionManager.get().registerNewProtection(p, new Selection(e.getBlockPlaced(), size));
		}
	}
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event) {
		for (Protections region : ProtectionManager.get().ProtectionsSet()) {
			if (event.getBlocks().contains(region.getSelection().getCenterPoint())) {
				event.setCancelled(true);
				break;
			} 
		} 
	}
	  
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event) {
		for (Protections region : ProtectionManager.get().ProtectionsSet()) {
			if (event.getBlocks().contains(region.getSelection().getCenterPoint())) {
				event.setCancelled(true);
				break;
			} 
		} 
	}
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		sp.setPlayerInRegion(false);
		
		for (Protections protes : ProtectionManager.get().ProtectionsSet()) {
			if (protes.containsPlayer(p)) {
				if (!playerRegionMap.containsKey(p)) {
					String owner = protes.getOwner().getName();
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.messages")) {
						p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.protections.join-protection.message").replace("<OWNER>", owner)));
					}
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.actionbar")) {
						Tools.get().sendActionBar(p, Tools.get().Text(MSRP.lang.getString("messages.protections.join-protection.actionbar").replace("<OWNER>", owner)));
					}
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.title")) {
						String title = MSRP.lang.getString("messages.protections.join-protection.title").split(" : ")[0].replace("<OWNER>", owner);
						String subtitle = MSRP.lang.getString("messages.protections.join-protection.title").split(" : ")[1].replace("<OWNER>", owner);
						Tools.get().sendTitle(p, title, subtitle, 20);
					}
					Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
					playerRegionMap.put(p, protes);
					sp.setPlayerInRegion(true);
					break;
				}
				
				if (playerRegionMap.containsKey(p) && !protes.equals(playerRegionMap.get(p))) {
					String owner1 = protes.getOwner().getName();
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.messages")) {
						p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.protections.join-protection.message").replace("<OWNER>", owner1)));
					}
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.actionbar")) {
						Tools.get().sendActionBar(p, Tools.get().Text(MSRP.lang.getString("messages.protections.join-protection.actionbar").replace("<OWNER>", owner1)));
					}
					if (MSRP.get().getConfig().getBoolean("config.protections.config.join-protection.title")) {
						String title = MSRP.lang.getString("messages.protections.join-protection.title").split(" : ")[0].replace("<OWNER>", owner1);
						String subtitle = MSRP.lang.getString("messages.protections.join-protection.title").split(" : ")[1].replace("<OWNER>", owner1);
						Tools.get().sendTitle(p, title, subtitle, 20);
					}
					Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
					playerRegionMap.put(p, protes);
				}
				sp.setPlayerInRegion(true);
				break;
			}			
		}
		
		if (!sp.getPlayerInRegion() && playerRegionMap.containsKey(p)) {
			String owner = playerRegionMap.get(p).getOwner().getName();
			if (MSRP.get().getConfig().getBoolean("config.protections.config.leave-protection.messages")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.protections.leave-protection.message").replace("<OWNER>", owner)));
			}
			if (MSRP.get().getConfig().getBoolean("config.protections.config.leave-protection.actionbar")) {
				Tools.get().sendActionBar(p, Tools.get().Text(MSRP.lang.getString("messages.protections.leave-protection.actionbar").replace("<OWNER>", owner)));
			}
			if (MSRP.get().getConfig().getBoolean("config.protections.config.leave-protection.title")) {
				String title = MSRP.lang.getString("messages.protections.leave-protection.title").split(" : ")[0].replace("<OWNER>", owner);
				String subtitle = MSRP.lang.getString("messages.protections.leave-protection.title").split(" : ")[1].replace("<OWNER>", owner);
				Tools.get().sendTitle(p, title, subtitle, 20);
			}
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			playerRegionMap.remove(p);
		}
	}
	
	@EventHandler
	public void damageEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player)event.getDamager();
			if (p.hasPermission("msrp.admin"))
				return; 
			for (Protections region : ProtectionManager.get().ProtectionsSet()) {
				if (region.getSelection().containsBlock(event.getEntity().getLocation().getBlock())) {
					if (event.getEntity() instanceof Player) {
						event.setCancelled(true);
						return;
					} 
					if (!region.getMembers().contains(p.getUniqueId().toString()) && region.getOwner() != p)
						event.setCancelled(true); 
					break;
				} 
			} 
		} 
	}

	@EventHandler
	public void entityInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("EpicProtections.Admin"))
			return; 
		for (Protections region : ProtectionManager.get().ProtectionsSet()) {
			if (region.getSelection().containsBlock(event.getRightClicked().getLocation().getBlock())) {
				if (!region.getMembers().contains(player.getUniqueId().toString()) && region.getOwner() != player)
					event.setCancelled(true); 
				break;
			} 
		} 
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("msrp.admin"))
			return; 
		for (Protections region : ProtectionManager.get().ProtectionsSet()) {
			if (region.containsBlock(event.getBlockClicked())) {
				if (region.getMembers().contains(player.getUniqueId().toString()) || region.getOwner() == player)
					return; 
				event.setCancelled(true); 
				break;
			} 
		} 
	}
	  
	@EventHandler
	public void projectileShoot(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile))
			return; 
		for (Protections region : ProtectionManager.get().ProtectionsSet()) {
			if (region.containsBlock(event.getEntity().getLocation().getBlock())) {
				Projectile projectile = (Projectile)event.getDamager();
				if (projectile.getShooter() instanceof Player) {
					Player player = (Player)projectile.getShooter();
					if (!region.getMembers().contains(player.getUniqueId().toString()) && region.getOwner() != player && !player.hasPermission("msrp.admin")) {
						event.setCancelled(true);
						projectile.playEffect(EntityEffect.ENTITY_POOF);
					} 
				} 
				break;
			} 
		} 
	}
	
}
