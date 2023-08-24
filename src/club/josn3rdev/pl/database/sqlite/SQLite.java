package club.josn3rdev.pl.database.sqlite;
 
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import club.josn3rdev.pl.MSRP;

public class SQLite extends Database {
	
    String dbname;
    
    public SQLite(MSRP instance) { super(instance);
        dbname = "savedata"; // Set the table name here e.g player_kills
    }

    public String CreateTokenLevel = "CREATE TABLE IF NOT EXISTS player_data (" +
    		
    		"`uuid` VARCHAR(255) NOT NULL," +
    		"`name` VARCHAR(255) NOT NULL," +
            
			"`spaziocoins` INT(11) NOT NULL," +
			"`hospital` INT(11) NOT NULL," +
			"`bedrock` VARCHAR(255) NOT NULL," +
			"`marry` VARCHAR(255) NOT NULL," +
			
			"`level` INT(11) NOT NULL," +
			"`exp` INT(11) NOT NULL," +
			"`totalExp` INT(11) NOT NULL," +
            
			"`protections_owner` VARCHAR(255) NOT NULL," +
			"`protections_member` VARCHAR(255) NOT NULL," +
			
            "PRIMARY KEY (`uuid`));";

    public String CreateTokenBank = "CREATE TABLE IF NOT EXISTS bank_data (" +
    		
    		"`uuid` VARCHAR(255) NOT NULL," +
    		"`name` VARCHAR(255) NOT NULL," +
            
    		"`bankBalance` REAL(255) NOT NULL," +
    		"`bankLevel` INT(11) NOT NULL," +
    		"`bankTransactions` VARCHAR(255) NOT NULL," +
			
            "PRIMARY KEY (`uuid`));";
    
    public String CreateTokenClans = "CREATE TABLE IF NOT EXISTS clans_data (" +
    		
    		"`clan_id` INT(11) NOT NULL," +
    		
    		"`clan_name` VARCHAR(255) NOT NULL," +
    		"`clan_tag` VARCHAR(255) NOT NULL," +

			"`clan_kills` INT(11) NOT NULL," +
			"`clan_deaths` INT(11) NOT NULL," +
			    		
			"`clan_level` INT(11) NOT NULL," +
			"`clan_exp` INT(11) NOT NULL," +
			"`clan_totalExp` INT(11) NOT NULL," +
			
			"`clan_power` REAL(255) NOT NULL," +
			"`clan_powerchanges` VARCHAR(255) NOT NULL," +
			
			"`clan_members` VARCHAR(255) NOT NULL," +
			"`clan_ranks` VARCHAR(255) NOT NULL," +
			
			"`clan_protection` VARCHAR(255) NOT NULL," +
			
            "PRIMARY KEY (`clan_id`));";
    
    public String CreateTokenProtections = "CREATE TABLE IF NOT EXISTS protections_data (" +
    		
    		"`protection_uuid` VARCHAR(255) NOT NULL," +
    		
    		"`protection_ownerName` VARCHAR(255) NOT NULL," +
    		"`protection_ownerUUID` VARCHAR(255) NOT NULL," +
    		"`protection_members` VARCHAR(255) NOT NULL," +
			
			"`protection_center` VARCHAR(255) NOT NULL," +
			"`protection_size` INT(11) NOT NULL," +
			    		
			"`protection_power` INT(11) NOT NULL," +
			
            "PRIMARY KEY (`protection_uuid`));";
    
    
    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
        	MSRP.debug("Data folder not exists!");
            try {
                dataFolder.createNewFile();
                MSRP.debug("Data folder has been created!");
            } catch (IOException e) {
            	MSRP.debug("File write error: " + dbname + ".db");
            }
        }
        try {
            if(connection !=null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "");
            MSRP.debug("You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(CreateTokenLevel);
            s.executeUpdate(CreateTokenBank);
            s.executeUpdate(CreateTokenClans);
            s.executeUpdate(CreateTokenProtections);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
    
}