package club.josn3rdev.pl.clanes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.protections.Selection;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;

public class Clanes {
	
	// STATS YML LOADED
	private Integer clanID;
	
	private String clanName = "";
	private String clanTag = "";
		
	private int clanKills = 0;
	private int clanDeaths = 0;
	
	private int clanLevel = 1;
	private int clanExp = 0;
	private int clanTotalExp = 0;
	
	private Double clanPower = 40.0;
	private ArrayList<String> powerChanges = new ArrayList<String>();
	
	
	private ArrayList<String> clanMembers = new ArrayList<String>();
	
	// 'RANK ID : RANK NAME : RANK INVITE : RANK KICK : RANK EDIT MEMBERS : RANK ENABLE FRIENDLY FIRE'
	private ArrayList<String> clanRanks = new ArrayList<String>(); 
	
	private Boolean friendlyFire = false;
	
	private Location clanProtection = null;
	private Selection selection;
		
	public Clanes (Integer clanID) {
		this.clanID = clanID;
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
		if (block.getWorld() != this.clanProtection.getWorld())
			return false; 
		if (numberInRange(block.getX(), selection.getCorner_one().getX(), selection.getCorner_four().getX()))
			return numberInRange(block.getZ(), selection.getCorner_one().getZ(), selection.getCorner_four().getZ()); 
		return false;
	}
	
	public boolean numberInRange(int target, int starting, int ending) {
	    return (target >= starting && target <= ending);
	}
	
	public String getClanName() {
		return clanName;
	}


	public void setClanName(String clanName) {
		this.clanName = clanName;
	}


	public String getClanTag() {
		return clanTag;
	}


	public void setClanTag(String clanTag) {
		this.clanTag = clanTag;
	}


	public int getClanKills() {
		return clanKills;
	}


	public void setClanKills(int clanKills) {
		this.clanKills = clanKills;
	}


	public int getClanDeaths() {
		return clanDeaths;
	}


	public void setClanDeaths(int clanDeaths) {
		this.clanDeaths = clanDeaths;
	}
	
	
	public String getClanKDR() {
		if (this.clanKills == 0 || this.clanDeaths == 0) {
			return "0.0";
		}		
		DecimalFormat format = new DecimalFormat("#.##");
		double calculate = clanKills/clanDeaths;
		return format.format(calculate);
	}


	public int getClanLevel() {
		return clanLevel;
	}


	public void setClanLevel(int clanLevel) {
		this.clanLevel = clanLevel;
	}


	public int getClanExp() {
		return clanExp;
	}


	public void setClanExp(int clanExp) {
		this.clanExp = clanExp;
	}


	public int getClanTotalExp() {
		return clanTotalExp;
	}


	public void setClanTotalExp(int clanTotalExp) {
		this.clanTotalExp = clanTotalExp;
	}


	public Double getClanPower() {
		return clanPower;
	}


	public void setClanPower(Double clanPower) {
		this.clanPower = clanPower;
	}

	public ArrayList<String> getPowerChanges() {
		return powerChanges;
	}


	public void setPowerChanges(ArrayList<String> powerChanges) {
		this.powerChanges = powerChanges;
	}


	public ArrayList<String> getClanMembers() {
		return clanMembers;
	}


	public void setClanMembers(ArrayList<String> clanMembers) {
		this.clanMembers = clanMembers;
	}


	public ArrayList<String> getClanRanks() {
		return clanRanks;
	}


	public void setClanRanks(ArrayList<String> clanRanks) {
		this.clanRanks = clanRanks;
	}


	public Location getClanProtection() {
		return clanProtection;
	}


	public void setClanProtection(Location clanProtection) {
		this.clanProtection = clanProtection;
		if (this.clanProtection == null) {
			return;
		}
		this.selection = new Selection(this.clanProtection.getBlock(), 64);
	}


	public Integer getClanID() {
		return clanID;
	}
	
	
	public Boolean getFriendlyFire() {
		return friendlyFire;
	}


	public void setFriendlyFire(Boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
	}

	//
	
	public void saveClan() {
		String clanMembers = "";
		String clanMembers64 = "";
		for (String str : getClanMembers()) {
			clanMembers = clanMembers + str + " /// ";
		}
		clanMembers = clanMembers.substring(0, clanMembers.length() - 5);
		
		//
		
		String clanRanks = "";
		String clanRanks64 = "";
		for (String str : getClanRanks()) {
			clanRanks = clanRanks + str + " /// ";
		}
		clanRanks = clanRanks.substring(0, clanRanks.length() - 5);
		
		//
		
		String clanPowerChanges = "";
		String clanPowerChanges64 = "";
		if (!powerChanges.isEmpty()) {
			for (String str : getPowerChanges()) {
				clanPowerChanges = clanPowerChanges + str + " /// ";
			}
			clanPowerChanges = clanPowerChanges.substring(0, clanPowerChanges.length() - 5);
		}
		
		//
		
		clanMembers64 = Base64.getEncoder().encodeToString(clanMembers.toString().getBytes());
		clanRanks64 = Base64.getEncoder().encodeToString(clanRanks.toString().getBytes());
		clanPowerChanges64 = Base64.getEncoder().encodeToString(clanPowerChanges.toString().getBytes());
		
		String protection = "NONE";
		if (clanProtection != null) {
			protection = Tools.get().setLocToStringBlock(this.clanProtection);
		}
		
		MSRP.get().getDatabase().saveClan(clanID, clanName, clanTag, clanKills, clanDeaths, clanLevel, clanExp, clanTotalExp, clanPower, clanPowerChanges64, clanMembers64, clanRanks64, protection);
	}
	
	//
	
	//
	
	//
	
	public boolean isProtectionZone (Player p) {
		if (clanProtection == null) {
			return false;
		}
		Double locX = clanProtection.getX();
		Double locZ = clanProtection.getZ();
		
		Location max = new Location(clanProtection.getWorld(), locX + 64, 512, locZ + 64);
    	Location min = new Location(clanProtection.getWorld(), locX - 64, -128, locZ - 64);
    	Cuboid cuboid = new Cuboid(max.add(0.5, 0.0, 0.5), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}
	
	public boolean isProtectionZone (Location loc) {
		if (clanProtection == null) {
			return false;
		}
		Double locX = clanProtection.getX();
		Double locZ = clanProtection.getZ();
		
		Location max = new Location(clanProtection.getWorld(), locX + 64, 512, locZ + 64);
    	Location min = new Location(clanProtection.getWorld(), locX - 64, -128, locZ - 64);
    	Cuboid cuboid = new Cuboid(max.add(0.5, 0.0, 0.5), min);
    	if (cuboid.containsLocation(loc)) {
    		return true;
    	}    	
    	return false;
	}
}
