package club.josn3rdev.pl.menu.clanes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.clanes.Clanes;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class ClanMembersMenu extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	private Clanes clans;
	private boolean edit = false;
	
	public ClanMembersMenu(Player p, Clanes clans) { super("Clan: &c" + clans.getClanName(), 3);
		
		this.clans = clans;
		
		set(0, ItemBuilder.crearItem(Material.BARRIER, 1, "&cVolver"));
	
		set(12, ItemBuilder.crearItem(Material.PAPER, 1, "&eNombre & Tag", 
				"&7Modifica el nombre del clan",
				"&7o el tag del clan.",
				"",
				"&6Información",
				"&fNombre del Clan: &e" + clans.getClanName(),
				"&fTag del Clan: &e" + clans.getClanTag(),
				"",
				"&a» &fClick izquierdo para editar el nombre.",
				"&a» &fClick derecho para editar el tag."));
		
		set(14, ItemBuilder.crearItem(Material.DIAMOND, 1, "&eFriendly Fire", 
				"&7Modifica el Friendly Fire.", 
				"", 
				"" + (clans.getFriendlyFire() == true ? "&a" + ("\u27A4") + " Activado" : "&fActivado"),
				"" + (clans.getFriendlyFire() == false ? "&a" + ("\u27A4") + " Desactivado" : "&fDesactivado"),
				"",
				"&7Click para modificar."));
		
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		if (name.equalsIgnoreCase(Text("&cVolver"))) {
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			new ClanMenu(p, clans).open(p);
		}
		
		if (name.equalsIgnoreCase(Text("&eFriendly Fire"))) {
			
			if (clans.getFriendlyFire()) {
				clans.setFriendlyFire(false);
			} else {
				clans.setFriendlyFire(true);
			}
			
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			set(14, ItemBuilder.crearItem(Material.DIAMOND, 1, "&eFriendly Fire", 
					"&7Modifica el Friendly Fire.", 
					"", 
					"" + (clans.getFriendlyFire() == true ? "&a" + ("\u27A4") + " Activado" : "&fActivado"),
					"" + (clans.getFriendlyFire() == false ? "&a" + ("\u27A4") + " Desactivado" : "&fDesactivado"),
					"",
					"&7Click para modificar."));
		}
		
		if (name.equalsIgnoreCase(Text("&eNombre & Tag"))) {
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			if (e.getClick() == ClickType.LEFT) {
				this.edit = true;
				editor(p, clans, "name", "Editor de Nombre");
			} else if (e.getClick() == ClickType.RIGHT) {
				this.edit = true;
				editor(p, clans, "tag", "Editor de Tag");
			}
		}
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		if (!this.edit) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new ClanMenu(p, clans).open(p);
				}
			});
		}
	}
	
	public void editor (Player p, Clanes clans, String option, String title) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new ClanMembersMenu(p, clans).open(p);
				}
			});
		})
		.onComplete((pl, text) -> {
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			
			if (option.equalsIgnoreCase("name")) {
				if (text.length() > 32) {
					text = text.substring(0, 32);
				}
				clans.setClanName(text);
			} else {
				if (text.length() > 3) {
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					return AnvilGUI.Response.text("El tag solo debe tener 3 letras.");
				}
				
				clans.setClanTag(text);
			}
			
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.PAPER))
		.title(Tools.get().Text(title))
		.plugin(MSRP.get())
		.open(p);
	}
	
}
