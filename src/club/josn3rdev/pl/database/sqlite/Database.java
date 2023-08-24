package club.josn3rdev.pl.database.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;

public abstract class Database {
	
    MSRP plugin;
    Connection connection;
    
    // The name of the table we created back in SQLite class.
    public String player = "player_data";
    public String bank = "bank_data";
    public String job = "jobs_data";
    public String protection = "protections_data";
    
    public Database(MSRP instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + player + " WHERE uuid = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public boolean hasData (Player p, String tabla) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tabla + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
    //
    
    //
    // CLAN DATA
    //
    
    public Integer countClansDatabase () {
    	Integer value = 0;
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data ORDER BY clan_id DESC LIMIT 25000;");
            rs = ps.executeQuery();
            while(rs.next()){
            	value += 1;
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return value;
    }
    
    public void saveClan (Integer clanID, String clanName, String clanTag, Integer kills, Integer deaths, Integer level, Integer exp, 
    		Integer totalExp, Double power, String powerChanges, String clanMembers, String clanRanks, String clanProtection) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO clans_data ("
            		+ "clan_id, "
            		+ "clan_name, "
            		+ "clan_tag, "
            		+ "clan_kills, "
            		+ "clan_deaths, "
            		+ "clan_level, "
            		+ "clan_exp, "
            		+ "clan_totalExp, "
            		+ "clan_power, "
            		+ "clan_powerchanges, "
            		+ "clan_members, "
            		+ "clan_ranks, "
            		+ "clan_protection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
           
            ps.setInt(1, clanID);
            ps.setString(2, clanName);
            ps.setString(3, clanTag);
            ps.setInt(4, kills);
            ps.setInt(5, deaths);
            ps.setInt(6, level);
            ps.setInt(7, exp);
            ps.setInt(8, totalExp);
            ps.setDouble(9, power);
            ps.setString(10, powerChanges);
            ps.setString(11, clanMembers);
            ps.setString(12, clanRanks);
            ps.setString(13, clanProtection);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    public String getClanName (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_name");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getClanTag (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_tag");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public Integer getClanKills (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getInt("clan_kills");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }
    
    public Integer getClanDeaths (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getInt("clan_deaths");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }
    
    public Integer getClanLevel (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getInt("clan_level");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 1;
    }
    
    public Integer getClanExp (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getInt("clan_exp");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }
    
    public Integer getClanTotalExp (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getInt("clan_totalExp");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }
    
    public Double getClanPower (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getDouble("clan_power");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0.0;
    }
    
    public String getClanPowerChanges (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_powerchanges");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getClanMembers (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_members");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getClanRanks (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_ranks");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getClanProtection (Integer clanID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM clans_data WHERE clan_id = '"+clanID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("clan_id") == clanID) {
                    return rs.getString("clan_protection");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    //
    // PLAYER DATA
    //
    

    public Integer getHospital (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("hospital");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 1;
    }
    
    public Integer getSpazioCoins (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("spaziocoins");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 1;
    }
    
    public Boolean getBedrock (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getBoolean("bedrock");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
    public String getMarry (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getString("marry");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public Integer getLevel (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("level");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 1;
    }
    
    public Integer getExp (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("exp");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }
    
    public Integer getTotalExp (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("totalExp");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public String getProtectionsOwner (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getString("protections_owner");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getProtectionMember (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + player + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getString("protections_member");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public void savePlayerData (Player player, Integer spazioCoins, Integer hospital, Boolean bedrock, String marry, Integer level, Integer exp, Integer totalExp, String protectionOwner, String protectionMember) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + this.player + " ("
            		+ "uuid, "
            		+ "name, "
            		+ "spaziocoins, "
            		+ "hospital, "
            		+ "bedrock, "
            		+ "marry, "
            		+ "level, "
            		+ "exp, "
            		+ "totalExp, "
            		+ "protections_owner, "
            		+ "protections_member"
            		+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());   
            ps.setInt(3, spazioCoins);
            ps.setInt(4, hospital);
            ps.setBoolean(5, bedrock);
            ps.setString(6, marry);
            ps.setInt(7, level);
            ps.setInt(8, exp);
            ps.setInt(9, totalExp);
            ps.setString(10, protectionOwner);
            ps.setString(11, protectionMember);   
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    
    //
    // BANK DATA
    //
    
    public Double getTotalBankBalance() {
    	Double value = 0.0;
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + bank + " ORDER BY bankBalance DESC LIMIT 25000;");
            rs = ps.executeQuery();
            while(rs.next()){
            	String uuid = rs.getString("uuid");
            	Double balance = 0.0;
            	Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            	if (p != null) {
            		SPlayer sp = PlayerManager.get().getPlayer(UUID.fromString(uuid));
            		balance = sp.getBankBalance();
            	} else {
            		balance = rs.getDouble("bankBalance");
            	}
            	value += balance;
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return value;
    }
    
    public Double getBankBalance (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + bank + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getDouble("bankBalance");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0.0;
    }
    
    public Integer getBankLevel (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + bank + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getInt("bankLevel");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 1;
    }
    
    public String getBankTransactions (Player p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + bank + " WHERE uuid = '"+p.getUniqueId().toString()+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(p.getUniqueId().toString())) {
                    return rs.getString("bankTransactions");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public void savePlayerBank (Player player, Double bankBalance, Integer bankLevel, String transactions) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + bank + " ("
            		+ "uuid, "
            		+ "name, "
            		+ "bankBalance, "
            		+ "bankLevel, "
            		+ "bankTransactions"
            		+ ") VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setDouble(3, bankBalance);
            ps.setInt(4, bankLevel);
            ps.setString(5, transactions);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    //
    // PROTECTION DATA
    //

    public ArrayList<String> getProtectionsData () {
    	ArrayList<String> value = new ArrayList<String>();
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " ORDER BY protection_uuid DESC LIMIT 25000;");
            rs = ps.executeQuery();
            while(rs.next()){
            	String proteUUID = rs.getString("protection_uuid");
            	value.add(proteUUID);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return value;
    }
    
    public String getProtectionOwnerName (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getString("protection_ownerName");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getProtectionOwnerUUID (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getString("protection_ownerUUID");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getProtectionMembers (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getString("protection_members");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public String getProtectionCenterLoc (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getString("protection_center");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    
    public Integer getProtectionSize (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getInt("protection_size");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 10;
    }
    
    public Integer getProtectionPower (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + protection + " WHERE protection_uuid = '"+protectionUUID+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("protection_uuid").equalsIgnoreCase(protectionUUID)) {
                    return rs.getInt("protection_power");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 40;
    }
    
    public void saveProtectionData (String protectionUUID, String ownerName, String ownerUUID, String members, String locationCenter, Integer size, Integer power) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + protection + " ("
            		+ "protection_uuid, "
            		
            		+ "protection_ownerName, "
            		+ "protection_ownerUUID, "
            		+ "protection_members, "
            		
            		+ "protection_center, "
            		+ "protection_size, "
            		
            		+ "protection_power"
            		+ ") VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            ps.setString(1, protectionUUID);
            
            ps.setString(2, ownerName);
            ps.setString(3, ownerUUID);
            ps.setString(4, members);
            
            ps.setString(5, locationCenter);
            ps.setInt(6, size);
            
            ps.setInt(7, power);
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    public void deleteProtectionData (String protectionUUID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + protection + " WHERE protection_uuid = '" + protectionUUID + "';");
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    //
    //
    //
    
    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
    
}