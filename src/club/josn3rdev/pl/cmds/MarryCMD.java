package club.josn3rdev.pl.cmds;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.marry.MarryManager;
import club.josn3rdev.pl.marry.Request;
import club.josn3rdev.pl.marry.RequestManager;
import club.josn3rdev.pl.marry.RequestTypes;
import club.josn3rdev.pl.utils.Tools;

public class MarryCMD implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("marry")) {
						
			if (args.length < 1) {
				p.sendMessage(Tools.get().Text("&6&lBODAS - COMMANDS"));
				p.sendMessage(Tools.get().Text("&f/marry marry <jugador>"));
				p.sendMessage(Tools.get().Text("&f/marry divorce <jugador>"));
				p.sendMessage(Tools.get().Text("&f/marry accept <jugador>"));
				p.sendMessage(Tools.get().Text("&f/marry deny <jugador>"));
				p.sendMessage(Tools.get().Text("&f/marry togglePvP"));
				return true;
			}
			
			/*
			 * CREATE
			 */
			
			if (args[0].equalsIgnoreCase("marry")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&fUso correcto: &b/marry marry <jugador>"));
					return true;
				}
				Player receiver = Bukkit.getPlayer(args[1]);
				if (receiver == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + args[1] + " &cno está conectado."));
					return true;
				}
				MarryManager.get().checkRelationship(p, receiver, RequestTypes.MARRY);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("divorce")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&fUso correcto: &b/marry divorce <jugador>"));
					return true;
				}
				Player receiver = Bukkit.getPlayer(args[1]);
				if (receiver == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + args[1] + " &cno está conectado."));
					return true;
				}
				MarryManager.get().checkRelationship(p, receiver, RequestTypes.DIVORCE);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("accept")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&fUso correcto: &b/marry accept <jugador>"));
					return true;
				}
				
				Player requester = Bukkit.getPlayer(args[1]);
				if (requester == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + args[1] + " &cno está conectado."));
					return true;
				}
				
				UUID receiverUUID = p.getUniqueId();
			    UUID requesterUUID = requester.getUniqueId();
			    
			    if (!RequestManager.checkRequest(requesterUUID, receiverUUID))
			    	return false; 
			    Request request = RequestManager.getRequest(requesterUUID, receiverUUID);
			    
			    if (MarryManager.get().getRelationship(receiverUUID) != null && request.getType().equals(RequestTypes.MARRY)) {
			    	p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo te puedes casar con más de una persona!"));
					return true;
			    } 
			    if (MarryManager.get().getRelationship(requesterUUID) != null && request.getType().equals(RequestTypes.MARRY)) {
			    	p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse jugador no se puede casar con más de una persona!"));
			    	return true;
			    } 
			    
			    RequestManager.removeRequest(request);
			    switch (request.getType()) {
			    	case MARRY:
			    		MarryManager.get().addRelationship(requester, p);
			    		MarryManager.get().announceMarriage(receiverUUID, requesterUUID);
			       		break;
			    	case DIVORCE:
			    		MarryManager.get().removeRelationship(requester, p);
			    		MarryManager.get().announceDivorce(receiverUUID, requesterUUID);
			    		break;
			    } 
			    return true;
			}
			
			if (args[0].equalsIgnoreCase("deny")) {
				if (args.length < 2) {
					p.sendMessage(Tools.get().Text("&fUso correcto: &b/marry deny <jugador>"));
					return true;
				}
				
				Player requester = Bukkit.getPlayer(args[1]);
				if (requester == null) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + args[1] + " &cno está conectado."));
					return true;
				}
				
			    if (!RequestManager.checkRequest(requester.getUniqueId(), p.getUniqueId()))
			    	return true; 
			   
			    Request request = RequestManager.getRequest(requester.getUniqueId(), p.getUniqueId());
			    RequestManager.removeRequest(request);
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cRechazaste la solicitud de &e" + requester.getName()));
			    if (requester.isOnline())
			    	requester.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEl jugador &e" + p.getName() + " &crechazó tu solicitud de " + (request.getType() == RequestTypes.MARRY ? "boda" : "divocio") + "."));
			    
			    return true;
			}
							
			if (args[0].equalsIgnoreCase("togglePvP")) {
				MarryManager.get().togglePvP(p.getUniqueId());
				return true;
			}
			
			return true;
		}
				
		return false;
	}

}
