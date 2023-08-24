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
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.menu.bank.BankConfig;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BankBalance extends Menu {

	private String bankName;
	private Double bankBalance;
	
	private ArrayList<String> bankLore = new ArrayList<String>();
	private ArrayList<String> infoLore = new ArrayList<String>();
	
	
	private ArrayList<String> balanceLore = new ArrayList<String>();
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankBalance(Player p) { super("&lBank Balance", 6);
			
		this.bankName = MSRP.bank.getString("banks.bank-name");
		this.bankBalance = MSRP.bank.getDouble("banks.bank-balance");
		
		//
		
		this.loadDescriptions();
		
		String bName = MSRP.lang.getString("messages.menus.bank-menu.bank-balance.items.bank.displayname");
		String bBalance = MSRP.lang.getString("messages.menus.bank-menu.bank-balance.items.balance.displayname");
	    String bInfo = MSRP.lang.getString("messages.menus.bank-menu.bank-balance.items.information.displayname");
		
		String deposit = MSRP.lang.getString("messages.menus.default-bottoms.deposit-displayname");
		String withdraw = MSRP.lang.getString("messages.menus.default-bottoms.withdraw-displayname");
		
		String back = MSRP.lang.getString("messages.menus.default-bottoms.back-to-menu");
		
		
		//
		
		set(12, ItemBuilder.crearItem(Material.NAME_TAG, 1, bName, bankLore));
		
		set(14, ItemBuilder.crearItem(Material.SUNFLOWER, 1, bBalance, balanceLore));
		
		set(20, ItemBuilder.crearItem(Material.LIME_DYE, 1, deposit));
				
		set(22, ItemBuilder.crearItem(Material.BOOK, 1, bInfo, infoLore));
		
		set(24, ItemBuilder.crearItem(Material.RED_DYE, 1, withdraw));
		
		set(40, ItemBuilder.crearItem(Material.GREEN_WOOL, 1, back));
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		String deposit = MSRP.lang.getString("messages.menus.default-bottoms.deposit-displayname");
		String withdraw = MSRP.lang.getString("messages.menus.default-bottoms.withdraw-displayname");
		String back = MSRP.lang.getString("messages.menus.default-bottoms.back-to-menu");
		
		if (name.equalsIgnoreCase(Text(deposit))) {
			p.closeInventory();
			anvil(p, "Deposit");
		}
		if (name.equalsIgnoreCase(Text(withdraw))) {
			p.closeInventory();
			anvil(p, "Withdraw");
		} 
		if (name.equalsIgnoreCase(Text(back))) {
			p.closeInventory();
			new BankConfig(p).open(p);
		}
		
	}
	
	@Override
	public void onClose(Player p) {
	}
	
	private void loadDescriptions() {
		
		bankLore.clear();
		infoLore.clear();
		//
		
		String name = this.bankName;
		Double balance = this.bankBalance;
		
		Double totalMoney = Bank.get().checkTotalMoney();
		
		Double DailyProfit = Bank.get().getIngresos();
		Double gastoIntereses = Bank.get().checkGastosIntereses();
		Double DailyNetProfit = (DailyProfit - gastoIntereses);
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-balance.items.bank.description")) {
			str = str.replace("<BANK_NAME>", name)
					 .replace("<BANK_BALANCE>", "$"+Tools.get().formatMoney(balance));
			bankLore.add(str);
		}
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-balance.items.balance.description")) {
			str = str.replace("<BANK_NAME>", name)
					 .replace("<BANK_BALANCE>", "$"+Tools.get().formatMoney(balance));
			
			balanceLore.add(str);
		}
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-balance.items.information.description")) {
			str = str.replace("<BANK_NAME>", name)
					 .replace("<BANK_BALANCE>", "$"+Tools.get().formatMoney(balance))
					 
					 .replace("<BANK_ACCOUNTS_CREATED>", "0")
					 .replace("<BANK_TOTAL_MONEY>", "$"+Tools.get().formatMoney(totalMoney))
					 
					 .replace("<BANK_DAILY_PROFIT>", "$"+Tools.get().formatMoney(DailyProfit))
					 .replace("<BANK_DAILY_INTERESTS_PAY>", "$"+Tools.get().formatMoney(gastoIntereses))
					 .replace("<BANK_DAILY_NET>", "$"+Tools.get().formatMoney(DailyNetProfit));
			infoLore.add(str);
		}
				
	}
	
	public void anvil(Player p, String type) {
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BankBalance(p).open(p);
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
				return AnvilGUI.Response.text("Only Money Value");
			}
			
			
			if (Double.valueOf(text) > 1000000000000.0) {
				return AnvilGUI.Response.text("Max 1$Trillon");
			}
			
			Double finalBalance = 0.0;			
			if (type.equalsIgnoreCase("deposit")) {
				if (Vault.getMoney(p) < Double.valueOf(text)) {
					return AnvilGUI.Response.text("Error #303");
				}
				
				finalBalance = this.bankBalance + Double.valueOf(text);
				Vault.removeMoney(p, Double.valueOf(text));
			} else {
				if (Bank.get().getBankBalance() < Double.valueOf(text)) {
					return AnvilGUI.Response.text("Error #303");
				}
				
				finalBalance = this.bankBalance - Double.valueOf(text);
				Vault.setMoney(p, Double.valueOf(text));
			}
			
			//MSRP.bank.set("banks." + bank + ".bank-balance", Double.valueOf(finalBalance));
			Bank.get().setBankBalance(finalBalance);
			Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
			
			new BankBalance(p).open(p);
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.SUNFLOWER))
		.title(Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
}
