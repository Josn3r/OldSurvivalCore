package club.josn3rdev.pl.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.utils.Tools;

public abstract class Menu implements Listener {
	
    Inventory _inv;

    public Menu(String name, int rows) {
        _inv = Bukkit.createInventory(null, (9 * rows), Tools.get().Text(name));
        MSRP.get().getServer().getPluginManager().registerEvents(this, MSRP.get());
    }

    public void add(ItemStack stack) {
        _inv.addItem(new ItemStack[]{stack});
    }

    public void set(int i, ItemStack stack) {
        _inv.setItem(i, stack);
    }

    public void clear() {
        _inv.clear();
    }

    public Inventory inv() {
        return _inv;
    }

    public void open(Player p) {
    	p.openInventory(_inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inv()) && event.getCurrentItem() != null && inv().contains(event.getCurrentItem()) && event.getWhoClicked() instanceof Player) {
            onClick2((Player)event.getWhoClicked(), event);
            event.setCancelled(true);
            Player p = (Player)event.getWhoClicked();
            p.setItemOnCursor(null);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inv()) && event.getPlayer() instanceof Player) {
        	onClose((Player)event.getPlayer());
        }
    }

    public abstract void onClose(Player player);

    public abstract void onClick(Player var1, ItemStack var2);
    
    public abstract void onClick2(Player var1, InventoryClickEvent event);
}

