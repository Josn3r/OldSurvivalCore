package club.josn3rdev.pl.jobs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.menu.jobs.garbage.GarbageCreator;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.zones.Zones;

public class GarbageCollector implements Listener {

	private static GarbageCollector ins;	
	private String PREFIX = Tools.get().Text("&c[NPC] &eCarlos &7» ");
	
	public static GarbageCollector get() {
		if (ins == null) {
			ins = new GarbageCollector();
		}
		return ins;
	}
	
	//
	
	public String getPrefix() {
		return PREFIX;
	}
		
	public void load() {
    	for (String mision : MSRP.garbage.getConfig().getConfigurationSection("config.routes").getKeys(false)) {
    		Integer count = 0;
    		for (String str : MSRP.garbage.getStringList("config.routes." + mision + ".routePoints")) {
    			Location loc = Tools.get().setStringToLocBlock(str);
    			MSRP.get().getGPSApi().addPoint(mision + "-" + count, loc);
    			++count;
    		}
    	}
    	MSRP.get().getGPSApi().addPoint("garbage", Tools.get().setStringToLocBlock(MSRP.garbage.getString("config.location-job.location")));
    	MSRP.debug("&7Se ha cargado correctamente el trabajo de Garbage Collector.");
	}
		
	@EventHandler
	public void onBlockBreak (BlockBreakEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
	
		if (!sp.getGarbagePoints().isEmpty()) {
			if (e.getBlock().getType() == Material.BEACON) {
				String bLoc = Tools.get().setLocToStringBlock(e.getBlock().getLocation());
				if (sp.getGarbagePoints().contains(bLoc)) {
					sp.getGarbagePoints().remove(bLoc);
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&c&l-1 &fpunto de control eliminado."));
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHay un total de &e(" + sp.getGarbagePoints().size() + ") &fpuntos de control."));
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace (BlockPlaceEvent e) {
		Player p = e.getPlayer();		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (Tools.get().existName(p, "&6Garbage Route Point")) {
			String locBlock = Tools.get().setLocToStringBlock(e.getBlock().getLocation());
			sp.getGarbagePoints().add(locBlock);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&a&l+1 &fpunto de control registrado."));
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHay un total de &e(" + sp.getGarbagePoints().size() + ") &fpuntos de control."));
			
		}
		
		if (Tools.get().existName(p, "&6Garbage Zone")) {
			e.setCancelled(true);
			String locBlock = Tools.get().setLocToStringBlock(e.getBlock().getLocation());
			MSRP.garbage.set("config.location-job.location", locBlock);
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha colocado el punto de control de la zona."));
			p.getInventory().getItemInMainHand().setType(Material.AIR);
			new GarbageCreator(p).open(p);
		}
 	}
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String jobName = MSRP.lang.getString("messages.jobs.garbage.displayname");
		
		if (!sp.getActualJobPoints().isEmpty()) {
			if (sp.getJob1() == Jobs.GARBAGE || sp.getJob2() == Jobs.GARBAGE) {
				Location loc = Tools.get().setStringToLocBlock(sp.getActualJobPoints().get(sp.getGarbageJobCount()));
	        	if (playerIsInTheRadius(loc, p.getLocation())) {
	        		if (e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockX() != e.getTo().getBlockX()) {
						if (!sp.getGarbageShiftNotify()) {
							p.sendMessage(Tools.get().Text(PREFIX + "&fAgáchate &7[Shift] &fpara empezar a recoger la basura."));
							sp.setGarbageShiftNotify(true);
						}
					}
	        	}
			}
		}
		
		if (playerInZone(p)) {
			if (sp.getZone() == Zones.GARBAGE) {
				return;
			}
			if (MSRP.get().getConfig().getBoolean("config.jobs.join-work-area.notify")) {
				Tools.get().playSound(p, Sound.valueOf(MSRP.get().getConfig().getString("config.jobs.join-work-area.sound")), 1.0f, 1.0f);
				if (MSRP.get().getConfig().getBoolean("config.jobs.join-work-area.actionbar-notify")) {
					Tools.get().sendActionBar(p, Tools.get().Text(MSRP.lang.getString("messages.jobs.joinZone.actionbar").replace("<JOB>", jobName)));
				}
				if (MSRP.get().getConfig().getBoolean("config.jobs.leave-work-area.title-notify")) {
					String title = MSRP.lang.getString("messages.jobs.joinZone.title").split(" : ")[0];
					String subtitle = MSRP.lang.getString("messages.jobs.joinZone.title").split(" : ")[1];
					
					Tools.get().sendTitle(p, title.replace("<JOB>", jobName), subtitle, 20);
				}
			}			
			sp.setZone(Zones.GARBAGE);
		} else {
			if (sp.getZone() != Zones.GARBAGE) {
				return;
			}
			if (MSRP.get().getConfig().getBoolean("config.jobs.leave-work-area.notify")) {
				Tools.get().playSound(p, Sound.valueOf(MSRP.get().getConfig().getString("config.jobs.leave-work-area.sound")), 1.0f, 1.0f);
				if (MSRP.get().getConfig().getBoolean("config.jobs.leave-work-area.actionbar-notify")) {
					Tools.get().sendActionBar(p, Tools.get().Text(MSRP.lang.getString("messages.jobs.leaveZone.actionbar").replace("<JOB>", jobName)));
				}
				if (MSRP.get().getConfig().getBoolean("config.jobs.leave-work-area.title-notify")) {
					String title = MSRP.lang.getString("messages.jobs.leaveZone.title").split(" : ")[0];
					String subtitle = MSRP.lang.getString("messages.jobs.leaveZone.title").split(" : ")[1];
					
					Tools.get().sendTitle(p, title.replace("<JOB>", jobName), subtitle, 20);
				}
			}
			sp.setZone(Zones.NONE);
		}
	}
	
	@EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
        
        if (sp.getActualJob() != null) {
        	Location loc = Tools.get().setStringToLocBlock(sp.getActualJobPoints().get(sp.getGarbageJobCount()));
        	if (playerIsInTheRadius(loc, p.getLocation())) {
        		new BukkitRunnable() {
        			Double count = 5.0;        			
					@Override
					public void run() {
						if (!p.isSneaking()) {
							this.cancel();
						}
						if (count > 0.0) {
							count -= 0.1;							
							String barra = Tools.get().getProgressBar(count, 5.0, 20, '|', ChatColor.YELLOW, ChatColor.GRAY);
							Tools.get().sendActionBar(p, Tools.get().Text("&fRecogiendo Basura &7- [" + barra + "&7]"));
						} else {
							cancel();
							sp.setGarbageJobCount(sp.getGarbageJobCount()+1);
							sp.setGarbageShiftNotify(false);
							startMission(p);
						}
					}
        		}.runTaskTimerAsynchronously(MSRP.get(), 0, 2L);
        	}
        }        
    }
	
	//
	
	public void setMission (Player p, String mission) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		sp.setActualJob(mission);
		sp.setGarbageJobCount(0);
		for (String str : MSRP.garbage.getStringList("config.routes." + mission + ".routePoints")) {
			sp.getActualJobPoints().add(str);
		}
	}
		
	public void startMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		String routeName = MSRP.garbage.getString("config.routes." + sp.getActualJob() + ".routeName");
		
		if (sp.getGarbageJobCount() == sp.getActualJobPoints().size()) {
			finishMission(p);
			return;
		}
		if (sp.getGarbageJobCount() == 0) {
			p.sendMessage(Tools.get().Text(PREFIX + "&fPerfecto, seleccionaste la &e" + routeName + "&f."));
			p.sendMessage(Tools.get().Text(PREFIX + "&fSigue el GPS y dirígete al primer punto de limpieza."));
			Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
		} else {
			p.sendMessage(Tools.get().Text(PREFIX + "&fSigue el GPS y dirígete al siguiente punto de limpieza."));
			Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		}
		
		Bukkit.getScheduler().runTaskLater(MSRP.get(), new Runnable() {
			@Override
			public void run() {
				MSRP.get().getGPSApi().startGPS(p, sp.getActualJob() + "-" + sp.getGarbageJobCount());
			}
		}, 1L);
	}
	
