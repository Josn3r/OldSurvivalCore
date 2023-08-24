package club.josn3rdev.pl.controller.bank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;

class RequestTime {

	public static String Text (String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	static void requestTimer (final Player player) {
		MSRP.get().getServer().getScheduler().runTaskLaterAsynchronously(MSRP.get(), new Runnable() {
			@Override
			public void run() {
				
				Request request = RequestManager.get().getRequest(player);
				
				if (request != null) {
					RequestManager.get().timeOut(player);
					
					player.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partners.invite-timeout.invite-player").replace("<PLAYER>", Bank.get().getOwner().getName())));
					Bank.get().getOwner().sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partners.invite-timeout.message-owner").replace("<PLAYER>", player.getName())));
				}
			}
		 }, 300L);
	}
	
}
