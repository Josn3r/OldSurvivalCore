package club.josn3rdev.pl.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerManager {
	
    private static PlayerManager playerManager;
    private Map<UUID, SPlayer> players = new HashMap<UUID, SPlayer>();

    public static PlayerManager get() {
        if (playerManager == null) {
            playerManager = new PlayerManager();
        }
        return playerManager;
    }

    public Map<UUID, SPlayer> getPlayers() {
        return this.players;
    }

    public SPlayer getPlayer(UUID uUID) {
        return this.players.get(uUID);
    }
    
    public boolean doesPlayerExists(UUID uUID) {
        return this.players.containsKey(uUID);
    }

    public void createPlayer(Player p) {
        this.players.put(p.getUniqueId(), new SPlayer(p.getUniqueId()));
    }

    public void createPlayer(OfflinePlayer p) {
        this.players.put(p.getUniqueId(), new SPlayer(p.getUniqueId()));
    }

    public void removePlayer(Player p) {
        this.players.remove(p.getUniqueId());
    }
    
    public Set<SPlayer> PlayersSet(Set<UUID> set) {
        HashSet<SPlayer> hashSet = new HashSet<SPlayer>();
        for (UUID uUID : set) {
            hashSet.add(this.getPlayer(uUID));
        }
        return hashSet;
    }
    
    public Set<SPlayer> PlayersSet() {
        HashSet<SPlayer> hashSet = new HashSet<SPlayer>();
        for (UUID uUID : getPlayers().keySet()) {
            hashSet.add(this.getPlayer(uUID));
        }
        return hashSet;
    }
}

