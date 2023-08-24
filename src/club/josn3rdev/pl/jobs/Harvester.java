package club.josn3rdev.pl.jobs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.zones.Zones;

public class Harvester implements Listener {

	private static Harvester ins;
	
	public static ArrayList<Location> blocksRegen = new ArrayList<Location>();	
	public static HashMap<Player, Integer> harvestAcumulado = new HashMap<Player, Integer>();
	
	private String PREFIX = Tools.get().Text("&c[NPC] &eMaria &7» ");
	
	public static Harvester get() {
		if (ins == null) {
			ins = new Harvester();
		}
		return ins;
	}
	
	//
	
	public String getPrefix() {
		return PREFIX;
	}
		
	public void load() {
    	loadConfigurations();
    }
	
	private static void loadConfigurations() {
		ArrayList<String> locations = new ArrayList<String>();
		for (String locs : MSRP.harvester.getStringList("config.blocks-to-regen")) {
			locations.add(locs);
		}
		for (String str : locations) {
			Location loc = Tools.get().setStringToLocBlock(str);
			blocksRegen.add(loc);
		}
		MSRP.debug("&7Se ha cargado correctamente el trabajo de Harvest.");
	}
	
	//
	
	@EventHandler
	public void onInteractEvent (PlayerInteractEvent e) {
		Player p = e.getPlayer();		
		if (Tools.get().existName(p, Tools.get().Text("&6Harvest Selector"))) {
			// CLICK IZQUIERDO
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				e.setCancelled(true);
				blocksRegen.clear();
				
				ArrayList<String> locations = new ArrayList<String>();
				Material type = e.getClickedBlock().getType();
				Location loc = e.getClickedBlock().getLocation();
				
				for (String locs : MSRP.harvester.getStringList("config.blocks-to-regen")) {
					locations.add(locs);
				}
				if (locations.contains(Tools.get().setLocToStringBlock(loc))) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse bloque ya está registrado en el trabajo de minero."));
				    return;
				}
				
				locations.add(Tools.get().setLocToStringBlock(loc));
				MSRP.harvester.set("config.blocks-to-regen", locations);			
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
				
				for (String locs : MSRP.harvester.getStringList("config.blocks-to-regen")) {
					locations.add(locs);
				}
				
				if (!locations.contains(Tools.get().setLocToStringBlock(loc))) {
					p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse bloque no está registrado en el trabajo de minero."));
				    return;
				}
				
				locations.remove(Tools.get().setLocToStringBlock(loc));
				MSRP.harvester.set("config.blocks-to-regen", locations);			
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
	public void onPlayerInteractEventWork (PlayerInteractEvent e) {
		Player p = e.getPlayer();
		//SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (p.isSneaking() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
			Block block = e.getClickedBlock();			
			if (block.getBlockData() instanceof Ageable) {
				Ageable ageable = (Ageable)block.getBlockData();
				
				if (blocksRegen.contains(block.getLocation())) {
					e.setCancelled(true);
					
					int age = ageable.getAge();
					int maxAge = ageable.getMaximumAge();
					String cropType = block.getType().toString();
					cropType = cropType.toLowerCase();
					cropType = cropType.replaceFirst(String.valueOf(cropType.charAt(0)), String.valueOf(Character.toUpperCase(cropType.charAt(0))));
					p.sendMessage(Tools.get().Text(PREFIX + " &fCrecimiento de &e" + cropType + " &7[&f" + age + "&7/&f" + maxAge + "&7]"));
				}
			}
		}
		
	}
	
	@EventHandler
	public void onBlockBreakEvent (BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
	    Location bloc = block.getLocation();
	    
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (blocksRegen.contains(bloc)) {
			e.setCancelled(true);
			if (block.getBlockData() instanceof Ageable) {
				Ageable ageable = (Ageable)block.getBlockData();
				int age = ageable.getAge();
				int maxAge = ageable.getMaximumAge();
				
				if (sp.getJob1() != Jobs.HARVESTER && sp.getJob2() != Jobs.HARVESTER) {
					p.sendMessage(Tools.get().Text(PREFIX + "&cNo perteneces al equipo cosechadores, no puedes romper eso."));
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
				
				//
				
				if (!isCorrectType(p, block)) {
					p.sendMessage(" ");
					p.sendMessage(Tools.get().Text(PREFIX + "&cEso no es lo que te encargué!"));
					p.sendMessage(Tools.get().Text(PREFIX + "&cDebes traerme &e" + correctType(p)));
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					return;
				}
				
				if (age < maxAge) {
					p.sendMessage(Tools.get().Text(PREFIX + "&cSi es lo que necesito pero debes esperar a que crezca!"));
					Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
					return;
				}
				
				if (harvestAcumulado.containsKey(p)) {
					if (getAcumulado(p) >= needTala(p)) {
						p.sendMessage(Tools.get().Text(PREFIX + "&cYa cumpliste la misión! Interactúa conmigo y cobra tu sueldo."));
						Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
						return;
					}
				}
				
				block.getDrops().clear();
				ageable.setAge(1);
				block.setBlockData((BlockData)ageable);
				setBlock(bloc, block);
				
				addAcumulado(p);
				Tools.get().sendActionBar(p, Tools.get().Text("&eCosechado: &f" + getAcumulado(p) + "&7/" + needTala(p)));
			}	
		}
	}
	
	public void setBlock(Location location, Block block) {
	    location.getBlock().setType(block.getType());
	  }
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String jobName = MSRP.lang.getString("messages.jobs.harvester.displayname");
		
		if (playerInZone(p)) {
			if (sp.getZone() == Zones.HARVESTER) {
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
			sp.setZone(Zones.HARVESTER);
		} else {
			if (sp.getZone() != Zones.HARVESTER) {
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
		
		Double payment = MSRP.get().getConfig().getDouble("config.jobs.missions.harvester." + mision + ".payment");
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
		harvestAcumulado.put(p, 0);
	}
	
	public void addAcumulado (Player p) {
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		if (!harvestAcumulado.containsKey(p)) {
			harvestAcumulado.put(p, 0);
		}
		harvestAcumulado.put(p, harvestAcumulado.get(p).intValue()+1);
	}
	
	public Integer getAcumulado (Player p) {
		return harvestAcumulado.get(p).intValue();
	}
	
	public Integer needTala (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Integer value = 0;
		if (sp.getActualJob() != null) {
			value = MSRP.get().getConfig().getInt("config.jobs.missions.harvester." + sp.getActualJob() + ".amount");
		}
		return value;
	}
	
	public String correctType (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		if (sp.getActualJob() != null) {
			Material type = Material.getMaterial(MSRP.get().getConfig().getString("config.jobs.missions.harvester." + sp.getActualJob() + ".harvestType").toUpperCase());
			
			String cropType = type.name().toLowerCase();
			cropType = cropType.replaceFirst(String.valueOf(cropType.charAt(0)), String.valueOf(Character.toUpperCase(cropType.charAt(0))));
	          
	          
			return cropType;
		}
		return "";
	}
	
	public Boolean isCorrectType (Player p, Block block) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		if (sp.getActualJob() != null) {
			Material type = Material.getMaterial(MSRP.get().getConfig().getString("config.jobs.missions.harvester." + sp.getActualJob() + ".harvestType").toUpperCase());
			if (type == block.getType()) {
				return true;
			}
			
		}
		return false;
	}
	
	///
	
	public boolean playerInZone (Player p) {	
		if (!MSRP.harvester.isSet("config.location-job.corner1") && !MSRP.harvester.isSet("config.location-job.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.harvester.get("config.location-job.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.harvester.get("config.location-job.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}
	
}
