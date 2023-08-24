package club.josn3rdev.pl.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import club.josn3rdev.pl.MSRP;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

@SuppressWarnings("deprecation")
public class Vault {

	public static double getMoney(Player p){
		return MSRP.econ.getBalance(p.getName());
	}
	
	public static double getStringMoney(Player p){
		return (MSRP.econ.getBalance(p.getName()));
	}

	public static void setMoney(Player p, double money){
		MSRP.econ.depositPlayer(p.getName(), money);
	}
	
	public static void setMoney(String player, double money){
		MSRP.econ.depositPlayer(player, money);
	}
	
	public static void removeMoney(Player p, double money) {
		MSRP.econ.withdrawPlayer(p.getName(), money);		
	}
		
	public static boolean setupEconomy() {
		if (MSRP.get().getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = MSRP.get().getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		MSRP.econ = ((Economy)rsp.getProvider());
		return MSRP.econ != null;
	}

	public static boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = MSRP.get().getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			MSRP.chat = chatProvider.getProvider();
		}
		return (MSRP.chat != null);
	}
}