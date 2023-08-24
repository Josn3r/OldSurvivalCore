package club.josn3rdev.pl.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Nivel;
import club.josn3rdev.pl.jobs.Jobs;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.zones.Zones;

public class SPlayer {

	// STATS YML LOADED
	private UUID uuid;
	private Player player;
	
	private int level = 1;
	private int exp = 0;
	private int totalExp = 0;

	private int hospital = 0; // 0 = NADA - 1 = SEGURO - 2 = SOCIO
	
	// MARRY
	private String marryUUID = null;
	private String marryName = null;
	private String marryDate = null;
	
	// PROTECTION
	private ArrayList<String> protectionOwner = new ArrayList<String>();
	private ArrayList<String> protectionMember = new ArrayList<String>();
	
	
	// SPAZIOCOINS
	private int spaziocoins = 0;
	
	// LEVELS
	private BossBar bar;
	
	// ZONES
	private Zones zone = Zones.NONE;
	private Boolean playerInRegion = false;
	private String depaZone = null;
	
	// JOBS
	private Jobs job1 = null;
	private Jobs job2 = null;
	
	private Location corner1 = null;
	private Location corner2 = null;
	
	private String actualJob = null;
	private HashMap<Material, Integer> actualJobAmount = new HashMap<Material, Integer>();
	private ArrayList<String> actualJobPoints = new ArrayList<String>();
	
	private Integer garbageJobCount = 0;
	private Boolean garbageShiftNotify = false;
	
	// JOBS CONFIG
	
	private String missionName = "";
	private Double missionPayment = 0.0D;
	private Integer missionLevel = 1;
	private Integer missionTimeleft = 300;
	
	private HashMap<Material, Integer> woodcutterNeed = new HashMap<Material, Integer>();
	private HashMap<Material, Integer> minerNeed = new HashMap<Material, Integer>();
	private HashMap<Material, Integer> harvestNeed = new HashMap<Material, Integer>();
	private HashMap<Material, Integer> farmerNeed = new HashMap<Material, Integer>();
	private HashMap<Material, Integer> fishingNeed = new HashMap<Material, Integer>();
	private ArrayList<String> garbagePoints = new ArrayList<String>();
	
	// BANK
	private Boolean bedrock = false;
	
	private Double bankBalance = 0.0;
	private Integer bankLevel = 1;
	private ArrayList<String> transactions = new ArrayList<String>();
	
	private String transactionType = "null";
	private Integer transactionCount = 30;
	
	
	public SPlayer(UUID uUID) {
        this.setUuid(uUID);
        this.setPlayer(Bukkit.getPlayer(uUID));
    }

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	//
		
	public Boolean getBedrock() {
		return bedrock;
	}

	public void setBedrock(Boolean bedrock) {
		this.bedrock = bedrock;
	}
		
