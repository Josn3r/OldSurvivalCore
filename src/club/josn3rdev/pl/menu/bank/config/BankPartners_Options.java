package club.josn3rdev.pl.menu.bank.config;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BankPartners_Options extends Menu {

	private String partner;
	private Double percent;
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankPartners_Options(Player p, String partner) { super("&lBank Partners Options", 4);		
		this.partner = partner;
		
		String name = partner.split(" : ")[1];
		String rank = partner.split(" : ")[2];
		this.percent = Double.valueOf(partner.split(" : ")[3]);
			
		set(13, ItemBuilder.crearCabeza(name, "&e"+name, "&7- Rank: &f" + rank, "&7- Percent: &f" + percent + "%"));
		
		set(21, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&ePercent", "&7Click to edit profit percent"));
		set(23, ItemBuilder.crearItem(Material.BARRIER, 1, "&eKick", "&cKick from partnership bank."));
		
		set(35, ItemBuilder.crearItem(Material.GREEN_WOOL, 1, "&aSave"));
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		if (name.equalsIgnoreCase(Text("&ePercent"))) {
			percent(p);
		}
		
		if (name.equalsIgnoreCase(Text("&eKick"))) {
			if (partner.split(" : ")[2].equalsIgnoreCase("OWNER")) {
				return;
			}
			
			Bank.get().removePartner(partner.split(" : ")[0]);
			p.closeInventory();
			new BankPartners(p).open(p);
		}
		
		if (name.equalsIgnoreCase(Text("&aSave"))) {
			p.closeInventory();
			new BankPartners(p).open(p);
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public void percent(Player p) {
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BankPartners_Options(p, partner).open(p);
				}
			}, 5L);
		})
		.onComplete((pl, text) -> {
			
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			String tcode = text;
			Pattern pattern = Pattern.compile("[0-9].*");
			Matcher match = pattern.matcher(tcode);
						
			if (!match.matches()) {
				return AnvilGUI.Response.text("Value incorrect");
			}
			
			Double value = Double.valueOf(text);
				
			if (value < 0.0 || value > 100.0) {
				return AnvilGUI.Response.text("Min 0% Max 100%");
			}
			
			if (value > percent) {
				
				Double check = (checkTotalPercents()-percent) + value;
				if (check > 100.0) {
					return AnvilGUI.Response.text("ERROR 100%");
				}
			}
			
			Bank.get().removePartner(partner.split(" : ")[0]);
			Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
			
			//							UUID				:				NAME			   :			RANK				 :		PERCENT
			String format = "" + partner.split(" : ")[0] + " : " + partner.split(" : ")[1] + " : " + partner.split(" : ")[2] + " : " + value;
			Bank.get().addPartner(format);
			this.partner = format;
			
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.SUNFLOWER))
		.title(Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
	
	
	public Double checkTotalPercents() {
		ArrayList<String> partners = Bank.get().getBankPartnersList();
		Double percents = 0.0;
		for (String str : partners) {
			Double calc = Double.valueOf(str.split(" : ")[3]);
			
			percents = percents + calc;
		}
		return percents;
	}
	
}
