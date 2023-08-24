package club.josn3rdev.pl.marry.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import club.josn3rdev.pl.marry.MarryManager;

public class RideListener implements Listener {
	
	@EventHandler
	public void onCoupleRide(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return; 
		Player vehicle = e.getPlayer();
		Player lover = (Player)e.getRightClicked();
		UUID uuid = MarryManager.get().getRelationship(vehicle.getUniqueId());
		if (uuid == null)
			return; 
		if (!uuid.equals(lover.getUniqueId()))
			return; 
		UUID vehicleUUID = vehicle.getUniqueId();
		UUID loverUUID = lover.getUniqueId();
		if (vehicleUUID.equals(loverUUID))
			return; 
		if (!vehicle.isSneaking())
			return; 
		vehicle.getWorld().playSound(vehicle.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
		addPassengerToTheTop(vehicleUUID, loverUUID);
	}
  
	@EventHandler
	public void onTakeOffPassenger(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		if (player.getPassengers().isEmpty())
			return; 
		Entity passengerEntity = player.getPassengers().get(0);
		if (player.getPassengers().isEmpty())
			return; 
		if (!(passengerEntity instanceof Player))
			return; 
		Player passenger = (Player)passengerEntity;
		if (player.isSneaking())
			return; 
		if (!player.getPassengers().contains(passenger))
			return; 
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LEASH_KNOT_BREAK, 1.0F, 1.0F);
		player.eject();
	}
    
	public void addPassengerToTheTop(UUID vehicleUUID, UUID passengerUUID) {
		Player vehicle = Bukkit.getPlayer(vehicleUUID);
		Player passenger = Bukkit.getPlayer(passengerUUID);
		if (vehicle.getPassengers().isEmpty()) {
			vehicle.addPassenger((Entity)passenger);
		} else {
			while (passenger.getUniqueId() != passengerUUID) {
				if (passenger.getPassengers().isEmpty()) {
					passenger.addPassenger((Entity)Bukkit.getPlayer(passengerUUID));
					break;
				} 
				passenger = (Player)passenger.getPassengers().get(0);
			} 
		} 
	}
	
}