	public void finishMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		String routeName = MSRP.garbage.getString("config.routes." + sp.getActualJob() + ".routeName");
		p.sendMessage(Tools.get().Text(PREFIX + "&fTerminaste la limpieza de la &e" + routeName + "&f."));
		p.sendMessage(Tools.get().Text(PREFIX + "&fSigue el GPS y regresa a la planta a cobrar tu sueldo."));
		Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
		Bukkit.getScheduler().runTaskLater(MSRP.get(), new Runnable() {
			@Override
			public void run() {
				MSRP.get().getGPSApi().startGPS(p, "garbage");
			}
		}, 1L);
	}
	
	public String getMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		return sp.getActualJob();
	}
	
	public void payPerJobPlayer (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String mision = sp.getActualJob();
		
		if (sp.getGarbageJobCount() < sp.getActualJobPoints().size()) {
			p.sendMessage(Tools.get().Text(Woodcutter.get().getPrefix() + "&cYa tienes una mision activa, terminala!"));
			Tools.get().playSound(p, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
			return;
		}
		
		String routeName = MSRP.garbage.getString("config.routes." + sp.getActualJob() + ".routeName");
		Double payment = MSRP.garbage.getDouble("config.routes." + mision + ".routePayment");
		Tools.get().playSound(p, Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
				
		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		p.sendMessage(Tools.get().Text(PREFIX + "&aCompletaste la &f" +routeName +"&a, toma tu paga!"));
		p.sendMessage(Tools.get().Text("&fRecibiste un pago de &a$" + Tools.get().formatMoney(payment) + " &fde parte de &c" + PREFIX.substring(0, PREFIX.length()-5)));
		
		sp.setActualJob(null);
		sp.setGarbageJobCount(0);
		sp.setGarbageShiftNotify(false);
		sp.getActualJobPoints().clear();
	}
	
	public void cancelMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Tools.get().playSound(p, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
		sp.setActualJob(null);
	}
	
	
				
	///
	
	public boolean playerInZone (Player p) {	
		if (!MSRP.garbage.isSet("config.location-job.corner1") && !MSRP.garbage.isSet("config.location-job.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.garbage.get("config.location-job.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.garbage.get("config.location-job.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
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
	
	// CREADOR DE RUTA
	
	public void createRoute (String routeName, Double payment, Integer jobLevel, Integer routeTime, ArrayList<String> points) {
		Integer createds = MSRP.garbage.getInt("config.routesCreated");
		MSRP.garbage.set("config.routes.route" + (createds + 1) + ".routeName", routeName);
		MSRP.garbage.set("config.routes.route" + (createds + 1) + ".routePayment", payment);
		MSRP.garbage.set("config.routes.route" + (createds + 1) + ".routeJobLevel", jobLevel);
		MSRP.garbage.set("config.routes.route" + (createds + 1) + ".routeTime", routeTime);
		MSRP.garbage.set("config.routes.route" + (createds + 1) + ".routePoints", points);
		
		MSRP.garbage.set("config.routesCreated", (createds + 1));
	}
	
	public void addRoutePoints (String route, String location) {
		ArrayList<String> points = new ArrayList<String>();
		for (String str : MSRP.garbage.getStringList("config.routes." + route + ".routePoints")) {
			points.add(str);
		}
		points.add(location);
		MSRP.garbage.set("config.routes." + route + ".routePoints", points);
	}
	
	public void saveRouteEnable(String route) {
		
	}
	
}
