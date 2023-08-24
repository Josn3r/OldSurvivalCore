package club.josn3rdev.pl.controller.bank;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.utils.Tools;

public class RequestManager implements Listener {
	
	private static RequestManager ins;
	
	public Map<Player, Request> request = new HashMap<Player, Request>();
	
	public RequestManager() {
		ins = this;
	}
	
	public void register (JavaPlugin java) {
		Bukkit.getPluginManager().registerEvents(this, (Plugin)java);
	}
	
	public static RequestManager get() {
		if (ins == null) {
			ins = new RequestManager();
		}
		return ins;
	}
	
	public String Text (String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public void sendRequest (Player player, Player player2) {
		if (!this.request.containsKey(player2)) {
			if (!Bank.get().isOwnerPartnerBank(player2)) {
				this.request.put(player2, new Request(player2));
				
				player.sendMessage(" ");
				for (String str : MSRP.lang.getStringList("lang.messages.banks.bank-owner.invite-partners.send-invite")) {
					str = str.replace("<PLAYER>", player2.getName());
					player.sendMessage(Text(str));
				}
				player.sendMessage(" ");
				
				player2.sendMessage(" ");
				for (String str : MSRP.lang.getStringList("lang.messages.banks.bank-owner.invite-partners.receive-invite")) {
					str = str.replace("<PLAYER>", player.getName());
					Tools.get().sendClickeableMessage(player2, Text(str), "/openbank acceptPartner");
				}
				player2.sendMessage(" ");
				
				RequestTime.requestTimer(player2);
			} else {
				player.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partners.invite-error.player-with-partner")));
			}
		} else {
			player.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partners.invite-error.player-with-invitation-pending")));
		}
	}
	
	public Request getRequest (Player player) {
		return this.request.get(player);
	}
	
	public void declined (Player player) {
		this.request.remove(player).decline();
	}
	
	public void timeOut (Player player) {
		this.request.remove(player);
	}
	
}
