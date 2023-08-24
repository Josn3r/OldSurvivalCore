package club.josn3rdev.pl.menu.protections;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.protections.ProtectionManager;
import club.josn3rdev.pl.protections.Protections;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class ProtectionSelection extends Menu {
		
	public ProtectionSelection(Player p) { super("Protecciones", 3);
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		Integer slot = 10;
		for (String str : sp.getProtectionOwner()) {
			Protections prote = ProtectionManager.get().getProtection(str);
			
			Double cost = (1450.00 * prote.getSize());
			Double tax = (cost/10);
			
			set(slot, ItemBuilder.crearItem(Material.GOLD_ORE, 1, "&e&l"+prote.getUUID(), 
					"&7Ubicación " + Tools.get().setLocToStringBlock(prote.getLoc()), 
					"", 
					"&fTamaño: &e" + prote.getSize() + " x " + prote.getSize(),
					"&fDueño: &e" + prote.getOwner().getName(), 
					"&fMiembros: &e" + prote.getMembersList(), 
					"&fPoder: &a" + prote.getProtectionPower(),
					"&fImpuestos: &6&l$&e" + Tools.get().formatMoney(tax), 
					"",
					"&e» Click para configurar."));
			
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
				
		//
					
		if (name.equalsIgnoreCase(Tools.get().Text("&a+1"))) {
			
		}
		
		Tools.get().playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
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
