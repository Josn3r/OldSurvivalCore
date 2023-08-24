package club.josn3rdev.pl.controller.bank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;

public class Request {
	
	private Player recipient;
	
	public Request (Player player) {
		this.recipient = player;
	}
	
	public String Text (String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	void decline() {
		Player owner = Bank.get().getOwner();
		this.recipient.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partner.reject-invite.invite-player")).replace("<PLAYER>", owner.getName()));
		owner.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partner.reject-invite.message-owner")).replace("<PLAYER>", recipient.getName()));
	}
	
}
