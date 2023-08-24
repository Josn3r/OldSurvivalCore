package club.josn3rdev.pl.marry;

import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.utils.Tools;

public class Request {
	
	private UUID senderUUID;
	private UUID receiverUUID;
	  
	private RequestTypes requestType;
	  
	public Request(UUID senderUUID, UUID receiverUUID, RequestTypes requestType) {
		this.senderUUID = senderUUID;
	    this.receiverUUID = receiverUUID;
	    this.requestType = requestType;
	    RequestManager.addRequests(this);
	    sendOutRequest();
	}
	  
	public void sendOutRequest() {
		Player sender = Bukkit.getPlayer(this.senderUUID);
		Player receiver = Bukkit.getPlayer(this.receiverUUID);
		TextComponent playerRequest = new TextComponent(sender.getName() + " ha " + (this.requestType == RequestTypes.MARRY ? "pedido casarse contigo" : "solicitado divorciarse de usted") + "! ¿Lo aceptarás? ");
		TextComponent yes = new TextComponent(Tools.get().Text("&a&l[ACEPTO]"));
		yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Content[] { (Content)new Text(Tools.get().Text("&a&lSI, SI, SIII QUIERO")) }));
		yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry accept " + sender.getName()));
		TextComponent no = new TextComponent(Tools.get().Text("&c&l[RECHAZAR]"));
		no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Content[] { (Content)new Text(Tools.get().Text("&c&lUGH!! NOOOO")) }));
		no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry deny " + sender.getName()));
		Tools.get().sendSpigotMessageWithPrefix(receiver.getUniqueId(), new TextComponent[] { playerRequest, yes, no});
	}
	  
	  public UUID getSender() {
	    return this.senderUUID;
	  }
	  
	  public UUID getReceiver() {
	    return this.receiverUUID;
	  }
	  
	  public RequestTypes getType() {
	    return this.requestType;
	  }
	}