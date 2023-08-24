package club.josn3rdev.pl.database.sqlite;

import java.util.logging.Level;

import club.josn3rdev.pl.MSRP;

public class Error {
    public static void execute(MSRP plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(MSRP plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
 
