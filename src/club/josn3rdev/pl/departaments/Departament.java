package club.josn3rdev.pl.departaments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.menu.departament.AlquilerConfig;
import club.josn3rdev.pl.menu.departament.BuildingConfig;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Cuboid;
import club.josn3rdev.pl.utils.Tools;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

public class Departament implements Listener {
	
	private static Departament ins;
	
	private ArrayList<String> edificios = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> rentals = new HashMap<String, ArrayList<String>>();
	
	private ArrayList<Material> interactBlocks = new ArrayList<Material>();
	
	public static Departament get() {
		if (ins == null) {
			ins = new Departament();
		}
		return ins;
	}
	
	public void load() {
		if (!MSRP.departament.isSet("departaments.edificios")) {
			MSRP.debug("&7No se cargó ningún departamente registrado o alquileres.");
			return;
		}
		for (String str : MSRP.departament.getConfig().getConfigurationSection("departaments.edificios").getKeys(false)) {
			edificios.add(str);
			if (MSRP.departament.isSet("departaments.edificios." + str + ".rentals")) {
				ArrayList<String> rents = new ArrayList<String>();
				rents.clear();
				for (String rentas : MSRP.departament.getConfig().getConfigurationSection("departaments.edificios." + str + ".rentals").getKeys(false)) {
					rents.add(rentas);
					if (MSRP.departament.isSet("departaments.edificios." + str + ".rentals." + rentas + ".location.hologramLoc")) {
						Location loc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + str  + ".rentals." + rentas + ".location.hologramLoc"));
						createRentalHologram(loc, str, rentas);
					}
				}
				rentals.put(str, rents);
			}
			
