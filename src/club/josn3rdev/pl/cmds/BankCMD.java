package club.josn3rdev.pl.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.controller.bank.Request;
import club.josn3rdev.pl.controller.bank.RequestManager;
import club.josn3rdev.pl.menu.bank.personal.BankMenu;

public class BankCMD implements CommandExecutor {
	 
	public String Text (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("openBank")) {
			if (args.length == 0) {
				//if (Tools.get().playerIsInTheRadius(loc, p.getLocation())) {
					new BankMenu(p).open(p);
				/*} else {
					if (MS.getBank().isOwnerPartnerBank(p)) {
						new BankMenu(p).open(p);
					} else {
						p.sendMessage(Tools.get().Text("&7[&6Banco&7] » &cNo estás en la zona del banco!"));
						p.sendMessage(Tools.get().Text("&7[&6Banco&7] » &cPara abrir el menú, debes ir a &f/warp banco&c."));
						Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					}
				}*/
				return true;
			}
			
			if (args[0].equalsIgnoreCase("acceptPartner")) {
				
				Request request = RequestManager.get().getRequest(p);
				
				if (request == null) {
					p.sendMessage(Text(MSRP.lang.getString("lang.messages.banks.bank-owner.invite-partners.invite-error.no-invitation-pending")));
					return true;
				}
				
				Bank.get().addPartner(p);
				RequestManager.get().request.remove(p);
				
				Player owner = Bank.get().getOwner();
				for (String str : MSRP.lang.getStringList("lang.messages.banks.bank-owner.invite-partners.accept-invite.invite-player")) {
					str = str.replace("<PLAYER>", owner.getName())
							.replace("<BANK_NAME>", Bank.get().getBankName());
					p.sendMessage(Text(str));
				}
				
				for (String str : MSRP.lang.getStringList("lang.messages.banks.bank-owner.invite-partners.accept-invite.message-owner")) {
					str = str.replace("<PLAYER>", p.getName());
					owner.sendMessage(Text(str));
				}
				
				return true;
			}
			return true;
		}
		
		
		
		return false;
	}

}