	//

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}

	//
	
	public void setBossbar (String str, Double progress) {
		if (!MSRP.lang.getBoolean("messages.levels.boss-bar.enable")) {
			return;
		}
		
		if (bar == null) { 
			bar = Bukkit.createBossBar(Tools.get().Text(str), BarColor.YELLOW, BarStyle.SOLID);
		} else {
			bar.setTitle(Tools.get().Text(str));
		}
		
		Double barProgress = 1.0d;
		Double barNeededExp = Double.valueOf(Nivel.get().getNeededExpToLevelUp(this.getPlayer()));
		Double barProgressDivision = barProgress / barNeededExp;
		
		Double finalBarProgress = barProgressDivision * getExp();
		if (getLevel() == 16) {
			finalBarProgress = 1.0d;
		}		
		if (finalBarProgress < 0.0D) {
			finalBarProgress = 0.0D;
		}
		if (finalBarProgress > 1.0D) {
			finalBarProgress = 1.0D;
		}
		
		bar.setProgress(finalBarProgress);
		bar.setVisible(true);
		
		bar.addPlayer(getPlayer());
	}
	
	public BossBar getBar() {
		return bar;
	}
	
	public void removeBossbar() {
		bar.removePlayer(getPlayer());
	}
    
	public void updateBossbar() {
		String format = MSRP.lang.getString("messages.levels.boss-bar.format");
		
		if (MSRP.lang.getBoolean("messages.levels.boss-bar.enable")) {
			if (getBar() == null) {
				setBossbar("", 1.0);
				return;
			}
			
			if (getLevel() == 16) {
				getBar().setTitle(Tools.get().Text("&f&lNivel: &a<LEVEL> &f- Máximo nivel alcanzado!"
						.replace("<LEVEL>", ""+getLevel()).replace("<EXP>", ""+getExp())
						.replace("<EXP_NEEDED>", ""+Nivel.get().getNeededExpToLevelUp(getPlayer()))));
				getBar().setProgress(1.0d);
				return;
			}	
			
			getBar().setTitle(Tools.get().Text(format.replace("<LEVEL>", ""+getLevel()).replace("<EXP>", ""+getExp())
					.replace("<EXP_NEEDED>", ""+Nivel.get().getNeededExpToLevelUp(getPlayer()))));
			
			Double barProgress = 1.0D;
			Double barNeededExp = Double.valueOf(Nivel.get().getNeededExpToLevelUp(getPlayer()));
			Double barProgressDivision = barProgress / barNeededExp;
			
			Double finalBarProgress = barProgressDivision * getExp();
			
			if (finalBarProgress < 0.0D) {
				finalBarProgress = 0.0D;
			}
			if (finalBarProgress > 1.0D) {
				finalBarProgress = 1.0D;
			}
			
			getBar().setProgress(finalBarProgress);			
		}
	}
	
	//
	
	public Double getBankBalance() {
		return bankBalance;
	}

	public void setBankBalance(Double bankBalance) {
		this.bankBalance = bankBalance;
	}

	public Integer getBankLevel() {
		return bankLevel;
	}

	public void setBankLevel(Integer bankLevel) {
		this.bankLevel = bankLevel;
	}

	public ArrayList<String> getTransactions() {
		return transactions;
	}
	
	public void setTransactions(ArrayList<String> list) {
		this.transactions = list;
	}
	
	public void startChatTransaction() {
		new BukkitRunnable() {
			@Override
			public void run() {				
				if (transactionCount > 0) {
					--transactionCount;
				} else {
					cancel();
					transactionType = "null";
					transactionCount = 30;
				}
			}		
		}.runTaskTimer(MSRP.get(), 0L, 20L);
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(Integer transactionCount) {
		this.transactionCount = transactionCount;
	}
	
	//

	public Jobs getJob1() {
		return job1;
	}

	public void setJob1(Jobs job1) {
		this.job1 = job1;
	}

	public Jobs getJob2() {
		return job2;
	}

	public void setJob2(Jobs job2) {
		this.job2 = job2;
	}

	public Location getCorner1() {
		return corner1;
	}

	public void setCorner1(Location corner1) {
		this.corner1 = corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public void setCorner2(Location corner2) {
		this.corner2 = corner2;
	}

	public String getActualJob() {
		return actualJob;
	}

	public void setActualJob(String actualJob) {
		this.actualJob = actualJob;
	}

	public ArrayList<String> getActualJobPoints() {
		return actualJobPoints;
	}

	public Integer getGarbageJobCount() {
		return garbageJobCount;
	}

	public void setGarbageJobCount(Integer garbageJobCount) {
		this.garbageJobCount = garbageJobCount;
	}

	public Boolean getGarbageShiftNotify() {
		return garbageShiftNotify;
	}

	public void setGarbageShiftNotify(Boolean garbageShiftNotify) {
		this.garbageShiftNotify = garbageShiftNotify;
	}

	public HashMap<Material, Integer> getActualJobAmount() {
		return actualJobAmount;
	}
	
	
	//
	
	public Zones getZone() {
		return zone;
	}

	public void setZone(Zones zone) {
		this.zone = zone;
	}
	
	//
	
	public void resetMissionCreator() {
		missionName = "";
		missionPayment = 0.0D;
		missionLevel = 1;
		missionTimeleft = 300;
		
		woodcutterNeed.clear();
		minerNeed.clear();
		harvestNeed.clear();
		garbagePoints.clear();
	}
	
	public String getMissionName() {
		return missionName;
	}

	public void setMissionName(String missionName) {
		this.missionName = missionName;
	}

	public Double getMissionPayment() {
		return missionPayment;
	}

	public void setMissionPayment(Double missionPayment) {
		this.missionPayment = missionPayment;
	}

	public Integer getMissionLevel() {
		return missionLevel;
	}

	public void setMissionLevel(Integer missionLevel) {
		this.missionLevel = missionLevel;
	}

	public Integer getMissionTimeleft() {
		return missionTimeleft;
	}

	public void setMissionTimeleft(Integer missionTimeleft) {
		this.missionTimeleft = missionTimeleft;
	}

	public HashMap<Material, Integer> getWoodcutterNeed() {
		return woodcutterNeed;
	}

	public HashMap<Material, Integer> getMinerNeed() {
		return minerNeed;
	}

	public HashMap<Material, Integer> getHarvestNeed() {
		return harvestNeed;
	}

	public HashMap<Material, Integer> getFarmerNeed() {
		return farmerNeed;
	}

	public HashMap<Material, Integer> getFishingNeed() {
		return fishingNeed;
	}

	public ArrayList<String> getGarbagePoints() {
		return garbagePoints;
	}

	public String getDepaZone() {
		return depaZone;
	}

	public void setDepaZone(String depaZone) {
		this.depaZone = depaZone;
	}

	public Boolean getPlayerInRegion() {
		return playerInRegion;
	}

	public void setPlayerInRegion(Boolean playerInRegion) {
		this.playerInRegion = playerInRegion;
	}

	public int getSpazioCoins() {
		return spaziocoins;
	}

	public void setSpazioCoins(int spaziocoins) {
		this.spaziocoins = spaziocoins;
	}

	public int getHospital() {
		return hospital;
	}

	public void setHospital(int hospital) {
		this.hospital = hospital;
	}

	public String getMarryUUID() {
		return marryUUID;
	}

	public void setMarryUUID(String marryUUID) {
		this.marryUUID = marryUUID;
	}

	public String getMarryName() {
		return marryName;
	}

	public void setMarryName(String marryName) {
		this.marryName = marryName;
	}

	public String getMarryDate() {
		return marryDate;
	}

	public void setMarryDate(String marryDate) {
		this.marryDate = marryDate;
	}

	public ArrayList<String> getProtectionOwner() {
		return protectionOwner;
	}

	public void setProtectionOwner(ArrayList<String> protectionOwner) {
		this.protectionOwner = protectionOwner;
	}

	public ArrayList<String> getProtectionMember() {
		return protectionMember;
	}

	public void setProtectionMember(ArrayList<String> protectionMember) {
		this.protectionMember = protectionMember;
	}

}
