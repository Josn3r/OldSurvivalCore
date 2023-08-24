package club.josn3rdev.pl.menu.protections;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class ProtectionMenu extends Menu {
	
	private Integer proteSize = 10;
		
		public ProtectionMenu(Player p) { super("Protecciones", 3);
		
		Integer size = proteSize;
		Double cost = (1450.00 * proteSize);
		Double tax = (cost/10);
		
		set(0, ItemBuilder.crearItem(Material.RED_WOOL, 1, "&cCancelar", "&7Click aquí para cancelar", "&7Click aquí para renunciar al trabajo."));
		
		set(11, ItemBuilder.crearItem(Material.LIME_DYE, 5, "&c-5", "&7Resta &c-5 &7al tamaño."));
		set(12, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&c-1", "&7Resta &c-1 &7al tamaño."));
		
		set(13, ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&6Proteccion: &fx"+size, 				
				"&7Adquiere tu bloque de protección", 
				" ", 
				"&eInformación:", 
				"&7» Radio de protección: " + size + "x" + size, 
				"&7» Costo de protección: &6&l$&e" + Tools.get().formatMoney(cost), 
				"&7» Costo de impuesto inicial: &4&l$&c" + Tools.get().formatMoney(tax), 
				" ", 
				"&a» Click aquí para comprar."));
		
		set(14, ItemBuilder.crearItem(Material.LIME_DYE, 1, "&a+1", "&7Suma &a+1 &7al tamaño."));
		set(15, ItemBuilder.crearItem(Material.LIME_DYE, 5, "&a+5", "&7Suma &a+5 &7al tamaño."));
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		Integer size = proteSize;
		Double cost = (1450.00 * proteSize);
		Double tax = (cost/10);
		
		//
		
		if (name.equalsIgnoreCase(Tools.get().Text("&cCancelar"))) {
			p.closeInventory();
			return;
		}
		if (name.equalsIgnoreCase(Tools.get().Text("&6Proteccion: &fx"+size))) {
			p.closeInventory();
			
			if (Vault.getMoney(p) < cost) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes el dinero suficiente para adquirir esa protección."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
				return;
			}
			
			if (hasAvaliableSlot(p, 1)) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cDebes por lo menos tener 1 espacio disponible en tu inventario!"));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
				return;
			}
			
			Vault.removeMoney(p, cost);
			p.getInventory().addItem(ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&6Proteccion: &fx" + size, 
					"&7Bloque de protección.",
					" ",
					"&fTamaño de Protección: &e" + size + "x" + size,
					"&fCosto de Impuestos: &6&l$&e" + Tools.get().formatMoney(tax),
					""));
			
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fCompraste una &6Protección &fx" + size + " &fpor un valor de &6&l$&e" + Tools.get().formatMoney(cost) + "&f."));
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fEl pago de impuesto de esta protección de es &6&l$&e" + Tools.get().formatMoney(tax) + "&f."));
			Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
			return;
		}
			
		if (name.equalsIgnoreCase(Tools.get().Text("&a+1"))) {
			this.proteSize += 1;
			if (this.proteSize > 128) {
				this.proteSize = 128;
			}
		}
		if (name.equalsIgnoreCase(Tools.get().Text("&c-1"))) {
			this.proteSize -= 1;
			if (this.proteSize < 10) {
				this.proteSize = 10;
			}
		}
		
		if (name.equalsIgnoreCase(Tools.get().Text("&a+5"))) {
			this.proteSize += 5;
			if (this.proteSize > 128) {
				this.proteSize = 128;
			}
		}
		if (name.equalsIgnoreCase(Tools.get().Text("&c-5"))) {
			this.proteSize -= 5;
			if (this.proteSize < 10) {
				this.proteSize = 10;
			}
		}
		
		Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		updateMenu(p);		
		
	}
	
	private void updateMenu(Player p) {
		
		Integer size = proteSize;
		Double cost = (1450.00 * proteSize);
		Double tax = (cost/10);
		
		set(13, ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&6Proteccion: &fx"+size, 				
				"&7Adquiere tu bloque de protección", 
				" ", 
				"&eInformación:", 
				"&7» Radio de protección: " + size + "x" + size, 
				"&7» Costo de protección: &6&l$&e" + Tools.get().formatMoney(cost), 
				"&7» Costo de impuesto inicial: &4&l$&c" + Tools.get().formatMoney(tax), 
				" ", 
				"&a» Click aquí para comprar."));
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public boolean hasAvaliableSlot(Player player,int howmanyclear){
		Inventory inv = player.getInventory();
		Integer check = 0;
		for (ItemStack item: inv.getContents()) {
			if(item == null) {
				check++;
			}
		}
		
		if (check > howmanyclear){
			return true;
		} else {
			return false;
		}
	}
}
