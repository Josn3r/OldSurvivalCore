package club.josn3rdev.pl.menu.jobs.garbage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.jobs.GarbageCollector;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class GarbageNewRoute extends Menu {
	
		public GarbageNewRoute(Player p) { super("Garbage Create Route", 4);
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		set(11, ItemBuilder.crearItem(Material.PAPER, 1, "&eRoute Name: &f" + sp.getMissionName(), "&7Click to set Route Name."));
		set(12, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eRoute Payment: &f$" + Tools.get().formatMoney(sp.getMissionPayment()), "&7Click to set Route Payment"));
		set(13, ItemBuilder.crearItem(Material.CLOCK, 1, "&eRoute Time: &f" + Tools.get().getFormatTime(sp.getMissionTimeleft()), "&7Click to set Route Time Limit."));
		set(14, ItemBuilder.crearItem(Material.LEATHER_HELMET, 1, "&eJob Level: &f" + sp.getMissionLevel(), "&7Click to set Route Need Job Level."));
		set(15, ItemBuilder.crearItem(Material.COMPASS, 1, "&eRoute Points: &f" + sp.getGarbagePoints().size(), "&7Click to set Route Points."));
		
		set(30, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&aSave", "&7Click to save."));
		set(32, ItemBuilder.crearItem(Material.RED_DYE, 1, "&cCancel", "&7Click to cancel."));
		
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
						
		if (name.equalsIgnoreCase(Tools.get().Text("&eRoute Name: &f" + sp.getMissionName()))) {
			editOption(p, "name");
		} else if (name.equalsIgnoreCase(Tools.get().Text("&eRoute Payment: &f$" + Tools.get().formatMoney(sp.getMissionPayment())))) {
			editOption(p, "payment");
		} else if (name.equalsIgnoreCase(Tools.get().Text("&eRoute Time: &f" + Tools.get().getFormatTime(sp.getMissionTimeleft())))) {
			editOption(p, "timeleft");
		} else if (name.equalsIgnoreCase(Tools.get().Text("&eJob Level: &f" + sp.getMissionLevel()))) {
			editOption(p, "joblevel");
		} else if (name.equalsIgnoreCase(Tools.get().Text("&eRoute Points: &f" + sp.getGarbagePoints().size()))) {
			p.closeInventory();
			sp.getGarbagePoints().clear();
			p.getInventory().addItem(ItemBuilder.crearItem(Material.BEACON, 32, "&6Garbage Route Point", ""));
		} else if (name.equalsIgnoreCase(Tools.get().Text("&cCancel"))) {
			p.closeInventory();
			sp.resetMissionCreator();
		} else if (name.equalsIgnoreCase(Tools.get().Text("&aSave"))) {
			p.closeInventory();
			GarbageCollector.get().createRoute(sp.getMissionName(), sp.getMissionPayment(), sp.getMissionLevel(), sp.getMissionTimeleft(), sp.getGarbagePoints());
			sp.resetMissionCreator();
		}
	}
		
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public void editOption (Player p, String option) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new GarbageNewRoute(p).open(p);
				}
			}, 5L);
		})
		.onComplete((pl, text) -> {
			
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			
			String tcode = text;
			Pattern pattern = Pattern.compile("[0-9.]*");
			Matcher match = pattern.matcher(tcode);
			
			if (!option.equalsIgnoreCase("name")) {
				if (!match.matches()) {
					return AnvilGUI.Response.text("Only number");
				}
			}
			
			if (option.equalsIgnoreCase("name")) {
				sp.setMissionName(text);
			} else if (option.equalsIgnoreCase("payment")) {
				sp.setMissionPayment(Double.valueOf(text));
			} else if (option.equalsIgnoreCase("timeleft")) {
				sp.setMissionTimeleft(Integer.valueOf(text));
			} else if (option.equalsIgnoreCase("joblevel")) {
				sp.setMissionLevel(Integer.valueOf(text));
			}
			
			new GarbageNewRoute(p).open(p);
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.PAPER))
		.title(Tools.get().Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
	
}
