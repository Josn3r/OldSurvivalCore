package club.josn3rdev.pl.clanes;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.protections.ProtectionManager;
import club.josn3rdev.pl.protections.Selection;
import club.josn3rdev.pl.utils.Tools;

public class ClanesManager {

	private static ClanesManager cm;
    private Map<Integer, Clanes> clanes = new HashMap<Integer, Clanes>();

    public static ClanesManager get() {
        if (cm == null) {
        	cm = new ClanesManager();
        }
        return cm;
    }
	
    public Map<Integer, Clanes> getClans() {
        return this.clanes;
    }

    public Clanes getClan(Integer clanID) {
        return this.clanes.get(clanID);
    }
    
    public boolean doesClanExists(Integer clanID) {
        return this.clanes.containsKey(clanID);
    }

    public void createClan(Integer clanID) {
        this.clanes.put(clanID, new Clanes(clanID));
    }

    public void removeClan(Integer clanID) {
        this.clanes.remove(clanID);
    }
    
    public Set<Clanes> ClanesSet(Set<Integer> set) {
        HashSet<Clanes> hashSet = new HashSet<Clanes>();
        for (Integer clanID : set) {
            hashSet.add(this.getClan(clanID));
        }
        return hashSet;
    }
    
    public Set<Clanes> ClanesSet() {
        HashSet<Clanes> hashSet = new HashSet<Clanes>();
        for (Integer clanID : getClans().keySet()) {
            hashSet.add(this.getClan(clanID));
        }
        return hashSet;
    }
    
