package club.josn3rdev.pl.menu.bank.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.controller.bank.RequestManager;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.menu.bank.BankConfig;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BankPartners extends Menu {
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankPartners(Player p) { super("&lBank Partners", 6);
		// NAME : RANK	
		Integer slot = 0;	
		if (slot <= 45) {
			createPartnerIcon(slot);
		}
		++slot;
				
		set(48, ItemBuilder.crearItem(Material.NAME_TAG, 1, "&eInvitar"));
		set(50, ItemBuilder.crearItem(Material.GREEN_WOOL, 1, "&aGuardar"));
	}

	public void createPartnerIcon(Integer slot) {
		for (String str : Bank.get().getBankPartnersList()) {
			String name = str.split(" : ")[1];
			String rank = str.split(" : ")[2];
			String percent = str.split(" : ")[3];
			
			if (rank.equals("OWNER")) {
				set(slot, ItemBuilder.crearCabeza(name, "&a" + name, 
						"&7- &ePuesto: &a&l" + Bank.get().getBankRank(rank),
						"&7- &ePorcentaje: &f" + percent + "%",
						"&7&m------------------",
						"&7- &eGanancias: &6&l$&f" + Tools.get().formatMoney(Bank.get().getOwnerProfit(Double.valueOf(percent))) + ""));
			} else if (rank.equals("PARTNER")) {
				set(slot, ItemBuilder.crearCabeza(name, "&e" + name, 
						"&7- &ePuesto: &f" + Bank.get().getBankRank(rank),
						"&7- &ePorcentaje: &f" + percent + "%",
						"&7&m------------------",
						"&7- &eGanancias: &6&l$&f" + Tools.get().formatMoney(Bank.get().getOwnerProfit(Double.valueOf(percent))) + ""));
			}
			++slot;
		}
	}
	
	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
				
		for (String partners : Bank.get().getBankPartnersList()) {
			String nick = partners.split(" : ")[1];
			String rank = partners.split(" : ")[2];
			
			if (name.contains(nick)) {
				if (rank.equalsIgnoreCase("OWNER")) {
					if (!Bank.get().getBankRank(p).equalsIgnoreCase(MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER"))) {
						return;
					}
				}
				if (!Bank.get().getBankRank(p).equalsIgnoreCase(MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER"))) {
					return;
				}
				
				new BankPartners_Options(p, partners).open(p);
			}
		}
		
		if (name.equalsIgnoreCase(Text("&eInvitar"))) {
			p.closeInventory();
			if (!Bank.get().getOwner().getUniqueId().toString().equals(p.getUniqueId().toString())) {
				return;
			}
			
			if (Bank.get().getBankPartners().size() >= 4) {
				return;
			}
			createInvitation(p);
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
	
	public void createInvitation (Player p) {
		p.closeInventory();
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BankPartners(p).open(p);
				}
			}, 5L);
		})
		.onComplete((pl, text) -> {
			
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			
			if (text.isEmpty()) {
				return AnvilGUI.Response.text("Empty!");
			}
			
			if (text.equalsIgnoreCase(p.getName())) {
				return AnvilGUI.Response.text("Error #111");
			}
			
			Player player = Bukkit.getPlayer(text);
			if (player == null) {
				return AnvilGUI.Response.text("Jugador no conectado.");
			}
				
			RequestManager.get().sendRequest(p, player);			
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.SUNFLOWER))
		.title(Text(" "))
		.plugin(MSRP.get())
		.open(p);
	}
}
