package club.josn3rdev.pl.controller;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class Nivel {
	
	private static Nivel ins;
	
	public static Nivel get() {
		if (ins == null) {
			ins = new Nivel();
		}
		return ins;
	}
	
	public String Text (String textToTranslate) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}
	
	//
	
	public void giveExp (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		Integer playerLevel = sp.getLevel();
		Integer playerExp = sp.getExp();
				
		Integer maxExp = 0;
		
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				maxExp = Integer.valueOf(loadLevels.split(" : ")[2]);
			}
		}
		
		if (playerExp < maxExp) {
			sp.setExp(sp.getExp() + 1);
			sp.setTotalExp(sp.getTotalExp() + 1);
		}
		sp.updateBossbar();
	}
	
	public void givePayday (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());

		if (sp.getLevel() <= 0) {
			MSRP.debug("&7[MSurvival Debug]- " + p.getName() + " tiene un fallo con su nivel (es menor a 1)... Corrigiendo.");
			sp.setLevel(1);
		}
		
		Integer playerLevel = sp.getLevel();
		Integer playerExp = sp.getExp();
		
		Integer maxExp = 0;
		Double payDay = 0.0;
		
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				payDay = Double.valueOf(loadLevels.split(" : ")[1]);
				maxExp = Integer.valueOf(loadLevels.split(" : ")[2]);
			}
		}
		
		if (playerExp < maxExp) {
			sp.setExp(sp.getExp() + 1);
			sp.setTotalExp(sp.getTotalExp() + 1);
		}
				
    	Double tax = 8.0;
    	Double calcTax = Tools.get().calcPercent(payDay, tax);
    	Double totalCalc = (payDay - calcTax);
    	
    	Vault.setMoney(p, totalCalc);
    	    	
		for (String msg : MSRP.lang.getStringList("messages.payday.receive-payday")) {
			msg = msg.replace("<PAYDAY>", ""+Tools.get().formatMoney(payDay))
					 .replace("<PAYDAY_TAX>", ""+calcTax)
					 .replace("<TOTAL_CALC>", ""+Tools.get().formatMoney(totalCalc));
			if (MSRP.lang.getBoolean("messages.payday.sendCenteredMessage")) {
				Tools.get().sendCenteredMessage(p, Text(msg));
			} else {
				p.sendMessage(Text(msg));
			}
			
		}
		sp.updateBossbar();
		Tools.get().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
	}
	
	public Integer getNeededExpToLevelUp (Player p) {
		Integer neededExp = 0;
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Integer playerLevel = sp.getLevel();
		
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				neededExp = Integer.valueOf(loadLevels.split(" : ")[2]);
			}
		}
		
		return neededExp;
	}
	
	public Integer getNeededMoneyToLevelUp (Player p) {
		Integer neededExp = 0;
		
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		Integer playerLevel = sp.getLevel();
		
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				neededExp = Integer.valueOf(loadLevels.split(" : ")[3]);
			}
		}
		
		return neededExp;
	}
	
	public void subirNivel (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		Integer playerLevel = sp.getLevel();
		Double cost = 0.0D;
		Integer neededExp = 0;
		
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				neededExp = Integer.valueOf(loadLevels.split(" : ")[2]);
				cost = Double.valueOf(loadLevels.split(" : ")[3]);
			}
		}
		
		
		Integer calcNeededEXP = neededExp - sp.getExp();
		double calcMoney = cost - Vault.getMoney(p);
		
		// lastRank
		if (playerLevel == MSRP.get().getConfig().getStringList("config.levels").size()) {
			if (MSRP.lang.getBoolean("messages.levels.level-up.sendCenteredMessage")) {
				Tools.get().sendCenteredMessage(p, Text(MSRP.lang.getString("messages.levels.level-up.lastLevel")));
			} else {
				p.sendMessage(Text(MSRP.lang.getString("messages.levels.level-up.lastLevel")));
			}
			return;
		}
		
		//enough Level			
		if (sp.getExp() < neededExp) {
			for (String msg : MSRP.lang.getStringList("messages.levels.try-level-up.not-enough-exp")) {
				msg = msg.replace("<EXP>", ""+sp.getExp())
						.replace("<EXP_NEEDED>", ""+neededExp)
						.replace("<CALC_EXP>", ""+calcNeededEXP);
				if (MSRP.lang.getBoolean("messages.levels.try-level-up.sendCenteredMessage")) {
					Tools.get().sendCenteredMessage(p, Text(msg));
				} else {
					p.sendMessage(Text(msg));
				}
			}
			return;
		}
		
		if (Vault.getMoney(p) < cost) {
			for (String msg : MSRP.lang.getStringList("messages.levels.try-level-up.not-enough-money")) {
				msg = msg.replace("<BALANCE>", ""+Tools.get().formatMoney(Vault.getMoney(p)))
						.replace("<COST>", ""+Tools.get().formatMoney(cost))
						.replace("<CALC_MONEY>", ""+Tools.get().formatMoney(calcMoney));
				if (MSRP.lang.getBoolean("messages.levels.try-level-up.sendCenteredMessage")) {
					Tools.get().sendCenteredMessage(p, Text(msg));
				} else {
					p.sendMessage(Text(msg));
				}
			}
			return;
		}
		
		Vault.removeMoney(p, cost);
		sp.setLevel(sp.getLevel() + 1);
		sp.setExp(0);
		
		Integer newLevelPayday = 0;
		for (String loadLevels : MSRP.get().getConfig().getStringList("config.levels"))  {
			Integer getPlayerLevel = Integer.valueOf(loadLevels.split(" : ")[0]);
			if (playerLevel == getPlayerLevel) {
				newLevelPayday = Integer.valueOf(loadLevels.split(" : ")[1]);
			}
		}
		
		for (String msg : MSRP.lang.getStringList("messages.levels.level-up.message")) {
			msg = msg.replace("<LEVEL>", ""+sp.getLevel())
					.replace("<LEVEL_PAYDAY>", ""+newLevelPayday);
			if (MSRP.lang.getBoolean("messages.levels.level-up.sendCenteredMessage")) {
				Tools.get().sendCenteredMessage(p, Text(msg));
			} else {
				p.sendMessage(Text(msg));
			}
		}
		
		sp.updateBossbar();		
		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 10.0f, 1.0f);		
	}
	
}
