package club.josn3rdev.pl.menu.clanes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.clanes.Clanes;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class ClanRanksMenu extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	private Clanes clans;
	private boolean edit = false;
	
	public ClanRanksMenu(Player p, Clanes clans) { super("Clan: &c" + clans.getClanName(), 3);
		
		this.clans = clans;
		
		set(0, ItemBuilder.crearItem(Material.BARRIER, 1, "&cVolver"));
	
		// 'RANK ID : RANK NAME : RANK INVITE : RANK KICK : RANK EDIT MEMBERS : RANK ENABLE FRIENDLY FIRE'
		
		Integer slot = 11;
		for (String ranks : clans.getClanRanks()) {			
			String rankID = ranks.split(" : ")[0];
			String rankName = ranks.split(" : ")[1];
			
			Boolean rank_invite = Boolean.valueOf(ranks.split(" : ")[2]);
			Boolean rank_kick = Boolean.valueOf(ranks.split(" : ")[3]);
			Boolean rank_edit = Boolean.valueOf(ranks.split(" : ")[4]);
			Boolean rank_friendly = Boolean.valueOf(ranks.split(" : ")[5]);
			
			set(slot, ItemBuilder.crearItem(Material.PAPER, 1, "" + rankName, 
					"",
					"&6Información:", 
					"&fID del Rango: &e" + rankID,
					"",
					"&6Permisos:", 
					"&fInvitar jugadores: &e" + (rank_invite ? "&a" + ("\u2714") : "&c" + ("\u2718")),
					"&fExpulsar miembros: &e" + (rank_kick ? "&a" + ("\u2714") : "&c" + ("\u2718")),
					"&fAscender/Descender: &e" + (rank_edit ? "&a" + ("\u2714") : "&c" + ("\u2718")),
					"&fModificar Friendly Fire: &e" + (rank_friendly ? "&a" + ("\u2714") : "&c" + ("\u2718")),
					"",
					"&e» &fClick para modificar."));
			
			++slot;
		}
				
		
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
	
}
