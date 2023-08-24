package club.josn3rdev.pl.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.departaments.Departament;

public class DepartmentHologramUpdate {
		
	public DepartmentHologramUpdate () {}
	
	public void load() {
		this.startPaydayRunnable();
	}
	
	@SuppressWarnings("deprecation")
	public void startPaydayRunnable () {
		MSRP.get().getServer().getScheduler().scheduleAsyncRepeatingTask(MSRP.get(), new Runnable() {
			@Override
			public void run() {	
				for (String edifName : Departament.get().getEdificios()) {
					Departament.get().updateEdificioHologram(edifName);
					for (String rentas : Departament.get().getRentals().get(edifName)) {
						Departament.get().updateRentalHologram(edifName, rentas);
					}
				}
			}			
		}, 0L, 100L);
	}	

	public String getHour() {		
		Date now = new Date();
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");    	
		return format.format(now);
	}
	
}
