package club.josn3rdev.pl.marry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.utils.Tools;

public class RequestManager {
	private static List<Request> requests = new ArrayList<>();
  
	public static void addRequests(Request request) {
		requests.add(request);
	}
  
	public static Request getRequest(UUID senderUUID, UUID receiverUUID) {
		if (requests.isEmpty())
			return null; 
		for (Request request : requests) {
			if (request.getSender() == senderUUID && request.getReceiver() == receiverUUID)
				return request; 
		} 
		return null;
	}
  
	public static boolean checkRequest(UUID senderUUID, UUID receiverUUID) {
		if (requests.isEmpty()) {
			Bukkit.getPlayer(receiverUUID).sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo hay solicitudes en curso!"));
			return false;
		} 
		for (Request request : requests) {
			if (request.getSender() == senderUUID && request.getReceiver() == receiverUUID)
				return true; 
		} 
		Bukkit.getPlayer(receiverUUID).sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes una solicitud con este jugador!"));
		return false;
	}
  
	public static void removeRequest(Request request) {
		if (requests.isEmpty())
			return; 
		requests.remove(request);
	}
  
	public static List<Request> getRequests() {
		return requests;
	}
	
}
