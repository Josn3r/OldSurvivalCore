
package club.josn3rdev.pl.hooks;

import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.clanes.ClanesManager;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIFork extends PlaceholderExpansion {
	
	private MSRP plugin;
	  
	public PlaceholderAPIFork(MSRP plugin) {
		this.plugin = plugin;
	}
	  
	public boolean persist() {
	    return true;
	}
	  
	public boolean canRegister() {
	    return true;
	}
	  
	public String getAuthor() {
	    return "Josn3r";
	}
	  
	public String getIdentifier() {
	    return "msrp";
	}
	  
	public String getVersion() {
	    return this.plugin.getDescription().getVersion();
	}
	  
	public String onPlaceholderRequest(Player player, String identifier) {
		SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
		
		if (identifier.equals("job1")) {
			return ""+(sp.getJob1() == null ? "Ninguno" : MSRP.lang.getString("messages.jobs." + sp.getJob1().name().toLowerCase() + ".displayname"));
		} 
		if (identifier.equals("job2")) {
			return ""+(sp.getJob2() == null ? "Ninguno" : MSRP.lang.getString("messages.jobs." + sp.getJob2().name().toLowerCase() + ".displayname"));
		} 
		
		if (identifier.equals("clan_name")) {
			return ""+(ClanesManager.get().getPlayerClan(player) == null ? "" : ClanesManager.get().getPlayerClan(player).getClanName());
		}
		if (identifier.equals("clan_tag")) {
			return ""+(ClanesManager.get().getPlayerClan(player) == null ? "" : ClanesManager.get().getPlayerClan(player).getClanTag());
		}
		return null;
	}
}