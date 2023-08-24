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
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.menu.bank.BankConfig;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BankComissions extends Menu {

	private Double deposit,withdraw;
	
	private ArrayList<String> depositLore = new ArrayList<String>();
	private ArrayList<String> withdrawLore = new ArrayList<String>();
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankComissions(Player p) { super("&lBank Commissions", 3);
	
		this.deposit = MSRP.bank.getDouble("banks.comissions.deposit");
		this.withdraw = MSRP.bank.getDouble("banks.comissions.withdraw");
		
		//
		
		this.loadDescriptions();
		
		String bDeposit = MSRP.lang.getString("messages.menus.bank-menu.bank-commissions.items.deposit.displayname");
		String bWithdraw = MSRP.lang.getString("messages.menus.bank-menu.bank-commissions.items.withdraw.displayname");
	    
		//
		
		set(12, ItemBuilder.crearItem(Material.PAPER, 1, bDeposit, depositLore));
		
		set(14, ItemBuilder.crearItem(Material.SUNFLOWER, 1, bWithdraw, withdrawLore));
				
		set(26, ItemBuilder.crearItem(Material.GREEN_WOOL, 1, "&aGuardar", ""));
		
	}

	private void loadDescriptions() {
		
		Double commDeposit = this.deposit;
		Double commWithdraw = this.withdraw;
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-commissions.items.deposit.description")) {
			str = str.replace("<BANK_COMM_DEPOSIT>", ""+commDeposit + "%")
					 .replace("<BANK_COMM_WITHDRAW>", "" +commWithdraw + "%");
			depositLore.add(str);
		}
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-commissions.items.withdraw.description")) {
			str = str.replace("<BANK_COMM_DEPOSIT>", ""+commDeposit + "%")
					 .replace("<BANK_COMM_WITHDRAW>", "" +commWithdraw + "%");
			withdrawLore.add(str);
		}
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		String bDeposit = MSRP.lang.getString("messages.menus.bank-menu.bank-commissions.items.deposit.displayname");
		String bWithdraw = MSRP.lang.getString("messages.menus.bank-menu.bank-commissions.items.withdraw.displayname");
		
		if (name.equalsIgnoreCase(Text(bDeposit))) {
			editOption(p, name, "deposit");
		}
		if (name.equalsIgnoreCase(Text(bWithdraw))) {
			editOption(p, name, "withdraw");
		}
		
		
		if (name.equalsIgnoreCase(Text("&aGuardar"))) {
			p.closeInventory();
			new BankConfig(p).open(p);
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	public void editOption (Player p, String name, String option) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BankComissions(p).open(p);
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
				return AnvilGUI.Response.text("Only Percent Value");
			}
						
			if (Double.valueOf(text) < 0.0D) {
				return AnvilGUI.Response.text(text + " is lower");
			}
			if (Double.valueOf(text) > 75.0D) {
				return AnvilGUI.Response.text(text + " is higher");
			}
			
			MSRP.bank.set("banks.comissions." + option, Double.valueOf(text));
			new BankComissions(p).open(p);
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.SUNFLOWER))
		.title(Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
}
