package club.josn3rdev.pl.menu.departament;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.departaments.Departament;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BuildingConfig extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	private String edifName = null;
	
	public BuildingConfig(Player p, String edifName) { super("Configuración de Edificio", 4);
		
		this.edifName = edifName;
		Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
		Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
    		
		set(13, ItemBuilder.crearItem(Material.PAPER, 1, "&6&l" + edifName, "&aEres dueño de este edificio.", " ",
				"&7Ingresos total obtenidos de alquileres.",
				"&7Esta lista se reinicia en", "&7cada reinicio de la modalidad.",
				"&fIngresos obtenidos: &6&l$&e34.875"));
		
		Double alquileres = 0.0D;
		for (String rentas : Departament.get().getRentals().get(edifName)) {
			alquileres += MSRP.departament.getDouble("departaments.edificios." + edifName + ".rentals." + rentas + ".rental-cost");
		}
		alquileres = alquileres / Departament.get().getRentals().get(edifName).size();
		
		set(21, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eControl de Precios", "&7Costo de Alquileres: &6$" + Tools.get().formatMoney(alquileres)));
		
		set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&eVenta Automática", 
				"&7¿Quieres vender el edificio?",
				"&f» Shift + Click para modificar el precio", 
				"&f» Click para poner en venta",
				"",
				"&fPrecio: &6&l$&e" + Tools.get().formatMoney(price),
				"&fVendiendo: " + (isSelling ? "&aSi" : "&cNo")));
	
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		if (e.getClick() == ClickType.SHIFT_LEFT) {
			if (name.equalsIgnoreCase(Text("&eVenta Automática"))) {
				editVentaOption(p, this.edifName);
			}
		} else {
			if (name.equalsIgnoreCase(Text("&eVenta Automática"))) {
				Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
				Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
				
				if (!isSelling) {
					MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", true);
				} else {
					MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", false);
				}
				
				isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
				
				set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&eVenta Automática", 
						"&7¿Quieres vender el edificio?",
						"&f» Shift + Click para modificar el precio", 
						"&f» Click para poner en venta",
						"",
						"&fPrecio: &6&l$&e" + Tools.get().formatMoney(price),
						"&fVendiendo: " + (isSelling ? "&aSi" : "&cNo")));
			}
			
			if (name.equalsIgnoreCase(Text("&eControl de Precios"))) {
				editAlquilerOption(p, this.edifName);
			}
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public void editVentaOption (Player p, String edifName) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BuildingConfig(p, edifName).open(p);
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
			
			if (!match.matches()) {
				return AnvilGUI.Response.text("Only number");
			}
			
			Double precio = Double.valueOf(tcode);
			if (precio > 30000000.0) {
				precio = 30000000.0;
			}
			if (precio < 17500000.0) {
				precio = 17500000.0;
			}
			
			MSRP.departament.set("departaments.edificios." + edifName + ".sellPrice", precio);
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.PAPER))
		.title(Tools.get().Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
	
	public void editAlquilerOption (Player p, String edifName) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BuildingConfig(p, edifName).open(p);
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
			
			if (!match.matches()) {
				return AnvilGUI.Response.text("Only number");
			}
			
			Double precio = Double.valueOf(tcode);
			if (precio > 7500.0) {
				precio = 7500.0;
			}
			if (precio < 1250.0) {
				precio = 1250.0;
			}
			
			for (String rentas : Departament.get().getRentals().get(edifName)) {
				MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentas + ".rental-cost", precio);
			}
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.PAPER))
		.title(Tools.get().Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
}
