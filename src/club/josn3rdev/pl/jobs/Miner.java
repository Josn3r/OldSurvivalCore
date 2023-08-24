package club.josn3rdev.pl.jobs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.zones.Zones;

public class Miner implements Listener {
	
	private static Miner ins;
	
	public static ArrayList<Location> blocksRegen = new ArrayList<Location>();
	
	public static HashMap<Location, Integer> regenCount = new HashMap<Location, Integer>();
	public static HashMap<Location, Material> regenType = new HashMap<Location, Material>();
	
	public static HashMap<Player, Integer> mineralAcumulado = new HashMap<Player, Integer>();
	
	private String PREFIX = Tools.get().Text("&c[NPC] &ePedro &7» ");
	
	public static Miner get() {
		if (ins == null) {
			ins = new Miner();
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
		for (String locs : MSRP.miner.getStringList("config.blocks-to-regen")) {
			locations.add(locs);
		}
		for (String str : locations) {
			Location loc = Tools.get().setStringToLocBlock(str);
			blocksRegen.add(loc);
		}
		MSRP.debug("&7Se ha cargado correctamente el trabajo de Minero.");
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
                            	Material type = regenType.get(b);
                            	regenCount.remove(b);
                            	regenType.remove(b);
                            	Tools.get().playSound(b, Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                            	b.getBlock().setType(type);
                            }
                        }, 1L);
                    }
                    ++i;
                }
			}
		}, 0, 20L);
	}
		
	//
	
	@EventHandler
	public void onInteractEvent (PlayerInteractEvent e) {
		Player p = e.getPlayer();		
		if (Tools.get().existName(p, Tools.get().Text("&6Miner Selector"))) {
			// CLICK IZQUIERDO
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				e.setCancelled(true);
				blocksRegen.clear();
				
				ArrayList<String> locations = new ArrayList<String>();
				Material type = e.getClickedBlock().getType();
				Location loc = e.getClickedBlock().getLocation();
				
				for (String locs : MSRP.miner.getStringList("config.blocks-to-regen")) {
					locations.add(locs);
				}
				if (locations.contains(Tools.get().setLocToStringBlock(loc))) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse bloque ya está registrado en el trabajo de minero."));
				    return;
				}
				
				locations.add(Tools.get().setLocToStringBlock(loc));
				MSRP.miner.set("config.blocks-to-regen", locations);			
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &fSe ha guardado el bloque seleccionado."));
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + " &e" + type.name().toUpperCase() + " &7- " + Tools.get().setLocToStringBlock(loc)));
				
				for (String str : locations) {
					Location locLoad = Tools.get().setStringToLocBlock(str);
					blocksRegen.add(locLoad);
				}
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&7Se ha recargado la lista de bloques regenerables. (" + blocksRegen.size() + ")"));
			}
			// CLICK DERECHO
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				blocksRegen.clear();
				
				ArrayList<String> locations = new ArrayList<String>();
				Material type = e.getClickedBlock().getType();
				Location loc = e.getClickedBlock().getLocation();
				
				for (String locs : MSRP.miner.getStringList("config.blocks-to-regen")) {
					locations.add(locs);
				}
				
				if (!locations.contains(Tools.get().setLocToStringBlock(loc))) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse bloque no está registrado en el trabajo de minero."));
				    return;
				}
				
				locations.remove(Tools.get().setLocToStringBlock(loc));
				MSRP.miner.set("config.blocks-to-regen", locations);			
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha eliminado el bloque seleccionado."));
			    p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&e" + type.name().toUpperCase() + " &7- " + Tools.get().setLocToStringBlock(loc)));
				
				for (String str : locations) {
					Location locLoad = Tools.get().setStringToLocBlock(str);
					blocksRegen.add(locLoad);
				}
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&7Se ha recargado la lista de bloques regenerables. (" + blocksRegen.size() + ")"));
			}
			
		}
		
	}
	
	@EventHandler
	public void onBlockBreakEvent (BlockBreakEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (blocksRegen.contains(e.getBlock().getLocation())) {
			e.setCancelled(true);
			
			if (sp.getJob1() != Jobs.MINER && sp.getJob2() != Jobs.MINER) {
				p.sendMessage(Tools.get().Text(PREFIX + "&cNo perteneces al equipo minero, no puedes romper eso."));
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
						
			if (!isCorrectType(p, e.getBlock())) {
				p.sendMessage(" ");
				p.sendMessage(Tools.get().Text(PREFIX + "&cEse no es el mineral que te encargué!"));
				p.sendMessage(Tools.get().Text(PREFIX + "&cDebes traerme &e" + correctType(p)));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
				return;
			}
			
			if (mineralAcumulado.containsKey(p)) {
				if (getAcumulado(p) >= needTala(p)) {
					p.sendMessage(Tools.get().Text(PREFIX + "&cYa cumpliste la misión! Interactúa conmigo y cobra tu sueldo."));
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					return;
				}
			}
			regenCount.put(e.getBlock().getLocation(), 3);
			regenType.put(e.getBlock().getLocation(), e.getBlock().getType());
			
			e.getBlock().setType(Material.BEDROCK);			
			addAcumulado(p);
			Tools.get().sendActionBar(p, Tools.get().Text("&eMineral minado: &f" + getAcumulado(p) + "&7/" + needTala(p)));
			
		}
		
			
	}
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String jobName = MSRP.lang.getString("messages.jobs.miner.displayname");
		
		if (playerInZone(p)) {
			if (sp.getZone() == Zones.MINER) {
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
			sp.setZone(Zones.MINER);
		} else {
			if (sp.getZone() != Zones.MINER) {
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
	
	
	//
	
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
			p.sendMessage(Tools.get().Text(Woodcutter.get().getPrefix() + "&fTienes &e" + getAcumulado(p) + " &fmineral acumulado, te pedí &e" + needTala(p)));
			Tools.get().playSound(p, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
			return;
		}
		
		Double payment = MSRP.get().getConfig().getDouble("config.jobs.missions.miner." + mision + ".payment");
		Tools.get().playSound(p, Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.0f);
				
		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		p.sendMessage(Tools.get().Text(PREFIX + "&aCompletaste la misión, toma tu paga!"));
		p.sendMessage(Tools.get().Text("&fRecibiste un pago de &a$" + Tools.get().formatMoney(payment) + " &fde parte de &c" + PREFIX.substring(0, PREFIX.length()-5)));
		
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
		mineralAcumulado.put(p, 0);
	}
	
	public void addAcumulado (Player p) {
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		if (!mineralAcumulado.containsKey(p)) {
			mineralAcumulado.put(p, 0);
		}
		mineralAcumulado.put(p, mineralAcumulado.get(p).intValue()+1);
	}
	
	public Integer getAcumulado (Player p) {
		return mineralAcumulado.get(p).intValue();
	}
	
	public Integer needTala (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Integer value = 0;
		if (sp.getActualJob() != null) {
			value = MSRP.get().getConfig().getInt("config.jobs.missions.miner." + sp.getActualJob() + ".amount");
		}
		return value;
	}
	
	public String correctType (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		if (sp.getActualJob() != null) {
			Material type = Material.getMaterial(MSRP.get().getConfig().getString("config.jobs.missions.miner." + sp.getActualJob() + ".mineralType").toUpperCase());
			return type.name().toUpperCase();
		}
		return "";
	}
	
	public Boolean isCorrectType (Player p, Block block) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		if (sp.getActualJob() != null) {
			Material type = Material.getMaterial(MSRP.get().getConfig().getString("config.jobs.missions.miner." + sp.getActualJob() + ".mineralType").toUpperCase());
			if (type == block.getType()) {
				return true;
			}
		}
		return false;
	}
	
	///
	
	public boolean playerInZone (Player p) {
		if (!MSRP.miner.isSet("config.location-job.corner1") && !MSRP.miner.isSet("config.location-job.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.miner.get("config.location-job.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.miner.get("config.location-job.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}

}
