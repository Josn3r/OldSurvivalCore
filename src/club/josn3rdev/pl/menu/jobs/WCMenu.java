package club.josn3rdev.pl.menu.jobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.jobs.Jobs;
import club.josn3rdev.pl.jobs.Woodcutter;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class WCMenu extends Menu {
	
		public WCMenu(Player p) { super(MSRP.lang.getString("messages.jobs.global-menu.title").replace("<JOB>", MSRP.lang.getString("messages.jobs.woodcutter.displayname")), 3);
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		List<String> description = MSRP.lang.getStringList("messages.jobs.global-menu.join-job.description");
		
		if (sp.getJob1() != Jobs.WOODCUTTER && sp.getJob2() != Jobs.WOODCUTTER) {
			set(13, ItemBuilder.crearItem(Material.DIAMOND_AXE, 1, MSRP.lang.getString("messages.jobs.global-menu.join-job.displayname"), description));
		} else {
			set(0, ItemBuilder.crearItem(Material.REDSTONE, 1, "&cRenunciar", "&7¿No quieres seguir trabajando conmigo?", "&7Click aquí para renunciar al trabajo."));
			
			for (String mision : MSRP.get().getConfig().getConfigurationSection("config.jobs.missions.woodcutter").getKeys(false)) {
				Integer slot = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".menuSlot");
				Integer amount = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".amount");
				Double payment = MSRP.get().getConfig().getDouble("config.jobs.missions.woodcutter." + mision + ".payment");
				Integer time = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".timeToFinish");
				
				createMission(slot, amount, payment, time);
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
			
			p.sendMessage(Tools.get().Text(Woodcutter.get().PREFIX + "&fEntiendo, no pasa nada, acepto tu renuncia."));
			p.sendMessage(Tools.get().Text(Woodcutter.get().PREFIX + "&fEspero volver a verte por aquí trabajando, cuidate!"));
			Tools.get().playSound(p, Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
			
			p.sendMessage(" ");
			p.sendMessage(Tools.get().Text("&eRenunciaste a tu trabajo de &f<JOB>").replace("<JOB>", MSRP.lang.getString("messages.jobs.woodcutter.displayname")));
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			if (sp.getJob1() == Jobs.WOODCUTTER) {
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
				
				sp.setJob2(Jobs.WOODCUTTER);
				Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.join-job").replace("<JOB>", ""+MSRP.lang.getString("messages.jobs.woodcutter.displayname"))));
				updateMenu(p);
				return;
			}
			
			sp.setJob1(Jobs.WOODCUTTER);
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + MSRP.lang.getString("messages.jobs.join-job").replace("<JOB>", ""+MSRP.lang.getString("messages.jobs.woodcutter.displayname"))));
			updateMenu(p);
		}
		
		for (String mision : MSRP.get().getConfig().getConfigurationSection("config.jobs.missions.woodcutter").getKeys(false)) {
			Integer slot = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".menuSlot");
			
			if (e.getSlot() == slot) {
				Woodcutter.get().setMission(p, mision);
				p.sendMessage(Tools.get().Text(Woodcutter.get().getPrefix() + "&fIniciaste la misión de &e" + name));
				p.closeInventory();
			}
		}
			
			
		
	}
	
	private void updateMenu(Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		clear();
		
		List<String> description = MSRP.lang.getStringList("messages.jobs.global-menu.join-job.description");
		
		if (sp.getJob1() != Jobs.WOODCUTTER && sp.getJob2() != Jobs.WOODCUTTER) {
			set(13, ItemBuilder.crearItem(Material.DIAMOND_AXE, 1, MSRP.lang.getString("messages.jobs.global-menu.join-job.displayname"), description));
		} else {
			set(0, ItemBuilder.crearItem(Material.REDSTONE, 1, "&cRenunciar", "&7¿No quieres seguir trabajando conmigo?", "&7Click aquí para renunciar al trabajo."));
			
			for (String mision : MSRP.get().getConfig().getConfigurationSection("config.jobs.missions.woodcutter").getKeys(false)) {
				Integer slot = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".menuSlot");
				Integer amount = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".amount");
				Double payment = MSRP.get().getConfig().getDouble("config.jobs.missions.woodcutter." + mision + ".payment");
				Integer time = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + mision + ".timeToFinish");
				
				createMission(slot, amount, payment, time);
			}
		}		
	}
	
	private void createMission (Integer slot, Integer amount, Double payment, Integer time) {
		
		List<String> description = new ArrayList<String>();		
		for (String str : MSRP.lang.getStringList("messages.jobs.woodcutter.menu.mission-selector.description")) {
			str = str.replace("<AMOUNT>", ""+amount)
					 .replace("<PAYMENT>", ""+Tools.get().formatMoney(payment)
					 .replace("<TIME>", ""+Tools.get().getFormatTime(time)));
			description.add(str);
		}		
		set(slot, ItemBuilder.crearItem(Material.OAK_LOG, amount, MSRP.lang.getString("messages.jobs.woodcutter.menu.mission-selector.displayname").replace("<AMOUNT>", ""+amount), description));
	}

	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
