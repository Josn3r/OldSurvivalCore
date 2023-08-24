package club.josn3rdev.pl.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.controller.Nivel;

public class PaydayTask {

	private MSRP core;
		
	public PaydayTask (MSRP core) {
		this.core = core;
	}
	
	private List<String> paydayHours = new ArrayList<String>();
	private List<String> bPaydayHours = new ArrayList<String>();
	private List<String> bankPaydayHours = new ArrayList<String>();
	private List<String> resetHours = new ArrayList<String>();
	
	public void loadPayday() {
		String hour;
		for (int i = 0; i < 24; ++i) {
			if (i < 10) {
				hour = "0" + i;
			} else { hour = ""+i; }
			paydayHours.add(hour + ":00:00");
		}
		for (int i = 0; i < 24; ++i) {
			if (i < 10) {
				hour = "0" + i;
			} else { hour = ""+i; }
			bPaydayHours.add(hour + ":02:30");
		}
		for (int i = 0; i < 24; ++i) {
			if (i < 10) {
				hour = "0" + i;
			} else { hour = ""+i; }
			bankPaydayHours.add(hour + ":05:00");
		}
		
		for (int i = 0; i < 24; ++i) {
			if (i < 10) {
				hour = "0" + i;
			} else { hour = ""+i; }
			resetHours.add(hour + ":22:30");
		}
		
		this.startPaydayRunnable();
	}
	
	@SuppressWarnings("deprecation")
	public void startPaydayRunnable () {
		MSRP.get().getServer().getScheduler().scheduleAsyncRepeatingTask(MSRP.get(), new Runnable() {
			@Override
			public void run() {				
				for (String hour : paydayHours) {
					if (hour.equals(getHour())) {
						for (Player p : MSRP.get().getServer().getOnlinePlayers()) {
							Nivel.get().givePayday(p);
						}
					}
				}
				for (String hour : bPaydayHours) {
					if (hour.equals(getHour())) {
						for (Player p1 : Bukkit.getOnlinePlayers()) {
							Bank.get().playerBankInterests(p1);
						}
					}
				}
				for (String hour : bankPaydayHours) {
					if (hour.equals(getHour())) {
						Bank.get().bankPayday();
					}
				}
				
				for (String hour : resetHours) {
					if (hour.equals(getHour())) {
						//MSRP.debug("Todos los cheques del banco fueron eliminados... Un total de " + Bank.get().getPagosDiarios() + " cheques no fueron reclamados por los jugadores.");
						Bank.get().getPagosDiarios().clear();
					}
				}
			}			
		}, 0L, 20L);
	}	

	public String getHour() {		
		Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");    	
		return format.format(now);
	}

	public MSRP getCore() {
		return core;
	}
	
}
