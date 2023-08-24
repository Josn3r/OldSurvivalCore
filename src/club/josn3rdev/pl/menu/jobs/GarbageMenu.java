package club.josn3rdev.pl.menu.jobs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.jobs.GarbageCollector;
import club.josn3rdev.pl.jobs.Jobs;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class GarbageMenu extends Menu {
	
		public GarbageMenu(Player p) { super(MSRP.lang.getString("messages.jobs.global-menu.title").replace("<JOB>", MSRP.lang.getString("messages.jobs.garbage.displayname")), 3);
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		List<String> description = MSRP.lang.getStringList("messages.jobs.global-menu.join-job.description");
		
		if (sp.getJob1() != Jobs.GARBAGE && sp.getJob2() != Jobs.GARBAGE) {
			set(13, ItemBuilder.crearItem(Material.IRON_HOE, 1, MSRP.lang.getString("messages.jobs.global-menu.join-job.displayname"), description));
		} else {
			set(0, ItemBuilder.crearItem(Material.REDSTONE, 1, "&cRenunciar", "&7¿No quieres seguir trabajando conmigo?", "&7Click aquí para renunciar al trabajo."));
			
			Integer slot = 11;
			for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {				
				createMission(slot, mision);
				++slot;
			}
		}
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
			
		String joinJob = MSRP.lang.getString("messages.jobs.global-menu.join-job.displayname");
		
		//
			
		if (name.equalsIgnoreCase(Tools.get().Text("&cRenunciar"))) {
			
			p.sendMessage(Tools.get().Text(GarbageCollector.get().getPrefix() + "&fEntiendo, no pasa nada, acepto tu renuncia."));
			p.sendMessage(Tools.get().Text(GarbageCollector.get().getPrefix() + "&fEspero volver a verte por aquí trabajando, cuidate!"));
			Tools.get().playSound(p, Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
			
			p.sendMessage(" ");
			p.sendMessage(Tools.get().Text("&eRenunciaste a tu trabajo de &f<JOB>").replace("<JOB>", MSRP.lang.getString("messages.jobs.garbage.displayname")));
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			if (sp.getJob1() == Jobs.GARBAGE) {
				sp.setJob1(null);
			} else {
				sp.setJob2(null);
			}
			p.closeInventory();
		}
		
		
		if (name.equalsIgnoreCase(Tools.get().Text(joinJob))) {
			if (sp.getJob1() != null) {
				if (!p.hasPermission("jrpcore.jobs.job2")) {
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.already-have-job")));
					return;
				}
				
				if (sp.getJob2() != null) {
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.already-have-job")));
					return;
				}
				
				sp.setJob2(Jobs.GARBAGE);
				Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.join-job").replace("<JOB>", ""+MSRP.lang.getString("messages.jobs.garbage.displayname"))));
				updateMenu(p);
				return;
			}
			
			sp.setJob1(Jobs.GARBAGE);
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.join-job").replace("<JOB>", ""+MSRP.lang.getString("messages.jobs.garbage.displayname"))));
			updateMenu(p);
			return;
		}
		
		/*for (String mision : MSRP.get().getConfig().getConfigurationSection("config.jobs.missions.harvester").getKeys(false)) {
			Integer slot = MSRP.get().getConfig().getInt("config.jobs.missions.harvester." + mision + ".menuSlot");
			
			if (e.getSlot() == slot) {
				Harvester.get().setMission(p, mision);
				p.sendMessage(Tools.get().Text(Harvester.get().getPrefix() + "&fIniciaste la misión de &e" + name));
				p.closeInventory();
			}
		}*/
		
		for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {
			String ruta = MSRP.garbage.getString("config.routes." + mision + ".routeName");
			if (name.contains(ruta)) {
				GarbageCollector.get().setMission(p, mision);
				GarbageCollector.get().startMission(p);
				p.closeInventory();
			}
		}
			
			
		
	}
	
	private void updateMenu(Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		clear();
		
		List<String> description = MSRP.lang.getStringList("messages.jobs.global-menu.join-job.description");
		
		if (sp.getJob1() != Jobs.GARBAGE && sp.getJob2() != Jobs.GARBAGE) {
			set(13, ItemBuilder.crearItem(Material.IRON_HOE, 1, MSRP.lang.getString("messages.jobs.global-menu.join-job.displayname"), description));
		} else {
			set(0, ItemBuilder.crearItem(Material.REDSTONE, 1, "&cRenunciar", "&7¿No quieres seguir trabajando conmigo?", "&7Click aquí para renunciar al trabajo."));
			
			Integer slot = 11;
			for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {				
				createMission(slot, mision);
				++slot;
			}
		}		
	}
	
	private void createMission (Integer slot, String mission) {	
		String ruta = MSRP.garbage.getString("config.routes." + mission + ".routeName");
		Double payment = MSRP.garbage.getDouble("config.routes." + mission + ".routePayment");
		Integer timeleft = MSRP.garbage.getInt("config.routes." + mission + ".routeTime");
		Integer jobLevel = MSRP.garbage.getInt("config.routes." + mission + ".routeJobLevel");
		Integer points = MSRP.garbage.getStringList("config.routes." + mission + ".routePoints").size();
		
		set(slot, ItemBuilder.crearItem(Material.COMPASS, 1, "&e" + ruta, "&7Completa la &f" + ruta, " ", "&7» Pago: &e$" + Tools.get().formatMoney(payment), "&7» Tiempo límite: &e" + Tools.get().getFormatTime(timeleft), "&7» Nivel de trabajo: &e" + jobLevel, "&7» Puntos de control: &e" + points, "", "&a» Click para empezar."));
	}

	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
