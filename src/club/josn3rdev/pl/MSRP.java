package club.josn3rdev.pl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.live.bemmamin.gps.api.GPSAPI;

import club.josn3rdev.pl.cmds.JobCmd;
import club.josn3rdev.pl.cmds.ProtectionCMD;
import club.josn3rdev.pl.cmds.RPCmds;
import club.josn3rdev.pl.clanes.ClanesManager;
import club.josn3rdev.pl.cmds.BankCMD;
import club.josn3rdev.pl.cmds.ClanCMD;
import club.josn3rdev.pl.cmds.ComprarCMD;
import club.josn3rdev.pl.cmds.DepartamentCMD;
import club.josn3rdev.pl.cmds.LevelupCMD;
import club.josn3rdev.pl.cmds.MarryCMD;
import club.josn3rdev.pl.cmds.StatsCMD;
import club.josn3rdev.pl.events.PlayerListener;
import club.josn3rdev.pl.hooks.PlaceholderAPIFork;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.jobs.GarbageCollector;
import club.josn3rdev.pl.jobs.Harvester;
import club.josn3rdev.pl.jobs.Miner;
import club.josn3rdev.pl.jobs.Woodcutter;
import club.josn3rdev.pl.marry.listener.KissListener;
import club.josn3rdev.pl.marry.listener.PVPListener;
import club.josn3rdev.pl.marry.listener.RideListener;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.protections.ProtectionListener;
import club.josn3rdev.pl.protections.ProtectionManager;
import club.josn3rdev.pl.task.DepartmentHologramUpdate;
import club.josn3rdev.pl.task.PaydayTask;
import club.josn3rdev.pl.utils.Config;
import club.josn3rdev.pl.utils.Tools;
import club.josn3rdev.pl.database.Stats;
import club.josn3rdev.pl.database.sqlite.Database;
import club.josn3rdev.pl.database.sqlite.SQLite;
import club.josn3rdev.pl.departaments.Departament;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class MSRP extends JavaPlugin {

	private static MSRP ins;
	
	public static Config lang,bank,departament;
	public static Config talador,miner,harvester,garbage;
	private GPSAPI gpsapi = null;
	private Database db;
	
	public static Economy econ;
	public static Chat chat;
	
	public static String PREFIX;
	
	public void onEnable() {
		ins = this;
		
		// LOAD CONFIGURATIONS
		getConfig();
	    saveDefaultConfig();
	    
	    lang = new Config(this, "lang");
	    bank = new Config(this, "banks");
	    departament = new Config(this, "departaments");
		
	    talador = new Config(this, "woodcutter");
	    miner = new Config(this, "miner");
	    harvester = new Config(this, "harvester");
	    garbage = new Config(this, "garbage");
	    
		PREFIX = Tools.get().Text(lang.getString("prefix"));
		
		// LOAD MANAGERS
        Tools.get().executeAuthme();
        if (Tools.get().blocked) {
        	return;
        }
        
		// START SQLite
	    this.db = new SQLite(this);
	    this.db.load();
	    
		// VAULT HOOK
		Vault.setupEconomy();
		Vault.setupChat();
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderAPIFork(MSRP.get()).register();
		}
		
		if (Bukkit.getPluginManager().getPlugin("GPS").isEnabled()) {
            gpsapi = new GPSAPI(this);
        }
		
		// REG CMDS && EVENTS
		getCommand("minespazioroleplay").setExecutor(new RPCmds());
		getCommand("hora").setExecutor(new RPCmds());
		getCommand("jobs").setExecutor(new JobCmd());
		getCommand("protection").setExecutor(new ProtectionCMD());
		
		getCommand("openbank").setExecutor(new BankCMD());
		getCommand("setstats").setExecutor(new StatsCMD());
		getCommand("levelup").setExecutor(new LevelupCMD());	
		
		getCommand("departament").setExecutor(new DepartamentCMD());
		getCommand("comprar").setExecutor(new ComprarCMD());
		getCommand("vender").setExecutor(new ComprarCMD());
		getCommand("aceptar").setExecutor(new ComprarCMD());
		getCommand("alquilar").setExecutor(new ComprarCMD());
		getCommand("opciones").setExecutor(new ComprarCMD());
		getCommand("hospital").setExecutor(new ComprarCMD());
		
		getCommand("clan").setExecutor(new ClanCMD());
		
		getCommand("marry").setExecutor(new MarryCMD());
		
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ProtectionListener(), this);
		getServer().getPluginManager().registerEvents(new Departament(), this);
		
		getServer().getPluginManager().registerEvents(new Woodcutter(), this);
		getServer().getPluginManager().registerEvents(new Miner(), this);
		getServer().getPluginManager().registerEvents(new Harvester(), this);
		getServer().getPluginManager().registerEvents(new GarbageCollector(), this);
		
		getServer().getPluginManager().registerEvents(new KissListener(), this);
		getServer().getPluginManager().registerEvents(new PVPListener(), this);
		getServer().getPluginManager().registerEvents(new RideListener(), this);
		
		// Load Jobs
		Woodcutter.get().load();
		Miner.get().load();
		Harvester.get().load();
		GarbageCollector.get().load();
		
		Departament.get().load();		
		
		ClanesManager.get().loadClans();
		ProtectionManager.get().loadProtections();
		
		new PaydayTask(this).loadPayday();
		new DepartmentHologramUpdate().load();
		
		// RELOAD
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (PlayerManager.get().getPlayer(p.getUniqueId()) == null) {
						PlayerManager.get().createPlayer(p);
					}
					SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
			        Stats.get().loadStats(p);
			        
			        sp.setBossbar("", 0.0D);
			        sp.updateBossbar();
				}
			}
		}.runTaskLater(this, 1L);
	}

	public void onDisable() {		
		for (String depa : Departament.get().getEdificios()) {
			Departament.get().deleteEdificioHologram(depa);
			if (!Departament.get().getRentals().get(depa).isEmpty()) {
				for (String retals : Departament.get().getRentals().get(depa)) {
					Departament.get().deleteRentalHologram(depa, retals);
				}
			}
		}		
		gpsapi.removeAllPoints();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
			Stats.get().savePlayer(player);
			sp.removeBossbar();
			PlayerManager.get().removePlayer(player);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//
	
	public static void debug (String str) {
		Bukkit.getConsoleSender().sendMessage(Tools.get().Text("&7[&6&lMSRP Debug&7] » &r" + str));
	}
	
	public static MSRP get() {
		return ins;
	}
	
	public Database getDatabase() {
		return db;
	}

	public GPSAPI getGPSApi() {
		return gpsapi;
	}
	
}