    public boolean existsClanByName (String clanName) {
    	for (Clanes clans : ClanesSet()) {
    		if (clans.getClanName().equals(clanName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean existsClanByTag (String clanTag) {
    	for (Clanes clans : ClanesSet()) {
    		if (clans.getClanTag().equals(clanTag)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean playerHasClan (Player p) {
    	for (Clanes clans : ClanesSet()) {
    		for (String str : clans.getClanMembers()) {
    			if (str.split(" : ")[0].equals(p.getUniqueId().toString())) {
        			return true;
    			}
    		}
    	}
		return false;
    }
    
    public Clanes getPlayerClan (Player p) {
    	for (Clanes clans : ClanesSet()) {
    		for (String str : clans.getClanMembers()) {
    			if (str.split(" : ")[0].equals(p.getUniqueId().toString())) {
        			return clans;
    			}
    		}
    	}
		return null;
    }
    
    public Boolean checkRadiusProtection(Block b) {
    	Selection selection = new Selection(b, 64);    	
    	if (regionOverlaps(selection)) {
    		return true;
    	}     	
    	if (ProtectionManager.get().regionOverlaps(selection)) {
    		return true;
    	}
		return false;
	}
    
	public boolean regionOverlaps(Selection selection) {
		for (Clanes region : ClanesSet()) {
			if (region.getClanProtection() != null) {
				if (region.overlaps(selection))
					return true; 
			}
		} 
		return false;
	}
	  
	public Clanes getOverlap(Selection selection) {
		for (Clanes region : ClanesSet()) {
			if (region.getClanProtection() != null) {
				if (region.overlaps(selection))
					return region; 
			}
		} 
		return null;
	}
	
    //
    
    public void loadClans() {
    	Integer totals = MSRP.get().getDatabase().countClansDatabase();
    	MSRP.debug("&fHay un total de &e" + totals + " &fclanes registrados en la base de datos.");
    	
    	if (totals == 0) {
    		//MSRP.debug(totals + "&f == 0");
        	return;
    	}
    	
    	for (int i = 1; i <= totals; ++i) {
    		createClan(i);
    		Clanes clans = getClan(i);
    		
    		String clanName = MSRP.get().getDatabase().getClanName(i);
    		String clanTag = MSRP.get().getDatabase().getClanTag(i);
    		
    		Integer clanKills = MSRP.get().getDatabase().getClanKills(i);
    		Integer clanDeaths = MSRP.get().getDatabase().getClanDeaths(i);
    		
    		Integer clanLevel = MSRP.get().getDatabase().getClanLevel(i);
    		Integer clanExp = MSRP.get().getDatabase().getClanExp(i);
    		Integer clanTotalExp = MSRP.get().getDatabase().getClanTotalExp(i);
    		Double clanPower = MSRP.get().getDatabase().getClanPower(i);
    		
    		String clanProtection = MSRP.get().getDatabase().getClanProtection(i);
    		
    		clans.setClanName(clanName);
    		clans.setClanTag(clanTag);
    		
    		clans.setClanKills(clanKills);
    		clans.setClanDeaths(clanDeaths);
    		
    		clans.setClanLevel(clanLevel);
    		clans.setClanExp(clanExp);
    		clans.setClanTotalExp(clanTotalExp);
    		clans.setClanPower(clanPower);
    		
    		byte[] clanMembers64 = Base64.getDecoder().decode(MSRP.get().getDatabase().getClanMembers(i));
    		byte[] clanRanks64 = Base64.getDecoder().decode(MSRP.get().getDatabase().getClanRanks(i));
    		byte[] clanPowerChanges64 = Base64.getDecoder().decode(MSRP.get().getDatabase().getClanPowerChanges(i));
    		
    		String clanMembersArray = new String(clanMembers64);
    		String clanRanksArray = new String(clanRanks64);
    		String clanPowerChangesArray = new String(clanPowerChanges64);
    		
    		ArrayList<String> members = new ArrayList<String>();
			if (clanMembersArray.contains(" /// ")) {
    			for (String str : clanMembersArray.split(" /// ")) {
    				members.add(str);
    			}
    		} else {
    			members.add(clanMembersArray);
    		}
			
			ArrayList<String> ranks = new ArrayList<String>();
			if (clanRanksArray.contains(" /// ")) {
    			for (String str : clanRanksArray.split(" /// ")) {
    				ranks.add(str);
    			}
    		}
			
			ArrayList<String> clanPowerChanges = new ArrayList<String>();
			if (clanPowerChangesArray.contains(" /// ")) {
    			for (String str : clanPowerChangesArray.split(" /// ")) {
    				clanPowerChanges.add(str);
    			}
    		} else {
    			clanPowerChanges.add(clanPowerChangesArray);
    		}
			
			clans.setClanMembers(members);
			clans.setClanRanks(ranks);
			clans.setPowerChanges(clanPowerChanges);
			
    		clans.setClanProtection((clanProtection.equals("NONE") ? null: Tools.get().setStringToLocBlock(clanProtection)));
    		
    		/*MSRP.debug(" ");
        	MSRP.debug("&fLoaded &eCLAN ID &f= &6&l" + i);
        	MSRP.debug("&7Clan Name = &a" + clanName);
        	MSRP.debug("&7Clan Tag = &a" + clanTag);
        	MSRP.debug("&7Clan Level = &a" + clanLevel);
        	MSRP.debug("&7Clan Exp = &a" + clanExp);
        	MSRP.debug("&7Clan TotalExp = &a" + clanTotalExp);
        	MSRP.debug("&7Clan Power = &a" + clanPower);
        	MSRP.debug("&7Clan Portection = &a" + clanProtection);
        	
        	MSRP.debug("&7Clan Members Base64 = &a" + MSRP.get().getDatabase().getClanMembers(i));
        	MSRP.debug("&7Clan Ranks Base64 = &a" + MSRP.get().getDatabase().getClanRanks(i));
        	
        	MSRP.debug("&7Clan Members = &a" + members);
        	MSRP.debug("&7Clan Ranks = &a" + ranks);
        	*/
    	}
    }
    
    public void createClan (Player p, String clanName, String clanTag) {
    	Integer clansCreated = ClanesSet().size();
    	Double clanCost = 250000.0;
    	
    	if (playerHasClan(p)) {
    		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cYa eres miembro de un clan!"));
    		return;
    	}
    	
    	if (Vault.getMoney(p) < clanCost) {
    		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes suficiente dinero para crear un clan."));
    		return;
    	}
    	    	
    	if (existsClanByName(clanName)) {
    		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cYa existe un clan con ese nombre."));
    		return;
    	}
    	
    	if (existsClanByTag(clanTag)) {
    		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cYa existe un clan con ese tag."));
    		return;
    	}
    	
    	ArrayList<String> members = new ArrayList<String>();
    	ArrayList<String> ranks = new ArrayList<String>();
    	
    	members.add(p.getUniqueId().toString() + " : " + p.getName() + " : " + 1);
    	
    	ranks.add("1 : &cLider : true : true : true : true");
    	ranks.add("2 : &6Sub-Lider : true : true : true : true");
    	ranks.add("3 : &eTier 3 : true : false : false : false");
    	ranks.add("4 : &eTier 2 : false : false : false : false");
    	ranks.add("5 : &eTier 1 : false : false : false : false");
    	
    	
    	createClan((clansCreated+1));
    	Clanes clans = getClan((clansCreated+1));
    	
    	clans.setClanName(clanName);
    	clans.setClanTag(clanTag);
    	
    	clans.setClanMembers(members);
    	clans.setClanRanks(ranks);
    	
    	clans.saveClan();
    	p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHaz creado el clan &a" + clanName + " &e[" + clanTag + "]&f."));
    }
    
}
