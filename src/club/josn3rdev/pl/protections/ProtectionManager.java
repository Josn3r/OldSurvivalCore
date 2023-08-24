package club.josn3rdev.pl.protections;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.clanes.ClanesManager;
import club.josn3rdev.pl.database.Stats;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class ProtectionManager {
	
    private static ProtectionManager playerManager;
    private Map<String, Protections> protections = new HashMap<String, Protections>();
    
    public static ProtectionManager get() {
        if (playerManager == null) {
            playerManager = new ProtectionManager();
        }
        return playerManager;
    }

    //
    
    public void loadProtections() {
    	for (String proteUUID : MSRP.get().getDatabase().getProtectionsData()) {
    		MSRP.debug("Loading protection UUID = " + proteUUID);
    		
    		String ownerName = MSRP.get().getDatabase().getProtectionOwnerName(proteUUID);
    		String ownerUUID = MSRP.get().getDatabase().getProtectionOwnerUUID(proteUUID);
    		
    		String membersLoad = MSRP.get().getDatabase().getProtectionMembers(proteUUID);
    		String centerLoc = MSRP.get().getDatabase().getProtectionCenterLoc(proteUUID);
    		
    		Integer size = MSRP.get().getDatabase().getProtectionSize(proteUUID);
    		Integer power = MSRP.get().getDatabase().getProtectionPower(proteUUID);
    		
    		Selection selection = new Selection(Tools.get().setStringToLocBlock(centerLoc).getBlock(), size);
    		selection.getCenterPoint().setType(Material.GOLD_ORE);
    		
    		byte[] transactions64 = Base64.getDecoder().decode(membersLoad);
    		String transactionsArray = new String(transactions64);
    		
    		ArrayList<String> membersList = new ArrayList<String>();
			if (transactionsArray.contains(" /// ")) {
    			for (String str : transactionsArray.split(" /// ")) {
    				membersList.add(str);
    			}
    		}
    		Protections prote = new Protections(proteUUID, ownerName, ownerUUID, membersList, power, selection);
    		protections.put(proteUUID, prote);
    	}
    }
    
    public void registerNewProtection (Player p, Selection selection) {
    	SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
    	
    	String proteUUID = Tools.get().getAlphaNumericString(8);
    	ArrayList<String> members = new ArrayList<String>();
    	 
    	Protections prote = new Protections(proteUUID, p, members, 40, selection);
    	protections.put(proteUUID, prote);
    	
    	sp.getProtectionOwner().add(prote.getUUID());
    	
    	String protectionMembers64 = "";
		protectionMembers64 = Base64.getEncoder().encodeToString("".getBytes());
		
		MSRP.get().getDatabase().saveProtectionData(prote.getUUID(), prote.getOwner().getName(), prote.getOwnerUUID().toString(), protectionMembers64, Tools.get().setLocToStringBlock(prote.getLoc()), prote.getSize(), prote.getProtectionPower());
		Stats.get().savePlayer(p);
    }
    
    public void saveProtection (String protectionUUID) {
    	Protections prote = getProtection(protectionUUID);
    	
    	String protectionMembers64 = "";
		String proteMembers = "";
		if (!prote.getMembers().isEmpty()) {
			for (String str : prote.getMembers()) {
				proteMembers = proteMembers + str + " /// ";
			}
			proteMembers = proteMembers.substring(0, proteMembers.length() - 5);
		}
		protectionMembers64 = Base64.getEncoder().encodeToString(proteMembers.toString().getBytes());  
		
    	MSRP.get().getDatabase().saveProtectionData(prote.getUUID(), prote.getOwner().getName(), prote.getOwnerUUID().toString(), protectionMembers64, Tools.get().setLocToStringBlock(prote.getLoc()), prote.getSize(), prote.getProtectionPower());
	}
    
    public void removeProtection (String protectionUUID) {
    	protections.remove(protectionUUID);
    	MSRP.get().getDatabase().deleteProtectionData(protectionUUID);
    }
    
    //
    
	public Map<String, Protections> getProtections() {
		return protections;
	}
	
	public Protections getProtection(String protectionUUID) {
        return this.protections.get(protectionUUID);
    }
    
	public Set<Protections> ProtectionsSet() {
        HashSet<Protections> hashSet = new HashSet<Protections>();
        for (String protectionUUID : getProtections().keySet()) {
            hashSet.add(this.getProtection(protectionUUID));
        }
        return hashSet;
    }
    
	public Boolean checkRadiusProtection(Location loc, Integer radius) {
		Selection selection = new Selection(loc.getBlock(), radius);    	
		if (regionOverlaps(selection)) {
    		return true;
    	} 
    	if (ClanesManager.get().regionOverlaps(selection)) {
    		return true;
    	}
		return false;
	}
		
	public boolean regionOverlaps(Selection selection) {
		for (Protections region : ProtectionsSet()) {
			if (region.overlaps(selection))
				return true; 
		} 
		return false;
	}
	  
	public Protections getOverlap(Selection selection) {
		for (Protections region : ProtectionsSet()) {
			if (region.overlaps(selection))
				return region; 
		} 
		return null;
	}
	
	public Integer maxProtections (Player p) {
		if (p.hasPermission("msrp.protections.max.8")) {
			return 8;
    	} else if (p.hasPermission("msrp.protections.max.7")) {
    		return 7;
    	} else if (p.hasPermission("msrp.protections.max.6")) {
    		return 6;
    	} else if (p.hasPermission("msrp.protections.max.5")) {
    		return 5;
    	} else if (p.hasPermission("msrp.protections.max.4")) {
    		return 4;
    	} else if (p.hasPermission("msrp.protections.max.3")) {
    		return 3;
    	}
		return 2;
	}
}