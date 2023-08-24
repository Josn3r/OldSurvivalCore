package club.josn3rdev.pl.menu.jobs.garbage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class GarbageCreator extends Menu {
	
		public GarbageCreator(Player p) { super("Garbage Config", 3);
		
		set(12, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&aCreate New Route", "&7- Click to Create New Garbage Route."));
		set(13, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&eGarbage Zone", "&7- Click to set Garbage Zone."));
		set(14, ItemBuilder.crearItem(Material.PAPER, 1, "&eGarbage Route List", "&7- Click to open."));
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		if (name.equalsIgnoreCase(Tools.get().Text("&eGarbage Route List"))) {
			new GarbageCreatorList(p).open(p);
		} else if (name.equalsIgnoreCase(Tools.get().Text("&aCreate New Route"))) {
			new GarbageNewRoute(p).open(p);
		} else if (name.equalsIgnoreCase(Tools.get().Text("&eGarbage Zone"))) {
			p.closeInventory();
			p.getInventory().setItem(0, ItemBuilder.crearItem(Material.BEACON, 1, "&6Garbage Zone", ""));
		}
	}
		
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
