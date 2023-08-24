package club.josn3rdev.pl.menu.jobs.garbage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class GarbageCreatorList extends Menu {
	
		public GarbageCreatorList(Player p) { super("Garbage Route List", 3);
		
		Integer slot = 11;
		for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {				
			createMission(slot, mision);
			++slot;
		}
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {
			String ruta = MSRP.garbage.getString("config.routes." + mision + ".routeName");
			if (name.contains(ruta)) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEliminasta correctamente &f" + name + "&c, Nodo: &f" + mision));
				MSRP.garbage.set("config.routes." + mision, null);
				updateMenu(p);
			}
		}
	}
		
	private void updateMenu(Player p) {
		clear();
		Integer slot = 11;
		for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {				
			createMission(slot, mision);
			++slot;
		}		
	}
	
	private void createMission (Integer slot, String mission) {	
		String ruta = MSRP.garbage.getString("config.routes." + mission + ".routeName");
		Double payment = MSRP.garbage.getDouble("config.routes." + mission + ".routePayment");
		Integer timeleft = MSRP.garbage.getInt("config.routes." + mission + ".routeTime");
		Integer jobLevel = MSRP.garbage.getInt("config.routes." + mission + ".routeJobLevel");
		Integer points = MSRP.garbage.getStringList("config.routes." + mission + ".routePoints").size();
		
		set(slot, ItemBuilder.crearItem(Material.COMPASS, 1, "&c" + ruta, "&fInformación:", "&7» Pago: &e$" + Tools.get().formatMoney(payment), "&7» Tiempo límite: &e" + Tools.get().getFormatTime(timeleft), "&7» Nivel de trabajo: &e" + jobLevel, "&7» Puntos de control: &e" + points, "", "&c» Click para eliminar."));
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
