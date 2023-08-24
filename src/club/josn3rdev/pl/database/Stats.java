package club.josn3rdev.pl.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;

public class Stats {
	
	private static Stats s;    
	
    public static Stats get() {
        if (s == null) {
        	s = new Stats();
        }
        return s;
    }
    
    public String Text(String s) {
        return s.replaceAll("&", "§");
    }

    public void createPlayer(Player p) {    	
    	if (MSRP.get().getDatabase().getSQLConnection() != null) {
    		if (!MSRP.get().getDatabase().hasData(p, "player_data")) {
    			MSRP.get().getDatabase().savePlayerData(p, 0, 0, false, "null", 1, 0, 0, "NONE", "NONE");
    			MSRP.get().getDatabase().savePlayerBank(p, 0.0, 1, "");
    		}
    	}
    }
    
    public void loadStats(Player p) {
        SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());  
        
    	if (MSRP.get().getDatabase().getSQLConnection() != null) {    		
    		if (!MSRP.get().getDatabase().hasData(p, "player_data")) {
    			createPlayer(p);
    			return;
    		}   	    		
    		
    		sp.setSpazioCoins(MSRP.get().getDatabase().getSpazioCoins(p));
    		sp.setHospital(MSRP.get().getDatabase().getHospital(p));
    		sp.setBedrock(MSRP.get().getDatabase().getBedrock(p));
    		sp.setLevel(MSRP.get().getDatabase().getLevel(p));
    		sp.setExp(MSRP.get().getDatabase().getExp(p));
    		sp.setTotalExp(MSRP.get().getDatabase().getTotalExp(p));
    		
    		String marry = MSRP.get().getDatabase().getMarry(p);
    		if (marry.contains(" /// ")) {
    			sp.setMarryUUID(marry.split(" /// ")[0]);
    			sp.setMarryName(marry.split(" /// ")[1]);
    			sp.setMarryDate(marry.split(" /// ")[2]);
    		} else {
    			sp.setMarryUUID(null);
    			sp.setMarryName(null);
    			sp.setMarryDate(null);
    		}
    		
    		String proteOwnerArray = MSRP.get().getDatabase().getProtectionsOwner(p);    		
    		ArrayList<String> loaded = new ArrayList<String>();
			if (!proteOwnerArray.contains("NONE")) {
    			for (String str : proteOwnerArray.split(" : ")) {
    				loaded.add(str);
    			}
    		}
    		sp.setProtectionOwner(loaded);
    		
    		String proteMemberArray = MSRP.get().getDatabase().getProtectionMember(p);
    		ArrayList<String> loaded1 = new ArrayList<String>();
			if (!proteMemberArray.contains("NONE")) {
    			for (String str : proteMemberArray.split(" : ")) {
    				loaded1.add(str);
    			}
    		}
    		sp.setProtectionMember(loaded1);
    		
    		if (MSRP.get().getDatabase().hasData(p, "bank_data")) {
    			sp.setBankBalance(MSRP.get().getDatabase().getBankBalance(p));
        		sp.setBankLevel(MSRP.get().getDatabase().getBankLevel(p));
        		byte[] transactions64 = Base64.getDecoder().decode(MSRP.get().getDatabase().getBankTransactions(p));
        		String transactionsArray = new String(transactions64);
        		
        		ArrayList<String> trans = new ArrayList<String>();
    			if (transactionsArray.contains(" /// ")) {
        			for (String str : transactionsArray.split(" /// ")) {
        				trans.add(str);
        			}
        		}
        		sp.setTransactions(trans);
    		}
    		MSRP.debug("&7Se ha cargado correctamente los datos de &f" + p.getName());
    	}    	
    }
    
    public void savePlayer(Player p) {
    	SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
        
    	if (MSRP.get().getDatabase().getSQLConnection() != null) {
    		if (!MSRP.get().getDatabase().hasData(p, "player_data")) {
    			createPlayer(p);
    		}
    		
    		Double bankBalance = 0.0;
    		Integer bankLevel = 1;
    		String transactions64 = "";
    		String marry = "NONE";
    		
    		if (sp.getMarryUUID() != null) {
    			marry = sp.getMarryUUID() + " /// " + sp.getMarryName() + " /// " + sp.getMarryDate();
    		}
    		
    		String protectionOwner = "NONE";
			if (!sp.getProtectionOwner().isEmpty()) {
				protectionOwner = "";
				for (String str : sp.getProtectionOwner()) {
					protectionOwner = protectionOwner + str + " : ";
    			}
				protectionOwner = protectionOwner.substring(0, protectionOwner.length() - 3);
			}
			
			String protectionMember = "NONE";
			if (!sp.getProtectionMember().isEmpty()) {
				protectionOwner = "";
				for (String str : sp.getProtectionMember()) {
					protectionMember = protectionMember + str + " : ";
    			}
				protectionMember = protectionMember.substring(0, protectionMember.length() - 3);
			}
			
    		if (MSRP.get().getDatabase().hasData(p, "bank_data")) {
    			bankBalance = sp.getBankBalance();
    			bankLevel = sp.getBankLevel();
    			String transacciones = "";
    			if (!sp.getTransactions().isEmpty()) {
    				for (String str : sp.getTransactions()) {
        				transacciones = transacciones + str + " /// ";
        			}
        			transacciones = transacciones.substring(0, transacciones.length() - 5);
    			}
    			transactions64 = Base64.getEncoder().encodeToString(transacciones.toString().getBytes());  
    			MSRP.get().getDatabase().savePlayerBank(p, bankBalance, bankLevel, transactions64);
    		}    		
    		MSRP.get().getDatabase().savePlayerData(p, sp.getSpazioCoins(), sp.getHospital(), sp.getBedrock(), marry, sp.getLevel(), sp.getExp(), sp.getTotalExp(), protectionOwner, protectionMember);
    		
    	}   
    }
    
    public static void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
