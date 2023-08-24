package club.josn3rdev.pl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.base.Strings;

import club.josn3rdev.pl.MSRP;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Tools {

	private static Tools ins;
    	
	public static Tools get() {
		if (ins == null) {
			ins = new Tools();
		}
		return ins;
	}
			
	public String Text(String s) {
	    return ChatColor.translateAlternateColorCodes('&', s);
	} 
	
	public boolean existName(Player p, String s){
    	return p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() && 
    		   p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(Tools.get().Text(s));
    }
	
	//
	//
	//
	
	public String formatMoney (Integer balance) {
		DecimalFormat format = new DecimalFormat("###,###,###,###,###.##");
		return format.format(balance);
	}
	
	public String formatMoney (Double balance) {
		DecimalFormat format = new DecimalFormat("###,###,###,###,###.##");
		return format.format(balance);
	}
	
	public Double calcPercent (Double value, Double percent) {
		double impuestos = -(100-percent);
    	double calculo = (value * (100 + (impuestos)) / 100);
    	return calculo;
	}
		
	public String findDifference(String start_date, String end_date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			String difference;
			Date d1 = sdf.parse(start_date);
			Date d2 = sdf.parse(end_date);
			long difference_In_Time = d2.getTime() - d1.getTime();
			long difference_In_Seconds = difference_In_Time / 1000L % 60L;
			long difference_In_Minutes = difference_In_Time / 60000L % 60L;
			long difference_In_Hours = difference_In_Time / 3600000L % 24L;
			long difference_In_Days = difference_In_Time / 86400000L % 365L;
	      
			if (difference_In_Days == 0L) {
				if (difference_In_Minutes == 0L) {
					difference = difference_In_Seconds + " : sec";
		        } else if (difference_In_Hours == 0L) {
		        	difference = difference_In_Minutes + " : min";
		        } else {
		        	difference = difference_In_Hours + " : hours";
		        } 
			} else {
				difference = difference_In_Days + " : days";
			} 
			return difference;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
	    } 
	}
	
	public boolean dateValidator(String format) {
	    try {
	        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
	        formatoFecha.setLenient(false);
	        formatoFecha.parse(format);
	        return true;
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    return false;
    }
	
	//
	
	public String getFormatTime (Integer timer)
    {
        int hours = timer / 3600;
        int secondsLeft = timer - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        
        if(hours >= 1)
        {
            if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";
        }
        if(minutes >= 1)
        {
        	if (minutes > 10) {
        		formattedTime += "0";
                formattedTime += minutes + ":";
        	} else {
        		formattedTime += minutes + ":";
        	}
            if (seconds < 10)
            	formattedTime += "0";
            formattedTime += seconds;
        } else {
        	formattedTime += seconds + "s" ;
        }

        return formattedTime;
    }

	public String formatLocation (Location loc) {
		Integer CoordX = loc.getBlockX();
		Integer CoordZ = loc.getBlockZ();
		
		String str = "X: " + CoordX + " - Z: " + CoordZ;
		return str;
	}
	
	//
	// MESSAGES TOOLS
	//
	
	public String compileWords(String[] args, int index) {
	    StringBuilder builder = new StringBuilder();
	    for (int i = index; i < args.length; i++) {
	      builder.append(args[i] + " ");
	    }
	    return builder.toString().trim();
	}
	
	public void sendClickeableMessage (Player p, String message, String event) {
    	ComponentBuilder mensaje = new ComponentBuilder (message);
    	BaseComponent[] msg = mensaje.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, event)).create();
    	
    	p.spigot().sendMessage(msg);
    }
	
	public void sendSpigotMessageWithPrefix(UUID playerUUID, TextComponent[] textComponents) {
		TextComponent prefixComponent = new TextComponent(MSRP.PREFIX);
		Player player = Bukkit.getPlayer(playerUUID);
		TextComponent start = new TextComponent(prefixComponent);
		for (TextComponent textComponent : textComponents)
			start.addExtra((BaseComponent)textComponent); 
		player.spigot().sendMessage((BaseComponent)start);
	}
	  
	public void sendSpigotMessageWithoutPrefix(UUID playerUUID, TextComponent[] textComponents) {
		Player player = Bukkit.getPlayer(playerUUID);
		TextComponent start = new TextComponent("");
		for (TextComponent textComponent : textComponents)
			start.addExtra((BaseComponent)textComponent); 
		player.spigot().sendMessage((BaseComponent)start);
	}
	  
	public void clearChat (Player p, Integer lines) {
		for (int i = 0; i < lines; ++i) {
			p.sendMessage(" ");
		}
	}
	
	public void sendTitle(Player player, String msgTitle, String msgSubTitle, int ticks) {
        player.sendTitle(Text(msgTitle), Text(msgSubTitle), 20, ticks, 20);
    }

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Text(message)));
    }
	
	//
	// SOUND/PARTICLE TOOLS
	//
	
	public void playSound(Player player, Sound sound, Float volumen, Float pitch) {
        player.playSound(player.getLocation(), sound, volumen, pitch);
    }
	
	public void playSound(Location loc, Sound sound, Float volumen, Float pitch) {
        loc.getWorld().playSound(loc, sound, volumen, pitch);
    }
	
	public void playParticle (Particle particle, Player p, Integer cantidad, Double offSetX, Double offSetY, Double offSetZ) {
		p.getWorld().spawnParticle(particle, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 
				cantidad, offSetX, offSetY, offSetZ, 0.01d, null, false);
		
		//p.getWorld().spawnParticle(particle, p.getLocation(), cantidad, offSetX, offSetY, offSetZ);
	}
	
	public void playParticle (Particle particle, Location loc, Integer cantidad, Double offSetX, Double offSetY, Double offSetZ) {
		loc.getWorld().spawnParticle(particle, loc, cantidad, offSetX, offSetY, offSetZ);
	}
		
	//
	// LOCATION TOOLS
	//
	
	public void teleportTo (Player p, Location loc) {
		if (loc != null) {
			p.teleport(loc.add(0.5, 0.0, 0.5));
		} else {
			p.sendMessage(Text("&cThat location does exists or not configure..."));
		}
		
	}
	
	public String setLocToStringBlock(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

	public String setLocToString(Player p) {
		Location loc = p.getLocation();
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," +  loc.getYaw() + "," + loc.getPitch();
    }
	
    public Location setStringToLocBlock(String path) {
        Location loc = null;
        if (path == null) {
        	return loc;
        }
        String[] locs = path.split(",");
        loc = new Location(Bukkit.getWorld(locs[0]), (double)Integer.parseInt(locs[1]), (double)Integer.parseInt(locs[2]), (double)Integer.parseInt(locs[3]));
        return loc;
    }
    
    public Location setStringToLoc(String path) {
        Location loc = null;
        if (path == null) {
        	return loc;
        }
        String[] locs = path.split(",");
        loc = new Location(Bukkit.getWorld(locs[0]), (double)Double.valueOf(locs[1]), (double)Double.valueOf(locs[2]), (double)Double.valueOf(locs[3]), (float)Float.valueOf(locs[4]), (float)Float.valueOf(locs[5]));
        return loc;
    }
    
	//
	// SAVE A CONFIG / YML FILE
	//
	
    public void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //
    // CHECK IF ARGUMENT IS A INT.
    //
    
    public boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
    
    public boolean isDouble(String s) {
	    try {
	        Double.parseDouble(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
    
    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }
      
    // PROGRESS BAR
	
 	public String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
 		float percent = (float) current / max;
 		int progressBars = (int) (totalBars * percent);
 		return Strings.repeat("" + completedColor + symbol, progressBars) + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
 	}
 	
 	public String getProgressBar(Double current, Double max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
 		Double percent = current / max;
 		int progressBars = (int) (totalBars * percent);
 		return Strings.repeat("" + completedColor + symbol, progressBars) + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
 	}
     
 	// CENTERED MESSAGE
 	
 	private final static int CENTER_PX = 154;
 	 
 	public void sendCenteredMessage(Player player, String message){
 		if(message == null || message.equals(""))
 			player.sendMessage("");	
 			
 		message = ChatColor.translateAlternateColorCodes('&', message);
 		
 		int messagePxSize = 0;
 		boolean previousCode = false;
 		boolean isBold = false;
 	 
 		for(char c : message.toCharArray()){
 			if(c == '§'){
 				previousCode = true;
 				continue;
 			}else if(previousCode == true){
 				previousCode = false;
 				if(c == 'l' || c == 'L'){
 					isBold = true;
 					continue;
 				}else isBold = false;
 			}else{
 				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
 				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
 				messagePxSize++;
 			}
 		}
 		
 		int halvedMessageSize = messagePxSize / 2;
 		int toCompensate = CENTER_PX - halvedMessageSize;
 		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
 		int compensated = 0;
 		StringBuilder sb = new StringBuilder();
 		while(compensated < toCompensate){
 			sb.append(" ");
 			compensated += spaceLength;
 		}	
 		player.sendMessage(sb.toString() + message);
 	}
 	    
    public void executeDeathEffect (Player p) {
    	Location location = p.getLocation();    	
    	p.getWorld().playEffect(location.add(0.0d,0.5d,0.0d), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    	p.getWorld().playEffect(location.add(0.0d,1.0d,0.0d), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    }
    
    //
    
    public void summonFirework (Location loc) {
		final Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).withColor(Color.GREEN).build());
		fwm.setPower(2);
		fw.setFireworkMeta(fwm);
	}
    
    //
    
 
    /**
     * 
     * 
     * 
     * 
     */    
    
    public String getAlphaNumericString(int n) {
    	String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	String AlphaNumericString = "0123456789";
    	StringBuilder sb = new StringBuilder(n);
    	for (int i = 0; i < n; i++) {
    		int j = (int)(AlphaNumericString.length() * Math.random());
    		sb.append(AlphaNumericString.charAt(j));
        } 
        int index = (int)(letters.length() * Math.random());
        char letter = letters.charAt(index);
        return letter + sb.toString();
    }
    
    public boolean playerIsInTheRadius (Location loc, Location locPlayer) {
		int X = loc.getBlockX();
    	int Y = loc.getBlockY();
    	int Z = loc.getBlockZ();		
		Location max = new Location(loc.getWorld(), X + 4, Y + 3, Z + 4);
    	Location min = new Location(loc.getWorld(), X - 4, Y - 3, Z - 4);
    	Cuboid cuboid = new Cuboid(max, min);
    	if (cuboid.containsLocation(locPlayer)) {
    		return true;
    	}    	
    	return false;
	}
    

    ///
    // LICENSE KEY
    //
    
    public String licenceID = MSRP.get().getConfig().getString("config.plugin-licence");
    public boolean blocked = false;
     
    public void executeAuthme() {
    	MSRP.debug("&7Checking MineSpazio RP-Survival Core license...");
		try {
			URLConnection url = new URL("https://pastebin.com/raw/CYw0Ts8P").openConnection();
			url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			url.connect();
         
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.getInputStream(), Charset.forName("UTF-8")));
         
			String str1;
			ArrayList<String> licencesLoad = new ArrayList<String>();
			
			while ((str1 = reader.readLine()) != null) {
				licencesLoad.add(str1);
			}	
			
			if (!licencesLoad.contains(licenceID)) {
				disableLeak(licenceID);
				return;
			} else {
				MSRP.debug("&7License is active! Starting loading plugin...");
			}
        }
        catch (IOException localIOException)
        {
        	disableNoInternet();
        	return;
        }
	}
	
    public void disableLeak(String ID) {
    	blocked = true;
    	Bukkit.getConsoleSender().sendMessage(Text("&7&m------------------------------------------------------------"));
    	Bukkit.getConsoleSender().sendMessage(Text("&7MSRP will be disable shortly..."));
    	Bukkit.getConsoleSender().sendMessage(Text("  "));
    	Bukkit.getConsoleSender().sendMessage(Text("&7The licence ID (&f" + ID + "&7) It is not on the white list."));
    	Bukkit.getConsoleSender().sendMessage(Text("&7You have broken the terms of the plugin"));
    	Bukkit.getConsoleSender().sendMessage(Text("&7And therefore the plugin has blocked its use with that ID."));
    	Bukkit.getConsoleSender().sendMessage(Text("  "));
    	Bukkit.getConsoleSender().sendMessage(Text("&7Get in touch with &9&lJosn3r#1259 &7on discord and request a new license."));
    	Bukkit.getConsoleSender().sendMessage(Text("  "));
    	Bukkit.getConsoleSender().sendMessage(Text("&7&m------------------------------------------------------------"));
    	MSRP.get().getServer().getPluginManager().disablePlugin(MSRP.get());
    	return;
    }
   
    public void disableNoInternet() {
    	Bukkit.getConsoleSender().sendMessage(Text("&7&m------------------------------------------------------------"));
    	Bukkit.getConsoleSender().sendMessage(Text("&cYou don't have a valid internet connection..."));
    	Bukkit.getConsoleSender().sendMessage(Text("&cPlease connect to the internet for the plugin to work!"));
    	Bukkit.getConsoleSender().sendMessage(Text("&7&m------------------------------------------------------------"));
    }
    
    //
}
