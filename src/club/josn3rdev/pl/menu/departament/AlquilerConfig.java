package club.josn3rdev.pl.menu.departament;

import java.util.List;
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

public class AlquilerConfig extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	private String edifName = null;
	private String rental = null;
	
	public AlquilerConfig(Player p, String edifName, String renta) { super("Configuraci�n de Alquiler", 4);
		
		this.edifName = edifName;
		this.rental = renta;
		
		List<String> members = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + renta + ".Rented");
		
		Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".rentals." + renta + ".rental-cost");
		Integer time = MSRP.departament.getInt("departaments.edificios." + edifName + ".rentals." + renta + ".rental-time");
			
		set(13, ItemBuilder.crearItem(Material.PAPER, 1, "&6&l" + renta, 
				"&aEres miembro de este alquiler.", 
				" ",
				"&fEdificio: &e" + edifName,
				"&fCosto de Alquiler: &e" + Tools.get().formatMoney(price), 
				"&fTiempo Alquilado: &e" + Tools.get().getFormatTime(time),
				"",
				"&fMiembros: " + members.size()));
		
		set(21, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&eInvitar", 
				"&7Puedes invitar a dos amigos a tu alquiler",
				"&7y que los gastos se dividan entre todos!"));
		
		set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&cCancelar Alquiler", 
				"&7�Quieres dejar de alquilar esta habitaci�n?",
				"&f� Click para cancelar el alquiler.",
				""));
	
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		if (e.getClick() == ClickType.SHIFT_LEFT) {
			if (name.equalsIgnoreCase(Text("&eVenta Autom�tica"))) {
				editVentaOption(p, this.edifName, this.rental);
			}
		} else {
			if (name.equalsIgnoreCase(Text("&eVenta Autom�tica"))) {
				Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
				Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
				
				if (!isSelling) {
					MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", true);
				} else {
					MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", false);
				}
				
				isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
				
				set(23, ItemBuilder.crearItem(Material.BOOK, 1, "&eVenta Autom�tica", 
						"&7�Quieres vender el edificio?",
						"&f� Shift + Click para modificar el precio", 
						"&f� Click para poner en venta",
						"",
						"&fPrecio: &6&l$&e" + Tools.get().formatMoney(price),
						"&fVendiendo: " + (isSelling ? "&aSi" : "&cNo")));
			}
			
			if (name.equalsIgnoreCase(Text("&eControl de Precios"))) {
				editAlquilerOption(p, this.edifName, this.rental);
			}
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public void editVentaOption (Player p, String edifName, String rental) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new AlquilerConfig(p, edifName, rental).open(p);
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
	
	public void editAlquilerOption (Player p, String edifName, String rental) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new AlquilerConfig(p, edifName, rental).open(p);
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
