package club.josn3rdev.pl.jobs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.zones.Zones;

public class Woodcutter implements Listener {

	private static Woodcutter ins;
    public MSRP plugin;
	
    public static ArrayList<Location> blocksRegen = new ArrayList<Location>();
	public static HashMap<Location, Integer> regenCount = new HashMap<Location, Integer>();
	
	public static HashMap<Player, Integer> maderaAcumulada = new HashMap<Player, Integer>();
	
	public String PREFIX = Tools.get().Text("&c[NPC] &fJorge &7» &r");

    public static Woodcutter get() {
        if (ins == null) {
        	ins = new Woodcutter();
        }
        return ins;
    }
    
    public String getPrefix() {
    	return PREFIX;
    }
	
    public void load() {
    	loadConfigurations();
		startRegenCount();
    }
    
	private static void loadConfigurations() {
		ArrayList<String> locations = new ArrayList<String>();
		for (String locs : MSRP.talador.getStringList("config.blocks-to-regen")) {
			locations.add(locs);
		}
		for (String str : locations) {
			Location loc = Tools.get().setStringToLocBlock(str);
			blocksRegen.add(loc);
			loc.getBlock().setType(Material.OAK_LOG);
		}
		MSRP.debug("&7Se ha cargado correctamente el trabajo de Talador.");
	}

	private static void startRegenCount() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(MSRP.get(), new Runnable() {
			@Override
			public void run() {
				ArrayList<Location> oldblock = new ArrayList<Location>(regenCount.keySet());
                int i = 0;
                while (i < oldblock.size()) {
                    final Location b = oldblock.get(i);
                    Integer time = regenCount.get(b);
                    if (time > 0) {
                    	regenCount.put(b, time - 1);
                    } else {
                        Bukkit.getScheduler().runTaskLater(MSRP.get(), new Runnable(){
                            @Override
                            public void run() {
                            	regenCount.remove(b);
                            	Tools.get().playSound(b, Sound.BLOCK_WOOD_PLACE, 1.0f, 1.0f);
                            	b.getBlock().setType(Material.OAK_LOG);
                            }
                        }, 1L);
                    }
                    ++i;
                }
			}
		}, 0, 20L);
	}
	
	@EventHandler
	public void onBlockBreak (BlockBreakEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (blocksRegen.contains(e.getBlock().getLocation())) {
			e.setCancelled(true);
			
			if (sp.getJob1() != Jobs.WOODCUTTER && sp.getJob2()  != Jobs.WOODCUTTER) {
				p.sendMessage(Tools.get().Text(PREFIX + "&cNo tienes el trabajo de talador, no puedes cortar ese arbol."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
				return;
			}
			
			Boolean missionActive = false;
			if (getMission(p) != null) {
				missionActive = true;
			}
			
			if (!missionActive) {				
 				p.sendMessage(" ");
				p.sendMessage(Tools.get().Text(PREFIX + "&cNo tienes ningún trabajo o misión para cumplir."));
				p.sendMessage(Tools.get().Text(PREFIX + "&cVen a interactuar conmigo y empieza a trabajar! "));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
				return;
			}
						
			if (maderaAcumulada.containsKey(p)) {
				if (getAcumulado(p) >= needTala(p)) {
					p.sendMessage(Tools.get().Text(PREFIX + "&cYa cumpliste la misión! Interactúa conmigo y cobra tu sueldo."));
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					return;
				}
			}
			regenCount.put(e.getBlock().getLocation(), 30);
			e.getBlock().setType(Material.AIR);			
			addAcumulado(p);
			Tools.get().sendActionBar(p, Tools.get().Text("&eMadera Cortada: &f" + getAcumulado(p) + "&7/" + needTala(p)));
		}
	}
	
	@EventHandler
	public void onBlockPlace (BlockPlaceEvent e) {
		Player p = e.getPlayer();		
		if (Tools.get().existName(p, "&6Woodcutter Logs")) {
			blocksRegen.clear();
			
			ArrayList<String> locations = new ArrayList<String>();
			for (String locs : MSRP.talador.getStringList("config.blocks-to-regen")) {
				locations.add(locs);
			}
			String locBlock = Tools.get().setLocToStringBlock(e.getBlock().getLocation());
			locations.add(locBlock);
			MSRP.talador.set("config.blocks-to-regen", locations);			
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&a&l+1 &fnuevo bloque registrado al trabajo de Leñador."));
			
			for (String str : locations) {
				Location loc = Tools.get().setStringToLocBlock(str);
				blocksRegen.add(loc);
			}
			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&7Se ha recargado la lista de bloques regenerables. (" + blocksRegen.size() + ")"));
			
		}
 	}
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		String jobName = MSRP.lang.getString("messages.jobs.woodcutter.displayname");
		
		if (playerInZone(p)) {
			if (sp.getZone() == Zones.WOODCUTTER) {
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
			sp.setZone(Zones.WOODCUTTER);
		} else {
			if (sp.getZone() != Zones.WOODCUTTER) {
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
			if (getMission(p) != null) {
				cancelMission(p);
				p.sendMessage(Tools.get().Text(PREFIX + "&cAbandonaste la zona de trabajo, he cancelado tu misión."));
			}
			sp.setZone(Zones.NONE);
		}
		
	}
	
	/**
	 * 
	 * 
	 */
	
	public void setMission (Player p, String mission) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		sp.setActualJob(mission);
		resetAcumulado(p);
	}
	
	public String getMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		return sp.getActualJob();
	}
	
	public void finishMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String mision = sp.getActualJob();
		
		if (getAcumulado(p) < needTala(p)) {
			p.sendMessage(Tools.get().Text(Woodcutter.get().getPrefix() + "&cYa tienes una mision activa, terminala!"));
			p.sendMessage(Tools.get().Text(Woodcutter.get().getPrefix() + "&fTienes &e" + getAcumulado(p) + " &fmadera acumulada, te pedí &e" + needTala(p)));
			Tools.get().playSound(p, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
			return;
		}
		
		Double payment = MSRP.get().getConfig().getDouble("config.jobs.missions.woodcutter." + mision + ".payment");
		Tools.get().playSound(p, Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
				
		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		p.sendMessage(Tools.get().Text(PREFIX + "&aCompletaste la misión, toma tu paga!"));
		p.sendMessage(Tools.get().Text("&fRecibiste un pago de &a$" + Tools.get().formatMoney(payment) + " &fde parte de &c[NPC] &eJorge&f."));
		
		sp.setActualJob(null);
		resetAcumulado(p);
	}
	
	public void cancelMission (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Tools.get().playSound(p, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
		sp.setActualJob(null);
		resetAcumulado(p);
	}
	
	//
	
	public void resetAcumulado(Player p) {
		maderaAcumulada.put(p, 0);
	}
	
	public void addAcumulado (Player p) {
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		if (!maderaAcumulada.containsKey(p)) {
			maderaAcumulada.put(p, 0);
		}
		maderaAcumulada.put(p, maderaAcumulada.get(p).intValue()+1);
	}
	
	public Integer getAcumulado (Player p) {
		return maderaAcumulada.get(p).intValue();
	}
	
	public Integer needTala (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Integer value = 0;
		if (sp.getActualJob() != null) {
			value = MSRP.get().getConfig().getInt("config.jobs.missions.woodcutter." + sp.getActualJob() + ".amount");
		}
		return value;
	}
	
	public boolean playerInZone (Player p) {
		if (!MSRP.talador.isSet("config.location-job.corner1") && !MSRP.talador.isSet("config.location-job.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.talador.get("config.location-job.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.talador.get("config.location-job.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}
}
