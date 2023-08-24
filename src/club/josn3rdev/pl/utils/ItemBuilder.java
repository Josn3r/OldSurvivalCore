package club.josn3rdev.pl.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
	
	@SuppressWarnings("deprecation")
	public static ItemStack crearCabeza(String owner, String name, String ... loreOptions) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        String[] arrstring = loreOptions;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String b = arrstring[n2];
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
            ++n2;
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    @SuppressWarnings("deprecation")
	public static ItemStack crearCabeza(String owner, String name, Integer i, String ... lore) {
    	ItemStack item = new ItemStack(Material.PLAYER_HEAD, i);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        String[] arrstring = lore;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String b = arrstring[n2];
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
            ++n2;
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    @SuppressWarnings("deprecation")
	public static ItemStack crearCabeza(String owner, String name, List<String> lore) {        
    	ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

	public static ItemStack crearItem(Material mat, int amount) {
    	ItemStack item = new ItemStack(mat, amount);
        return item;
    }
    
    public static ItemStack crearItem(Material mat, int amount, String name, String ... lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        String[] arrstring = lore;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String b = arrstring[n2];
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
            ++n2;
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack crearItem(Material mat, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack crearItem(Material mat, int amount, boolean enchant, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> color = new ArrayList<String>();
        for (String b : lore) {
            color.add(ChatColor.translateAlternateColorCodes((char)'&', (String)b));
            meta.setLore(color);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        return item;
    }
    
    /**
     * 
     * 
     */

    public static ItemStack parseItem(List<String> item) {
        if (item.size() < 2) {
            return null;
        }
        
        ItemStack itemStack = null;

        try {
        	itemStack = new ItemStack(Material.getMaterial(item.get(0).toUpperCase()), Integer.parseInt(item.get(1)));
        	     
        	if (item.size() > 2) {
            	for (int x = 2; x < item.size(); x++) {
            		if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            			EnchantmentStorageMeta meta = (EnchantmentStorageMeta)itemStack.getItemMeta();
            			meta.addStoredEnchant(getEnchant(item.get(x).split(":")[0]), Integer.parseInt(item.get(x).split(":")[1]), true);
                		itemStack.setItemMeta(meta);
            		} else {
            			ItemMeta meta = (ItemMeta)itemStack.getItemMeta();
                        meta.addEnchant(getEnchant(item.get(x).split(":")[0]), Integer.parseInt(item.get(x).split(":")[1]), true);
                		itemStack.setItemMeta(meta);
            		}
            	}
        	}
        	
        } catch (Exception ignored) {
        	
        }
        return itemStack;
    }
    
    private static Enchantment getEnchant(String enchant) {
    	enchant = enchant.toLowerCase();
    	switch (enchant) {
    	case "protection": return Enchantment.PROTECTION_ENVIRONMENTAL;
    	case "projectileprotection": return Enchantment.PROTECTION_PROJECTILE;
    	case "fireprotection": return Enchantment.PROTECTION_FIRE;
    	case "featherfall": return Enchantment.PROTECTION_FALL;
    	case "blastprotection": return Enchantment.PROTECTION_EXPLOSIONS;
    	case "respiration": return Enchantment.OXYGEN;
    	case "aquaaffinity": return Enchantment.WATER_WORKER;
    	case "sharpness": return Enchantment.DAMAGE_ALL;
    	case "smite": return Enchantment.DAMAGE_UNDEAD;
    	case "baneofarthropods": return Enchantment.DAMAGE_ARTHROPODS;
    	case "knockback": return Enchantment.KNOCKBACK;
    	case "fireaspect": return Enchantment.FIRE_ASPECT;
    	case "looting": return Enchantment.LOOT_BONUS_MOBS;
    	case "power": return Enchantment.ARROW_DAMAGE;
    	case "punch": return Enchantment.ARROW_KNOCKBACK;
    	case "flame": return Enchantment.ARROW_FIRE;
    	case "infinity": return Enchantment.ARROW_INFINITE;
    	case "efficiency": return Enchantment.DIG_SPEED;
    	case "silktouch": return Enchantment.SILK_TOUCH;
    	case "unbreaking": return Enchantment.DURABILITY;
    	case "fortune": return Enchantment.LOOT_BONUS_BLOCKS;
    	case "luckofthesea": return Enchantment.LUCK;
    	case "luck": return Enchantment.LUCK;
    	case "lure": return Enchantment.LURE;
    	case "thorns": return Enchantment.THORNS;
    	default: return null;	
    	}
    }
    
}

