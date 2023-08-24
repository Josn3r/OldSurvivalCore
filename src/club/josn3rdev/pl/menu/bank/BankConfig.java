package club.josn3rdev.pl.menu.bank;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.menu.bank.config.BankBalance;
import club.josn3rdev.pl.menu.bank.config.BankComissions;
import club.josn3rdev.pl.menu.bank.config.BankPartners;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;

public class BankConfig extends Menu {

	private String bankName;
	private Double bankBalance,deposit,withdraw;
	//private Integer accionesDisponibles;
	//private Double accionesPrecio,accionesMultiplier;
	
	private ArrayList<String> bankLore = new ArrayList<String>();	
	private ArrayList<String> commissionLore = new ArrayList<String>();
	private ArrayList<String> infoLore = new ArrayList<String>();
	
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankConfig(Player p) { super("Bank Config", 5);
			
		this.bankName = MSRP.bank.getString("banks.bank-name");
		this.bankBalance = MSRP.bank.getDouble("banks.bank-balance");
				
		this.deposit = MSRP.bank.getDouble("banks.comissions.deposit");
		this.withdraw = MSRP.bank.getDouble("banks.comissions.withdraw");
		
		//this.accionesDisponibles = MSRP.bank.getInt("banks.bank-options.bank-actions.disponibles");
		//this.accionesPrecio = MSRP.bank.getDouble("banks.bank-options.bank-actions.starting-price");
		//this.accionesMultiplier = MSRP.bank.getDouble("banks.bank-options.bank-actions.multiplier-price");
			
		//
		
		this.loadDescription();
		
		String bName = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.bank.displayname");
		String bPartners = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.partners.displayname");
		String bCommission = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.commissions.displayname");
		String bInfo = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.information.displayname");
		
		String cancel = MSRP.lang.getString("messages.menus.default-bottoms.cancel-bottom");
		String accept = MSRP.lang.getString("messages.menus.default-bottoms.confirm-bottom");
		
		//
		
		set(13, ItemBuilder.crearItem(Material.PAPER, 1, bName, bankLore));		
		
		set(21, ItemBuilder.crearItem(Material.PLAYER_HEAD, 1, bPartners, Bank.get().getBankPartners()));
		set(22, ItemBuilder.crearItem(Material.REDSTONE_LAMP, 1, "&eAcciones", "&7"));
		set(23, ItemBuilder.crearItem(Material.SUNFLOWER, 1, bCommission, commissionLore));
		
		//		
		
		set(29, ItemBuilder.crearItem(Material.RED_WOOL, 1, cancel));		
		set(31, ItemBuilder.crearItem(Material.BOOK, 1, bInfo, infoLore));		
		set(33, ItemBuilder.crearItem(Material.GREEN_WOOL, 1, accept));
		
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		String bName = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.bank.displayname");
		String bPartners = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.partners.displayname");
		String bCommission = MSRP.lang.getString("messages.menus.bank-menu.bank-config.items.commissions.displayname");
		
		String cancel = MSRP.lang.getString("messages.menus.default-bottoms.cancel-bottom");
		String accept = MSRP.lang.getString("messages.menus.default-bottoms.confirm-bottom");
						
		//
		
		if (name.equalsIgnoreCase(Text(bName))) {
			if (!Bank.get().getBankRank(p).equalsIgnoreCase(MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER"))) {
				return;
			}
			new BankBalance(p).open(p);
		}
		
		if (name.equalsIgnoreCase(Text(bPartners))) {
			new BankPartners(p).open(p);
		}
		
		if (name.equalsIgnoreCase(Text(bCommission))) {
			if (!Bank.get().getBankRank(p).equalsIgnoreCase(MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER"))) {
				return;
			}
			new BankComissions(p).open(p);
		}
				
		if (name.equalsIgnoreCase(Text(cancel))) {
			p.closeInventory();
		}
		
		if (name.equalsIgnoreCase(Text(accept))) {
			p.closeInventory();
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	//
	
	private void loadDescription() {
		bankLore.clear();
		commissionLore.clear();
		infoLore.clear();
		//
		
		String name = this.bankName;
		Double balance = this.bankBalance;
		
		Double commDeposit = this.deposit;
		Double commWithdraw = this.withdraw;
		
		Double totalMoney = Bank.get().checkTotalMoney();
		
		Double DailyProfit = Bank.get().getIngresos();
		Double gastoIntereses = Bank.get().checkGastosIntereses();
		Double DailyNetProfit = (DailyProfit - gastoIntereses);
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-config.items.bank.description")) {
			str = str.replace("<BANK_NAME>", name)
					 .replace("<BANK_BALANCE>", "$"+Tools.get().formatMoney(balance)
					 .replace("<BANK_COMM_DEPOSIT>", ""+commDeposit + "%")
					 .replace("<BANK_COMM_WITHDRAW>", "" +commWithdraw + "%"));
			bankLore.add(str);
		}
		
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-config.items.commissions.description")) {
			str = str.replace("<BANK_NAME>", name)
					 .replace("<BANK_BALANCE>", "$"+Tools.get().formatMoney(balance)
					 .replace("<BANK_COMM_DEPOSIT>", ""+commDeposit + "%")
					 .replace("<BANK_COMM_WITHDRAW>", "" +commWithdraw + "%"));
			commissionLore.add(str);
		}
				
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-config.items.information.description")) {
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
}
