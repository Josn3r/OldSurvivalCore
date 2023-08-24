package club.josn3rdev.pl.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Nivel;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class StatsCMD implements CommandExecutor {
 	
	public String Text (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		
		CommandSender p = sender;
		 	
		if (cmd.getName().equalsIgnoreCase("setstats")) {
			if (!p.hasPermission("msrp.admin")) {
				p.sendMessage(Tools.get().Text(MSRP.PREFIX + "&cNo tienes permiso para usar ese comando."));
				return true;
			}
			
			if (args.length == 0) {
				p.sendMessage(Text("&7- Correct usage: /setstats [level/exp] [player] [value]"));
				p.sendMessage(Text("&7- Correct usage: /setstats [bLevel] [player] [value]"));
				p.sendMessage(Text("&7- Correct usage: /setstats [addBankBalance(abb)/takeBankBalance(tbb)] [player] [value]"));
				p.sendMessage(Text("&7- Correct usage: /setstats getBankInfo [player]"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("getBankInfo")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats getBankInfo [player]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				
				if (player != null) {
					SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
					p.sendMessage(Text("&6&lBANK INFO &7- &e" + args[1])); 
					p.sendMessage(Text("&f» Bank Level &7- &e" + ss.getBankLevel())); 
					p.sendMessage(Text("&f» Bank Balance &7- &6&l$&e" + Tools.get().formatMoney(ss.getBankBalance()))); 
					
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("bLevel")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats bLevel [player] [value]"));
					return true;
				}
				
				if (args.length == 2) {
					p.sendMessage(Text("&7- Correct usage: /setstats bLevel [player] [value]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				String valueStr = args[2];
				
				if (player != null) {
					if (Tools.get().isInt(valueStr)) {
						SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
						Integer value = Integer.valueOf(valueStr);
						Integer maxLevel = 10;
						
						if (value > maxLevel) {
							p.sendMessage(Text("&cMax level: " + maxLevel));
							return true;
						}
						
						ss.setBankLevel(value);
						p.sendMessage(Text("&a- Changing &fBANK LEVEL &aof &f" + args[1] + " &ato &f" + value)); 
					} else {
						p.sendMessage(Text("&cValue only integers..."));
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("addBankBalance") || args[0].equalsIgnoreCase("abb")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats addBankBalance [player] [value]"));
					return true;
				}
				
				if (args.length == 2) {
					p.sendMessage(Text("&7- Correct usage: /setstats addBankBalance [player] [value]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				String valueStr = args[2];
				
				if (player != null) {
					if (Tools.get().isDouble(valueStr)) {
						SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
						Double value = Double.valueOf(valueStr);	
						
						ss.setBankBalance(ss.getBankBalance() + value);
						p.sendMessage(Text("&a- Adding &fBANK BALANCE &aof &f" + args[1] + " &ato &f$" + Tools.get().formatMoney(value))); 
					} else {
						p.sendMessage(Text("&cValue only integers..."));
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("takeBankBalance") || args[0].equalsIgnoreCase("tbb")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats takeBankBalance [player] [value]"));
					return true;
				}
				
				if (args.length == 2) {
					p.sendMessage(Text("&7- Correct usage: /setstats takeBankBalance [player] [value]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				String valueStr = args[2];
				
				if (player != null) {
					if (Tools.get().isDouble(valueStr)) {
						SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
						Double value = Double.valueOf(valueStr);	
						
						ss.setBankBalance(ss.getBankBalance() - value);
						if (ss.getBankBalance() < 0.0) {
							ss.setBankBalance(0.0);
						}
						p.sendMessage(Text("&a- Taking &fBANK BALANCE &aof &f" + args[1] + " &ato &f$" + Tools.get().formatMoney(value))); 
					} else {
						p.sendMessage(Text("&cValue only integers..."));
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			
			//
			
			if (args[0].equalsIgnoreCase("addlevel")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats addlevel [player]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				
				if (player != null) {
					SPlayer sp = PlayerManager.get().getPlayer(player.getUniqueId());
					
					Integer playerLevel = sp.getLevel();
					Integer maxLevel = MSRP.get().getConfig().getStringList("config.levels").size();
					
					if ((playerLevel + 1) > maxLevel) {
						p.sendMessage(Text("&cEl jugador ya está en el nivel máximo: " + maxLevel));
						return true;
					}
					
					sp.setLevel(playerLevel+1);
					p.sendMessage(Text("&a- Changing &fLEVEL &aof &f" + args[1] + " &ato &f" + (playerLevel + 1))); 
					
					for (Player o : Bukkit.getOnlinePlayers()) {
						SPlayer ss = PlayerManager.get().getPlayer(o.getUniqueId());
						ss.updateBossbar();
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("addexp")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats addexp [player]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				
				if (player != null) {
					Nivel.get().giveExp(player);
					p.sendMessage(Text("&a- Add &fEXP &aof &f" + args[1] + "&f."));
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("level")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats level [player] [value]"));
					return true;
				}
				
				if (args.length == 2) {
					p.sendMessage(Text("&7- Correct usage: /setstats level [player] [value]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				String valueStr = args[2];
				
				if (player != null) {
					if (Tools.get().isInt(valueStr)) {
						SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
						Integer value = Integer.valueOf(valueStr);
						Integer maxLevel = MSRP.get().getConfig().getStringList("config.levels").size();
						
						if (value > maxLevel) {
							p.sendMessage(Text("&cMax level: " + maxLevel));
							return true;
						}
						
						ss.setLevel(value);
						p.sendMessage(Text("&a- Changing &fLEVEL &aof &f" + args[1] + " &ato &f" + value)); 
						ss.updateBossbar();
					} else {
						p.sendMessage(Text("&cValue only integers..."));
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("exp")) {
				if (args.length == 1) {
					p.sendMessage(Text("&7- Correct usage: /setstats exp [player] [value]"));
					return true;
				}
				
				if (args.length == 2) {
					p.sendMessage(Text("&7- Correct usage: /setstats exp [player] [value]"));
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[1]);
				String valueStr = args[2];
				
				if (player != null) {
					if (Tools.get().isInt(valueStr)) {
						SPlayer ss = PlayerManager.get().getPlayer(player.getUniqueId());
						Integer value = Integer.valueOf(valueStr);
						Integer maxExp = Nivel.get().getNeededExpToLevelUp(player);
						
						if (value > maxExp) {
							p.sendMessage(Text("&cMax Exp: " + maxExp));
							return true;
						}
						
						ss.setExp(value);
						ss.setTotalExp(ss.getTotalExp() + value);
						p.sendMessage(Text("&a- Changing &fEXP &aof &f" + args[1] + " &ato &f" + value)); 
						ss.updateBossbar();
					} else {
						p.sendMessage(Text("&cValue only integers..."));
					}
				} else {
					p.sendMessage(Text("&cThat player not is online!"));
				}
				return true;
			}
			
		}
		
		return false;
	}

}
