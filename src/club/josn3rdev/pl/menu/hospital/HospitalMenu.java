package club.josn3rdev.pl.menu.hospital;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class HospitalMenu extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public HospitalMenu(Player p) { super("Hospital", 4);
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
	
		Integer gasto = 850;
		if (sp.getHospital() == 2) {
			gasto = 150;
		} else if (sp.getHospital() == 1) {
			gasto = 500;
		}
		
		set(13, ItemBuilder.crearItem(Material.PAPER, 1, "&f", 
				"&6Información:", 
				"",
				"&fActualmente pagas &6&l$&e"+gasto,
				"&fal Hospital por muerte.",
				"",
				"&fSeguro Médico: &e" + (sp.getHospital() >= 1 ? "&a" + ("\u2714") : "&c" + ("\u2718")),
				"&fSocio del Hospital: &e" + (sp.getHospital() >= 2 ? "&a" + ("\u2714") : "&c" + ("\u2718"))));
		
		if (sp.getHospital() == 2) {
			set(21, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&aSeguro Médico", 
					"&7Adquiere un Seguro Médico y paga",
					"&7solo $500 de gastos médicos!",
					" ",
					"&a» Ya tienes seguro."));
			
			set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&aSocio del Hospital", 
					"&7Únete a la sociedad del Hospital",
					"&7Y paga solo $150 de gastos médicos!",
					" ",
					"&fCosto de la sociedad: &6&l$&e12.500",
					" ",
					"&a» Ya eres socio."));
		} else if (sp.getHospital() == 1) {
			set(21, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&aSeguro Médico", 
					"&7Adquiere un Seguro Médico y paga",
					"&7solo $500 de gastos médicos!",
					" ",
					"&a» Ya tienes seguro."));
			
			set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&cSocio del Hospital", 
					"&7Únete a la sociedad del Hospital",
					"&7Y paga solo $150 de gastos médicos!",
					" ",
					"&fCosto de la sociedad: &6&l$&e12.500",
					" ",
					"&e» Click para adquirir."));
		} else {
			set(21, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&cSeguro Médico", 
					"&7Adquiere un Seguro Médico y paga",
					"&7solo $500 de gastos médicos!",
					" ",
					"&fCosto del Seguro: &6&l$&e3.500",
					" ",
					"&e» Click para adquirir."));
			
			set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&cSocio del Hospital", 
					"&7Únete a la sociedad del Hospital",
					"&7Y paga solo $150 de gastos médicos!",
					" ",
					"&fCosto de la sociedad: &6&l$&e12.500",
					" ",
					"&c» Debes tener un seguro médico primero."));
		}
		
	
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (name.equalsIgnoreCase(Text("&cSeguro Médico"))) {
			if (Vault.getMoney(p) < 3500.0) {
				p.closeInventory();
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo pudiste adquirir el seguro médico porque no tienes suficiente dinero."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 10.0f);
				return;
			}
			
			Vault.removeMoney(p, 3500.0);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHas comprado un Seguro Médico, ahora pagarás &a$500 &fcada vez que mueras!"));
			Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 10.0f);
		}
			
		if (name.equalsIgnoreCase(Text("&cSocio del Hospital"))) {
			if (sp.getHospital() < 1) {
				p.closeInventory();
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cPrimero debes tener un seguro médico antes de adquirir la sociedad del Hospital."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 10.0f);
				return;
			}
			
			if (Vault.getMoney(p) < 12500.0) {
				p.closeInventory();
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo pudiste adquirir la sociedad del Hospital porque no tienes suficiente dinero."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 10.0f);
				return;
			}
			
			Vault.removeMoney(p, 12500.0);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHas comprado una sociedad al Hospital, ahora pagarás &a$150 &fcada vez que mueras!"));
			Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 10.0f);
		}
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
}
