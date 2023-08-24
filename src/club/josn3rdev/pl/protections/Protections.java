package club.josn3rdev.pl.protections;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Protections {

	private String uuid;
	private Location loc = null;
	private Integer size = 10;
	
	private Integer protectionPower = 40;
		
	private OfflinePlayer owner = null;
	private UUID ownerUUID = null;
	private ArrayList<String> members = null;
	private final Selection selection;
		
	public Protections(String protectionUUID, Player owner, ArrayList<String> members, Integer protectionPower, Selection selection) {
		this.uuid = protectionUUID;
		
		this.setOwner(owner);
		this.ownerUUID = owner.getUniqueId();
		this.members = members;
		
		this.loc = selection.getCenterPoint().getLocation();
		this.size = selection.getRadius();
		this.selection = selection;
	}
	
	public Protections(String protectionUUID, String owner, String ownerUUID, ArrayList<String> members, Integer protectionPower, Selection selection) {
		this.uuid = protectionUUID;
		
		this.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)));
		this.ownerUUID = UUID.fromString(ownerUUID);
		this.members = members;
		
		this.loc = selection.getCenterPoint().getLocation();
		this.size = selection.getRadius();
		this.selection = selection;
	}
	
	public boolean overlaps(Selection comparator) {
		if (containsBlock(comparator.getCorner_one()))
			return true; 
	    if (containsBlock(comparator.getCorner_two()))
	    	return true; 
	    if (containsBlock(comparator.getCorner_three()))
	    	return true; 
	    return containsBlock(comparator.getCorner_four());
	}
	
	public boolean containsBlock(Block block) {
		if (block.getWorld() != this.loc.getWorld())
			return false; 
		if (numberInRange(block.getX(), selection.getCorner_one().getX(), selection.getCorner_four().getX()))
			return numberInRange(block.getZ(), selection.getCorner_one().getZ(), selection.getCorner_four().getZ()); 
		return false;
	}
	
	public boolean numberInRange(int target, int starting, int ending) {
	    return (target >= starting && target <= ending);
	}
	
	public String getUUID() {
		return uuid;
	}

	public Location getLoc() {
		return loc;
	}

	public Integer getSize() {
		return size;
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}
	
	public OfflinePlayer getOwner() {
		return owner;
	}

	public void setOwner(OfflinePlayer offlinePlayer) {
		this.owner = offlinePlayer;
	}

	public ArrayList<String> getMembers() {
		return members;
	}
	
	public String getMembersList() {
		String str = "";
		for (String list : getMembers()) {
			String name = list.split(" : ")[1];
			str = str + name + ", ";
		}
		if (str.contains(", ")) {
			str = str.substring(0, str.length() - 2);
		}		
		return str;
	}
	
	public Selection getSelection() {
		return this.selection;
	}
	
	//
	
	public boolean equals(Object o) {
		if (this == o)
			return true; 
		if (!(o instanceof Protections))
			return false; 
		Protections region = (Protections)o;
		return getOwner().equals(region.getOwner());
	}
	
	public boolean containsPlayer(Player player) {
	    return containsBlock(player.getWorld().getBlockAt(player.getLocation()));
	}

	public Integer getProtectionPower() {
		return protectionPower;
	}

	public void setProtectionPower(Integer protectionPower) {
		this.protectionPower = protectionPower;
	}
	
}