			if (MSRP.departament.isSet("departaments.edificios." + str + ".location.hologramLoc")) {
				Location loc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + str + ".location.hologramLoc"));
				createEdificioHologram(loc, str);
			}
			MSRP.debug("&7Departament loaded: &f" + str + " &7- Rentals: &f" + (rentals.get(str) != null ? rentals.get(str).size() : 0));
		}
	}
	
	public ArrayList<String> getEdificios() {
		return edificios;
	}
	
	public HashMap<String, ArrayList<String>> getRentals() {
		return rentals;
	}
	
	/*
	 * EVENTOS
	 */
	
	@EventHandler
	public void onBlockBreakEvent (BlockBreakEvent e) {
		Player p = e.getPlayer();		
		for (String depa : Departament.get().getEdificios()) {
			for (String rentals : Departament.get().getRentals().get(depa)) {
				if (isRentalZone(e.getBlock().getLocation(), depa, rentals)) {
					if (!p.hasPermission("msrp.admin")) {
						e.setCancelled(true);
						Tools.get().sendActionBar(p, (Tools.get().Text(MSRP.PREFIX + "&cNo puedes romper nada en una habitación alquilada.")));			
					}			
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent (BlockPlaceEvent e) {
		Player p = e.getPlayer();		
		for (String depa : Departament.get().getEdificios()) {
			for (String rentals : Departament.get().getRentals().get(depa)) {
				if (isRentalZone(e.getBlockPlaced().getLocation(), depa, rentals)) {
					if (!p.hasPermission("msrp.admin")) {
						e.setCancelled(true);
						Tools.get().sendActionBar(p, (Tools.get().Text(MSRP.PREFIX + "&cNo puedes construir en una habitación alquilada.")));			
					}	
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent (PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block block = e.getClickedBlock();
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND)) {
			for (String depa : Departament.get().getEdificios()) {
				for (String rentals : Departament.get().getRentals().get(depa)) {
					if (isRentalZone(block.getLocation(), depa, rentals)) {
						List<String> owners = MSRP.departament.getStringList("departaments.edificios." + depa + ".rentals." + rentals + ".Rented");
				    	if (!owners.isEmpty()) {
				    		for (String str : owners) {
				    			String uid = str.split(" : ")[1];
				    			if (!p.getUniqueId().toString().equals(uid)) {
				    				e.setCancelled(true);
				    				Tools.get().sendActionBar(p, (Tools.get().Text(MSRP.PREFIX + "&cNo eres propietario de esta habitación.")));
				    			} else {
				    				e.setCancelled(false);
				    			}
				    		}
				    	} else {
				    		if (!p.hasPermission("msrp.mod")) {
				    			e.setCancelled(true);
					    		Tools.get().sendActionBar(p, (Tools.get().Text(MSRP.PREFIX + "&cNo eres propietario de esta habitación.")));
				    		}
				    	}					
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent (PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		//if (e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockX() != e.getTo().getBlockX()) {
			for (String depa : Departament.get().getEdificios()) {
				if (sp.getDepaZone() == null) {
					if (playerEdificioZone(p, depa)) {
						sp.setDepaZone(depa);
						Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
						Tools.get().sendTitle(p, "&6&l"+depa, "&fAlquiler de departamentos!", 20);
					}
				} else {
					
					if (sp.getDepaZone().contains(depa)) {
						if (!playerEdificioZone(p, depa)) {
							Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
							sp.setDepaZone(null);
							return;
						}
					}
					
					for (String rentals : Departament.get().getRentals().get(depa)) {
						if (playerRentalZone(p, depa, rentals)) {
							if (sp.getDepaZone().equals(depa)) {
								sp.setDepaZone(depa+"-"+rentals);
								Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
								Tools.get().sendTitle(p, "&6&l"+rentals, "&fHabitación", 20);
							}
							break;
						} else {
							if (sp.getDepaZone().equals(depa+"-"+rentals)) {
								sp.setDepaZone(depa);
							}
						}				
					}
				}
			}
		//}
		
	}
	
	/*
	 * EDIFICIOS Y RENTAS - FUNCIONES
	 */
	
	public void createEdificio (Player p, String edifName) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (sp.getCorner1() == null && sp.getCorner2() == null) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fDebes seleccionar los dos corners primero!"));
			return;
		}
		
		if (MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fYa existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		String loc1 = Tools.get().setLocToStringBlock(sp.getCorner1());			
		String loc2 = Tools.get().setLocToStringBlock(sp.getCorner2());
		
		MSRP.departament.set("departaments.edificios." + edifName + ".owner.name", "NONE");
		MSRP.departament.set("departaments.edificios." + edifName + ".owner.uuid", "NONE");
		MSRP.departament.set("departaments.edificios." + edifName + ".sellPrice", 20000000.0);
		MSRP.departament.set("departaments.edificios." + edifName + ".minLevel", 7);
		MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", true);
		
		MSRP.departament.set("departaments.edificios." + edifName + ".location.corner1", loc1);
		MSRP.departament.set("departaments.edificios." + edifName + ".location.corner2", loc2);
		
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha creado correctamente el edificio &e" + edifName));
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fUsa el comando &e/departament hologram building set " + edifName + " &fpara colocar un holograma."));
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
	}
	
	public void editRegionEdificio (Player p, String edifName) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (sp.getCorner1() == null && sp.getCorner2() == null) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fDebes seleccionar los dos corners primero!"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		String loc1 = Tools.get().setLocToStringBlock(sp.getCorner1());			
		String loc2 = Tools.get().setLocToStringBlock(sp.getCorner2());
		
		MSRP.departament.set("departaments.edificios." + edifName + ".location.corner1", loc1);
		MSRP.departament.set("departaments.edificios." + edifName + ".location.corner2", loc2);
		
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha editado correctamente la región del edificio &e" + edifName));
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
	}
	
	public void createRental (Player p, String rentalCode, String edifName) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (sp.getCorner1() == null && sp.getCorner2() == null) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fDebes seleccionar los dos corners primero!"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		if (MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fYa existe un alquiler con ese código. &c(" + rentalCode + ")"));
			return;
		}
		
		String loc1 = Tools.get().setLocToStringBlock(sp.getCorner1());			
		String loc2 = Tools.get().setLocToStringBlock(sp.getCorner2());
		
		ArrayList<String> lista = new ArrayList<String>();
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".Rented", lista);
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".rental-cost", 4500.0);
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".rental-time", 0);
		
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1", loc1);
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2", loc2);
		
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha creado correctamente la renta &e" + rentalCode + " &fen el edificio &e" + edifName));
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fUsa el comando &e/departament hologram rental set " + edifName + " " + rentalCode + " &fpara colocar un holograma."));
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
	}
	
	public void editRegionRental (Player p, String rentalCode, String edifName) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		if (sp.getCorner1() == null && sp.getCorner2() == null) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fDebes seleccionar los dos corners primero!"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un alquiler con ese código. &c(" + rentalCode + ")"));
			return;
		}
		
		String loc1 = Tools.get().setLocToStringBlock(sp.getCorner1());			
		String loc2 = Tools.get().setLocToStringBlock(sp.getCorner2());
		
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1", loc1);
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2", loc2);
		
		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fSe ha editado correctamente la región de la renta &e" + rentalCode + " &fen el edificio &e" + edifName));
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
	}
	
	public void setEdificioHologram (Player p, String edifName) {
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		String loc = Tools.get().setLocToString(p);	
		MSRP.departament.set("departaments.edificios." + edifName + ".location.hologramLoc", loc);
		createEdificioHologram(p.getLocation(), edifName);
	}
	
	public void moveEdificioHologram (Player p, String edifName) {
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		String loc = Tools.get().setLocToString(p);	
		MSRP.departament.set("departaments.edificios." + edifName + ".location.hologramLoc", loc);
		setMoveEdificioHologram(p.getLocation(), edifName);
	}
	
	public void setRentalHologram (Player p, String edifName, String rentalCode) {
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			MSRP.debug("&c&lERROR » &fNo existe un codigo de renta con ese nombre. &c(" + rentalCode + ")");
			return;
		}
    	
		
		String loc = Tools.get().setLocToString(p);	
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.hologramLoc", loc);
		createRentalHologram(p.getLocation(), edifName, rentalCode);
	}
	
	public void moveRentalHologram (Player p, String edifName, String rentalCode) {
		if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			p.sendMessage(Tools.get().Text("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")"));
			return;
		}
		
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			MSRP.debug("&c&lERROR » &fNo existe un codigo de renta con ese nombre. &c(" + rentalCode + ")");
			return;
		}
    	
		String loc = Tools.get().setLocToString(p);	
		MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.hologramLoc", loc);
		setMoveRentalHologram(p.getLocation(), edifName, rentalCode);
	}
	
	public boolean playerEdificioZone (Player p, String edifName) {	
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".location.corner1") && !MSRP.departament.isSet("departaments.edificios." + edifName + ".location.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".location.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".location.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}
	
	public boolean isEdificioZone (Location loc, String edifName) {	
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".location.corner1") && !MSRP.departament.isSet("departaments.edificios." + edifName + ".location.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".location.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".location.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(loc)) {
    		return true;
    	}    	
    	return false;
	}
	
	public boolean playerRentalZone (Player p, String edifName, String rentalCode) {	
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1") && !MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(p.getLocation())) {
    		return true;
    	}    	
    	return false;
	}
	
	public boolean isRentalZone (Location loc, String edifName, String rentalCode) {	
		if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1") && !MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2")) {
			return false;
		}
		Location max = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner1"));
    	Location min = Tools.get().setStringToLocBlock(MSRP.departament.get("departaments.edificios." + edifName + ".rentals." + rentalCode + ".location.corner2"));
    	Cuboid cuboid = new Cuboid(max.add(1, 0.0, 1), min);
    	if (cuboid.containsLocation(loc)) {
    		return true;
    	}    	
    	return false;
	}
	
	//
	// HOLOGRAMS
	//
	
	public void createEdificioHologram (Location loc, String edifName) {
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	
    	if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			MSRP.debug("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")");
			return;
		}
    	
    	String owner = MSRP.departament.getString("departaments.edificios." + edifName + ".owner.name");
    	Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
    	Integer minLevel = MSRP.departament.getInt("departaments.edificios." + edifName + ".minLevel");
    	Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
    	
    	String sellingStr = MSRP.lang.getString("messages.departaments.buildings.variables.selling." + isSelling.toString().toLowerCase());
    	
    	Integer alquileresTotales = Departament.get().getRentals().get(edifName).size();
		Integer alquileresOcupados = 0;
		Integer alquileresDisponibles = 0;
		for (String rentas : Departament.get().getRentals().get(edifName)) {
			List<String> owned = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentas + ".Rented");
			if (!owned.isEmpty()) {
				++alquileresOcupados;
			}
		}
		alquileresDisponibles = (alquileresTotales - alquileresOcupados);
		
    	
    	if (dhologramEnable) {
    		List<String> lineas = new ArrayList<String>();
        	
    		if (isSelling) {
    			for (String str : MSRP.lang.getStringList("messages.departaments.buildings.hologram-selling")) {
        			str = str.replace("<NAME>", edifName)
        					.replace("<OWNER>", ""+owner)
        					.replace("<TOTAL_DEPARTAMENTS>", ""+alquileresTotales)
        					.replace("<OWNED_DEPARTAMENTS>", ""+alquileresOcupados)
        					.replace("<AVAILABLE_DEPARTAMENTS>", ""+alquileresDisponibles)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<NEED_LEVEL>", ""+minLevel)
        					.replace("<SELLING>", ""+sellingStr);
        			lineas.add(Tools.get().Text(str));
        		}
    		} else {
    			for (String str : MSRP.lang.getStringList("messages.departaments.buildings.hologram-owned")) {
        			str = str.replace("<NAME>", edifName)
        					.replace("<OWNER>", ""+owner)
        					.replace("<TOTAL_DEPARTAMENTS>", ""+alquileresTotales)
        					.replace("<OWNED_DEPARTAMENTS>", ""+alquileresOcupados)
        					.replace("<AVAILABLE_DEPARTAMENTS>", ""+alquileresDisponibles)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<NEED_LEVEL>", ""+minLevel)
        					.replace("<SELLING>", ""+sellingStr);
        			lineas.add(Tools.get().Text(str));
        		}
    		}
    		DHAPI.createHologram(edifName, loc.add(0.0, 0.0, 0.0), lineas);
    	}
    }

	public void setMoveEdificioHologram (Location loc, String edifName) {
		Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	if (dhologramEnable) {
    		Hologram hologram = DHAPI.getHologram(edifName);
    		if (hologram != null) {
    			hologram.setLocation(loc);
    		}
    	}
	}
	
	public void updateEdificioHologram (String edifName) {
    	
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	
    	String owner = MSRP.departament.getString("departaments.edificios." + edifName + ".owner.name");
    	Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
    	Integer minLevel = MSRP.departament.getInt("departaments.edificios." + edifName + ".minLevel");
    	Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
    	
    	String sellingStr = MSRP.lang.getString("messages.departaments.buildings.variables.selling." + isSelling.toString().toLowerCase());
    	
    	Integer alquileresTotales = Departament.get().getRentals().get(edifName).size();
		Integer alquileresOcupados = 0;
		Integer alquileresDisponibles = 0;
		for (String rentas : Departament.get().getRentals().get(edifName)) {
			List<String> owned = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentas + ".Rented");
			if (!owned.isEmpty()) {
				++alquileresOcupados;
			}
		}
		alquileresDisponibles = (alquileresTotales - alquileresOcupados);
		
		
    	if (dhologramEnable) {
    		List<String> lineas = new ArrayList<String>();
    		if (isSelling) {
    			for (String str : MSRP.lang.getStringList("messages.departaments.buildings.hologram-selling")) {
        			str = str.replace("<NAME>", edifName)
        					.replace("<OWNER>", ""+owner)
        					.replace("<TOTAL_DEPARTAMENTS>", ""+alquileresTotales)
        					.replace("<OWNED_DEPARTAMENTS>", ""+alquileresOcupados)
        					.replace("<AVAILABLE_DEPARTAMENTS>", ""+alquileresDisponibles)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<NEED_LEVEL>", ""+minLevel)
        					.replace("<SELLING>", ""+sellingStr);
        			lineas.add(Tools.get().Text(str));
        		}
    		} else {
    			for (String str : MSRP.lang.getStringList("messages.departaments.buildings.hologram-owned")) {
        			str = str.replace("<NAME>", edifName)
        					.replace("<OWNER>", ""+owner)
        					.replace("<TOTAL_DEPARTAMENTS>", ""+alquileresTotales)
        					.replace("<OWNED_DEPARTAMENTS>", ""+alquileresOcupados)
        					.replace("<AVAILABLE_DEPARTAMENTS>", ""+alquileresDisponibles)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<NEED_LEVEL>", ""+minLevel)
        					.replace("<SELLING>", ""+sellingStr);
        			lineas.add(Tools.get().Text(str));
        		}
    		}
    		Hologram hologram = DHAPI.getHologram(edifName);
    		if (hologram == null) {
    			Location loc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName + ".location.hologramLoc"));
				createEdificioHologram(loc, edifName);
    		}
    		DHAPI.setHologramLines(hologram, lineas);
    	}
    }
    
    
    public void deleteEdificioHologram(String edifName) {
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	if (dhologramEnable) {
    		Hologram hologram = DHAPI.getHologram(edifName);
    		if (hologram != null) {
    			hologram.delete();
    		}
    	}
    }

    
    public void createRentalHologram (Location loc, String edifName, String rentalCode) {
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	
    	if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			MSRP.debug("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")");
			return;
		}
    	
    	if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			MSRP.debug("&c&lERROR » &fNo existe un codigo de renta con ese nombre. &c(" + rentalCode + ")");
			return;
		}
    	
    	List<String> owners = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentalCode + ".Rented");
    	Double price = MSRP.departament.getDouble("departaments.edificios." + edifName  + ".rentals." + rentalCode + ".rental-cost");
    	Integer time = MSRP.departament.getInt("departaments.edificios." + edifName  + ".rentals." + rentalCode + ".rental-time");
    	
    	Boolean isSelling = true;
    	String ownerNames = "";
    	if (!owners.isEmpty()) {
    		isSelling = false;
        	for (String str : owners) {
        		String nick = str.split(" : ")[0];
        		ownerNames = ownerNames + nick + ", ";
        	}
        	ownerNames = ownerNames.substring(0, ownerNames.length() - 2)  + ".";
    	}
    	
    	if (dhologramEnable) {
    		List<String> lineas = new ArrayList<String>();
        	
    		if (isSelling) {
    			for (String str : MSRP.lang.getStringList("messages.departaments.rentals.hologram-rent")) {
        			str = str.replace("<RENT_CODE>", rentalCode)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price));
        			lineas.add(Tools.get().Text(str));
        		}
    		} else {
    			for (String str : MSRP.lang.getStringList("messages.departaments.rentals.hologram-owned")) {
        			str = str.replace("<RENT_CODE>", rentalCode)
        					.replace("<OWNER>", ""+ownerNames)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<RENT_TIME>", ""+Tools.get().getFormatTime(time));
        			lineas.add(Tools.get().Text(str));
        		}
    		}
    		DHAPI.createHologram(edifName+"_"+rentalCode, loc.add(0.0, 0.0, 0.0), lineas);
    	}
    }
        
    public void setMoveRentalHologram (Location loc, String edifName, String rentalCode) {
		Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	if (dhologramEnable) {
    		Hologram hologram = DHAPI.getHologram(edifName+"_"+rentalCode);
    		if (hologram != null) {
    			hologram.setLocation(loc);
    		}
    	}
	}
    
    
    public void updateRentalHologram (String edifName, String rentalCode) {
    	
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	
    	if (!MSRP.departament.isSet("departaments.edificios." + edifName)) {
			MSRP.debug("&c&lERROR » &fNo existe un edificio con ese nombre. &c(" + edifName + ")");
			return;
		}
    	
    	if (!MSRP.departament.isSet("departaments.edificios." + edifName + ".rentals." + rentalCode)) {
			MSRP.debug("&c&lERROR » &fNo existe un codigo de renta con ese nombre. &c(" + rentalCode + ")");
			return;
		}
    	
    	List<String> owners = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentalCode + ".Rented");
    	Double price = MSRP.departament.getDouble("departaments.edificios." + edifName  + ".rentals." + rentalCode + ".rental-cost");
    	Integer time = MSRP.departament.getInt("departaments.edificios." + edifName  + ".rentals." + rentalCode + ".rental-time");
    	
    	Boolean isSelling = true;
    	String ownerNames = "";
    	if (!owners.isEmpty()) {
    		isSelling = false;
        	for (String str : owners) {
        		String nick = str.split(" : ")[0];
        		ownerNames = ownerNames + nick + ", ";
        	}
        	ownerNames = ownerNames.substring(0, ownerNames.length() - 2)  + ".";
    	}
    	
    	if (dhologramEnable) {
    		List<String> lineas = new ArrayList<String>();
    		if (isSelling) {
    			for (String str : MSRP.lang.getStringList("messages.departaments.rentals.hologram-rent")) {
        			str = str.replace("<RENT_CODE>", rentalCode)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price));
        			lineas.add(Tools.get().Text(str));
        		}
    		} else {
    			for (String str : MSRP.lang.getStringList("messages.departaments.rentals.hologram-owned")) {
        			str = str.replace("<RENT_CODE>", rentalCode)
        					.replace("<OWNER>", ""+ownerNames)
        					.replace("<PRICE>", ""+Tools.get().formatMoney(price))
        					.replace("<RENT_TIME>", ""+Tools.get().getFormatTime(time));
        			lineas.add(Tools.get().Text(str));
        		}
    		}
    		
    		Hologram hologram = DHAPI.getHologram(edifName+"_"+rentalCode);
    		if (hologram == null) {
    			Location loc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName  + ".rentals." + rentalCode + ".location.hologramLoc"));
				createRentalHologram(loc, edifName, rentalCode);
    		}
    		DHAPI.setHologramLines(hologram, lineas);
    	}
    }
    
    
    public void deleteRentalHologram(String edifName, String rentalCode) {
    	Boolean dhologramEnable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    	if (dhologramEnable) {
    		Hologram hologram = DHAPI.getHologram(edifName+"_"+rentalCode);
    		if (hologram != null) {
    			hologram.delete();
    		}
    	}
    }
    
    /*
     * COMPRAR - VENDER EDIFICIO
     */
    
    public void buyBuilding (Player p) {
    	SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
    	Location pLoc = p.getLocation();
    	
    	for (String edifName : getEdificios()) {
    		Location holoLoc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName  + ".location.hologramLoc"));
    		if (playerInRadius(holoLoc, pLoc)) {
    			Boolean isSelling = MSRP.departament.getBoolean("departaments.edificios." + edifName + ".isSelling");
            	
        		if (isSelling) {
        			Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".sellPrice");
                	Integer minLevel = MSRP.departament.getInt("departaments.edificios." + edifName + ".minLevel");
                	
                	if (sp.getLevel() < minLevel) {
                		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes el nivel necesario para comprar este edificio."));
                		Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                		return;
                	}
                	
                	if (Vault.getMoney(p) < price) {
                		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes el dinero suficiente para comprar este edificio."));
                		Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                		return;
                	}
                	
                	Vault.removeMoney(p, price);
                	
                	MSRP.departament.set("departaments.edificios." + edifName + ".owner.name", p.getName());
            		MSRP.departament.set("departaments.edificios." + edifName + ".owner.uuid", p.getUniqueId().toString());
            		MSRP.departament.set("departaments.edificios." + edifName + ".isSelling", false);
            		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHaz comprado el edificio &e" + edifName + "&f. Ahora eres propietario del edificio."));
            		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        		} else {
        			p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEse edificio no está en venta."));
        			Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        		}
    		}
    	}
    }
    
    /*
     * ALQUILAR UN DEPARTAMENTO
     */
    
    public void startAndPayAlquiler (Player p) {
    	//SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
    	Location pLoc = p.getLocation();
    	
    	for (String edifName : getEdificios()) {
    		for (String rentas : getRentals().get(edifName)) {
    			Location holoLoc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName  + ".rentals." + rentas + ".location.hologramLoc"));
        		
    			if (playerInRadius(holoLoc, pLoc)) {
        			List<String> rented = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentas + ".Rented");
                	if (!rented.isEmpty()) {
                		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cEsa habitación ya fue rentada por alguien más."));
            			Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                		return;
                	}
            		
                	Double price = MSRP.departament.getDouble("departaments.edificios." + edifName + ".rentals." + rentas + ".rental-cost");
                	
                	if (Vault.getMoney(p) < price) {
                		p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes el dinero suficiente para alquilar esta habitación."));
                		Tools.get().playSound(p, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                		return;
                	}
                	
                	Vault.removeMoney(p, price);
                	
                	String register = p.getName() + " : " + p.getUniqueId().toString();
                	rented.add(register);
                	MSRP.departament.set("departaments.edificios." + edifName + ".rentals." + rentas + ".Rented", rented);
                	
                	p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fHaz alquilado una habitación en el edificio &e" + edifName + "&f. Número de habitación: &e" + rentas + "&f."));
            		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        		}
    		}
    	}
    }
    
    /*
     * OPEN OPTIONS
     */
    
    public void checkPosition (Player p) {
    	Location pLoc = p.getLocation();
    	
    	for (String edifName : getEdificios()) {
    		Location holoLoc = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName  + ".location.hologramLoc"));
    		if (playerInRadius(holoLoc, pLoc)) {
    			String ownerUID = MSRP.departament.getString("departaments.edificios." + edifName + ".owner.uuid");
    			if (p.getUniqueId().toString().equals(ownerUID)) {
    				new BuildingConfig(p, edifName).open(p);
    				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fAbriendo configuración del edificio &e" + edifName + "&f."));
            		break;
    			}
    		}
    		for (String rentas : getRentals().get(edifName)) {
    			Location holoLocRent = Tools.get().setStringToLoc(MSRP.departament.getString("departaments.edificios." + edifName  + ".rentals." + rentas + ".location.hologramLoc"));
    			if (playerInRadius(holoLocRent, pLoc)) {
    				List<String> rented = MSRP.departament.getStringList("departaments.edificios." + edifName + ".rentals." + rentas + ".Rented");
                	if (!rented.isEmpty()) {
                		for (String str : rented) {
                			String rentOwnerUID = str.split(" : ")[1];
                			if (p.getUniqueId().toString().equals(rentOwnerUID)) {
                				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&fAbriendo configuración del alquiler &e" + rentas + "&f. &7(" + edifName + ")"));
                        		new AlquilerConfig(p, edifName, rentas).open(p);
                				break;
                			}
                		}
                	}
    			}
    		}
    	}
    	
    	
    }
        
    public boolean playerInRadius (Location loc, Location locPlayer) {
		int X = loc.getBlockX();
    	int Y = loc.getBlockY();
    	int Z = loc.getBlockZ();		
		Location max = new Location(loc.getWorld(), X + 2, Y + 1, Z + 2);
    	Location min = new Location(loc.getWorld(), X - 2, Y - 4, Z - 2);
    	Cuboid cuboid = new Cuboid(max, min);
    	if (cuboid.containsLocation(locPlayer)) {
    		return true;
    	}    	
    	return false;
	}
    
    
    /**
     * 
     * OTHERS
     * 
     */
    
    
    
    public void loadInteractBlocks() {
    	interactBlocks.add(Material.ACACIA_DOOR);
    	interactBlocks.add(Material.ACACIA_TRAPDOOR);
    	interactBlocks.add(Material.BIRCH_DOOR);
    	interactBlocks.add(Material.BIRCH_TRAPDOOR);
    	interactBlocks.add(Material.CRIMSON_DOOR);
    	interactBlocks.add(Material.CRIMSON_TRAPDOOR);
    	interactBlocks.add(Material.DARK_OAK_DOOR);
    	interactBlocks.add(Material.DARK_OAK_TRAPDOOR);
    	interactBlocks.add(Material.IRON_DOOR);
    	interactBlocks.add(Material.IRON_TRAPDOOR);
    	interactBlocks.add(Material.JUNGLE_DOOR);
    	interactBlocks.add(Material.JUNGLE_TRAPDOOR);
    	interactBlocks.add(Material.OAK_DOOR);
    	interactBlocks.add(Material.OAK_TRAPDOOR);
    	interactBlocks.add(Material.SPRUCE_DOOR);
    	interactBlocks.add(Material.SPRUCE_TRAPDOOR);
    	interactBlocks.add(Material.WARPED_DOOR);
    	interactBlocks.add(Material.WARPED_TRAPDOOR);
    	interactBlocks.add(Material.MANGROVE_DOOR);
    	interactBlocks.add(Material.MANGROVE_TRAPDOOR);
    	interactBlocks.add(Material.CHEST);
    	interactBlocks.add(Material.CRAFTING_TABLE);
    	interactBlocks.add(Material.ANVIL);
    	interactBlocks.add(Material.CHIPPED_ANVIL);
    	interactBlocks.add(Material.DAMAGED_ANVIL);
    	interactBlocks.add(Material.ACACIA_BUTTON);
    	interactBlocks.add(Material.BIRCH_BUTTON);
    	interactBlocks.add(Material.CRIMSON_BUTTON);
    	interactBlocks.add(Material.DARK_OAK_BUTTON);
    	interactBlocks.add(Material.JUNGLE_BUTTON);
    	interactBlocks.add(Material.MANGROVE_BUTTON);
    	interactBlocks.add(Material.OAK_BUTTON);
    	interactBlocks.add(Material.POLISHED_BLACKSTONE_BUTTON);
    	interactBlocks.add(Material.SPRUCE_BUTTON);
    	interactBlocks.add(Material.STONE_BUTTON);
    	interactBlocks.add(Material.WARPED_BUTTON);
    	interactBlocks.add(Material.BEACON);
    	interactBlocks.add(Material.FURNACE);
    	interactBlocks.add(Material.ENDER_CHEST);
    	interactBlocks.add(Material.ENCHANTING_TABLE);
    	interactBlocks.add(Material.BLACK_BED);
    	interactBlocks.add(Material.BLUE_BED);
    	interactBlocks.add(Material.BROWN_BED);
    	interactBlocks.add(Material.CYAN_BED);
    	interactBlocks.add(Material.GRAY_BED);
    	interactBlocks.add(Material.GREEN_BED);
    	interactBlocks.add(Material.LIGHT_BLUE_BED);
    	interactBlocks.add(Material.LIGHT_GRAY_BED);
    	interactBlocks.add(Material.LIME_BED);
    	interactBlocks.add(Material.MAGENTA_BED);
    	interactBlocks.add(Material.ORANGE_BED);
    	interactBlocks.add(Material.PINK_BED);
    	interactBlocks.add(Material.PURPLE_BED);
    	interactBlocks.add(Material.RED_BED);
    	interactBlocks.add(Material.WHITE_BED);
    	interactBlocks.add(Material.YELLOW_BED);
    	interactBlocks.add(Material.SMOKER);
    	interactBlocks.add(Material.JUKEBOX);
    	interactBlocks.add(Material.LOOM);
    	interactBlocks.add(Material.BLAST_FURNACE);
    	interactBlocks.add(Material.CARTOGRAPHY_TABLE);
    	interactBlocks.add(Material.FLETCHING_TABLE);
    	interactBlocks.add(Material.SMITHING_TABLE);
    	interactBlocks.add(Material.NOTE_BLOCK);
    	interactBlocks.add(Material.BARREL);
    	interactBlocks.add(Material.GRINDSTONE);
    	interactBlocks.add(Material.BELL);
    	interactBlocks.add(Material.LODESTONE);
    	interactBlocks.add(Material.RESPAWN_ANCHOR);
    	interactBlocks.add(Material.DROPPER);
    	interactBlocks.add(Material.DISPENSER);
    	interactBlocks.add(Material.OBSERVER);
    	interactBlocks.add(Material.ACACIA_FENCE_GATE);
    	interactBlocks.add(Material.BIRCH_FENCE_GATE);
    	interactBlocks.add(Material.CRIMSON_FENCE_GATE);
    	interactBlocks.add(Material.DARK_OAK_FENCE_GATE);
    	interactBlocks.add(Material.JUNGLE_FENCE_GATE);
    	interactBlocks.add(Material.MANGROVE_FENCE_GATE);
    	interactBlocks.add(Material.OAK_FENCE_GATE);
    	interactBlocks.add(Material.SPRUCE_FENCE_GATE);
    	interactBlocks.add(Material.WARPED_FENCE_GATE);
    	interactBlocks.add(Material.LEVER);
    	interactBlocks.add(Material.REPEATER);
    	interactBlocks.add(Material.COMPARATOR);
    	interactBlocks.add(Material.HOPPER);
    	interactBlocks.add(Material.ITEM_FRAME);
    	interactBlocks.add(Material.GLOW_ITEM_FRAME);
    }
    
}
