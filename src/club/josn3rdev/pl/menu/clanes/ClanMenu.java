package club.josn3rdev.pl.menu.clanes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.clanes.Clanes;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class ClanMenu extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	private Clanes clans;
	
	public ClanMenu(Player p, Clanes clans) { super("Clan: &c" + clans.getClanName(), 5);
				
		this.clans = clans;
	
		set(13, ItemBuilder.crearItem(Material.PAPER, 1, "&b", 
				"&6Información",
				"&fNombre del Clan: &e" + clans.getClanName(),
				"&fTag del Clan: &e" + clans.getClanTag(),
				"",
				"&6Otros:",
				"&fMiembros: &e" + clans.getClanMembers().size(),
				"&fProtección: &e" + (clans.getClanProtection() == null ? "&cNinguna" : Tools.get().setLocToStringBlock(clans.getClanProtection())),
				""));
		
		set(19, ItemBuilder.crearItem(Material.COMPARATOR, 1, "&eConfiguración", "&7Click para editar", "&7las opciones del clan."));
		set(21, ItemBuilder.crearItem(Material.DIAMOND, 1, "&eEstadísticas", 
				"", 
				"&6Asesinatos & Muertes",
				"&fAsesinatos: &e" + clans.getClanKills(),
				"&fMuertes: &e" + clans.getClanDeaths(),
				"&fKDR: &e" + clans.getClanKDR(),
				"",
				"&6Nivel & Poder:",
				"&fNivel del Clan: &e" + clans.getClanLevel(),
				"&fExp/Necesario: &e" + clans.getClanExp() + "&f/75",
				"&fTotal Exp del Clan: &e" + clans.getClanTotalExp(),
				"&fPoder del Clan: &e" + clans.getClanPower(),
				""));
		
		set(23, ItemBuilder.crearItem(Material.PLAYER_HEAD, 1, "&eMiembros", "&7Click para ver", "&7la lista de miembros."));
		set(25, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&eRangos", "&7Click para editar", "&7los rangos del clan."));
		
		set(31, ItemBuilder.crearItem(Material.BARRIER, 1, "&cAbandonar", "&7¿Quieres abandonar el clan?", "&7Click aquí para retirarte."));
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		if (name.equalsIgnoreCase(Text("&eConfiguración"))) {
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			new ClanConfigMenu(p, clans).open(p);
		}
		
		if (name.equalsIgnoreCase(Text("&eMiembros"))) {
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			//
		}
		
		if (name.equalsIgnoreCase(Text("&eRangos"))) {
			Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
			new ClanRanksMenu(p, clans).open(p);
		}
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
