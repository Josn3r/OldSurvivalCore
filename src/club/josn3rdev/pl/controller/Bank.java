package club.josn3rdev.pl.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.Tools;

public class Bank {
	
	private Double ingresosDaily = 0.0;
	private Map<Player, Double> pagosDiarios = new HashMap<Player, Double>();
	
	
	private static Bank ins;
	
	public static Bank get() {
		if (ins == null) {
			ins = new Bank();
		}
		return ins;
	}
	
	
	public String Text (String textToTranslate) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}
		
	public String getBankName () {
		return MSRP.bank.getString("banks.bank-name");
	}
		
	public Double getBankBalance () {
		return MSRP.bank.getDouble("banks.bank-balance");
	}
	
	public void setBankBalance (Double balance) {
		MSRP.bank.set("banks.bank-balance", balance);
	}
		
	public Integer getBankAccounts() {
		Integer value = 0;
		return value;
	}
		
	//
	
	public void setBalance (Player p, Boolean type, Double value, Boolean payday) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		//
		if (type) {
    		Double comission = MSRP.bank.getDouble("banks.comissions.deposit");
            Double calcCommission = Tools.get().calcPercent(value, comission);            
            sp.setBankBalance(sp.getBankBalance() + (value - calcCommission));
    		if (!payday) {
    			sendTransactionToOwner(p, "DEPOSIT", value, calcCommission);
    		}
    		addIngresos(calcCommission);
    	} else {
    		Double comission = MSRP.bank.getDouble("banks.comissions.withdraw");
            Double calcCommission = Tools.get().calcPercent(value, comission);
    		sp.setBankBalance(sp.getBankBalance() - value);
    		if (!payday) {
    			sendTransactionToOwner(p, "WITHDRAW", value, calcCommission);
    		}
    		addIngresos(calcCommission);
    	}
	}
		
	//
	
	public void broadcastOwners (String message, String type) {
		for (Player p : Bukkit.getOnlinePlayers()) { 
			if (getBank(p)) {
				if (type.equals("NORMAL")) {
					p.sendMessage(Tools.get().Text(message));
				} else if (type.equals("CENTERED")) {
					Tools.get().sendCenteredMessage(p, message);
				} else {
					Tools.get().sendActionBar(p, message);
				}
			}
		}
	}
	
	public void sendTransactionToOwner(Player p, String type, Double balance, Double comission) {
		if (type.equalsIgnoreCase("DEPOSIT")) {
			broadcastOwners("&7[&6Banco&7] » &fEl jugador &e" + p.getName() + " &fhizo un &adepósito &fde &6&l$&e" + Tools.get().formatMoney(balance) + " &7(+$" + Tools.get().formatMoney(comission) + ")", "NORMAL");
		} else if (type.equalsIgnoreCase("BONO")) {
			broadcastOwners("&7[&6Banco&7] » &fEl jugador &e" + p.getName() + " &freclamó un &abono &fde &6&l$&e" + Tools.get().formatMoney(balance) + " &7(+$" + Tools.get().formatMoney(comission) + ")", "NORMAL");
		} else {
			broadcastOwners("&7[&6Banco&7] » &fEl jugador &e" + p.getName() + " &fhizo un &cretiro &fde &6&l$&e" + Tools.get().formatMoney(balance) + " &7(+$" + Tools.get().formatMoney(comission) + ")", "NORMAL");
		}
	}
	
	public void sendLevelUpToOwner(Player p, Integer oldLevel, Integer newLevel, Double balance) {
		broadcastOwners("&7[&6Banco&7] » &fEl jugador &e" + p.getName() + " &fsubió el &eNivel &fde su cuenta. &c[" + oldLevel + "] &f«-» &b[" + newLevel + "] &7- &fPagó &6&l$&e" + Tools.get().formatMoney(balance), "NORMAL");
	}
	
	public void createAccountTrasaction (Player p, String type, Double balance) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		
		ArrayList<String> trasactions = new ArrayList<String>();
		if (!sp.getTransactions().isEmpty()) {
			for (String str : sp.getTransactions()) {
				trasactions.add(str);
			}
		}	
		
		String timeStamp = (new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)")).format(Calendar.getInstance().getTime());
		String text = balance + " : " + type + " : " + timeStamp;
		trasactions.add(text);
		
		if (trasactions.size() > 10) {
			trasactions.remove(0);
		}
		
		sp.setTransactions(trasactions);
		
	}
	
	public ArrayList<String> getAccountTrasaction (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());		
		ArrayList<String> trasactions = new ArrayList<String>();
		Integer listSize = sp.getTransactions().size() - 1;
		
		if (!sp.getTransactions().isEmpty()) {
			for (int i = listSize; i >= 0; --i) {
				String str = sp.getTransactions().get(i);
				
				String money = str.split(" : ")[0];
				String type = str.split(" : ")[1];
				String time = str.split(" : ")[2];
				
				String text = "";
				if (type.equalsIgnoreCase("DEPOSIT")) {
					text = "&7[&a&l+&7] Depósito - &6$" + Tools.get().formatMoney(Double.valueOf(money)) + " &7- " + time;
				} else if (type.equalsIgnoreCase("BONO")) {
					text = "&7[&a&l+&7] Bono - &6$" + Tools.get().formatMoney(Double.valueOf(money)) + " &7- " + time;
				} else if (type.equalsIgnoreCase("INTERESES")) {
					text = "&7[&a&l+&7] Intereses - &6$" + Tools.get().formatMoney(Double.valueOf(money)) + " &7- " + time;
				} else if (type.equalsIgnoreCase("LEVELUP")) {
					text = "&7[&c&l-&7] Nivel - &6$" + Tools.get().formatMoney(Double.valueOf(money)) + " &7- " + time;
				} else {
					text = "&7[&c&l-&7] Retiro - &6" + Tools.get().formatMoney(Double.valueOf(money)) + " &7- " + time;
				}
				trasactions.add(text);
			}
		} else {
			trasactions.add("&7- Unknown");
		}		
		return trasactions;
	}
	
	//
	
	public Double checkGastosIntereses() {
    	Double value = 0.0;
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());    		
    		value += limitInterestPay(sp.getBankBalance()/1000, sp.getBankLevel());
    	}
    	return value;
    }
	
	public Double checkTotalMoney() {
    	Double value = 0.0;
    	value = MSRP.get().getDatabase().getTotalBankBalance();
    	return value;
    }
	
	public void bankPayday() {		
		Double bankIngresos = getIngresos();    	
		Double bankBalance = getBankBalance();	
		
		Double finalCalc = 0.0D;
		Double gastoIntereses = checkGastosIntereses();
		
		finalCalc = bankIngresos - gastoIntereses;
		
		Double calc25forOwners = Tools.get().calcPercent(finalCalc, 50.0);	
		Double accionesPagadas = 0.0;
		
		if (calc25forOwners > 0.0) {
			for (String owners : getBankPartnersList()) {
				String name = owners.split(" : ")[1];
				Double percent = Double.valueOf(owners.split(" : ")[3]);
				
				Double calculo = Tools.get().calcPercent(calc25forOwners, percent);
				Vault.setMoney(name, calculo);
				accionesPagadas += calculo;
				
				Player player = Bukkit.getPlayer(name);
				if (player != null) {
					player.sendMessage(Text("&e&lSpazioBank &7» &fEl banco tuvo &6&l$&e" + Tools.get().formatMoney(finalCalc) + " &fde Ganancia NETA."));
					player.sendMessage(Text("&e&lSpazioBank &7» &6+&6&l$&e" + Tools.get().formatMoney(calculo) + " para tí. (Acciones del banco - " + percent + "%)"));
					Tools.get().playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
			}
		} else {
			for (String owners : getBankPartnersList()) {
				String name = owners.split(" : ")[1];
				Player player = Bukkit.getPlayer(name);
				if (player != null) {
					player.sendMessage(Text("&c&lAVISO: &fTu banco está perdiendo dinero, no hubo ganancias!"));
				}
			}
		}
		
		Double pagosAcciones = calc25forOwners - accionesPagadas;
		finalCalc -= pagosAcciones;		
		Double newBankBalance = bankBalance + (finalCalc);
		setBankBalance(newBankBalance);
		setIngresos(0.0);
	}
	
	public Double getOwnerProfit (Double percent) {
		Double finalPercent = 0.0;
		
		Double bankIngresos = getIngresos();    	
		
		Double finalCalc = 0.0D;
		Double gastoIntereses = checkGastosIntereses();
				
		finalCalc = bankIngresos - gastoIntereses;
		
		Double calc25forOwners = Tools.get().calcPercent(finalCalc, 50.0);
		if (calc25forOwners > 0.0) {
			finalPercent = Tools.get().calcPercent(calc25forOwners, percent);
		}		
		return finalPercent;
	}
	
	//
	
	public void bankAccountLevelup (Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		Integer bankLevel = sp.getBankLevel().intValue();
		Integer levels = 12;
		
		Double nextLevelCost = getNextAccountLevelCost(p.getUniqueId().toString());
		Double pbankBalance = sp.getBankBalance();
		
		if (bankLevel >= levels) {
			p.sendMessage(Text("&7[&6Banco&7] » &cNivel bancario maximo alcanzado, eres todo un empresario."));
			Tools.get().playSound(p, Sound.BLOCK_ANVIL_FALL, 10.0f, 1.0f);
			return;
		}
		
		if ((bankLevel+1) > 10) {
			if (sp.getLevel() < 12) {
				p.sendMessage(Text("&7[&6Banco&7] » &cPara subir a un nivel de banco mayor a 10, debes llegar a Nivel 12 de cuenta!."));
				Tools.get().playSound(p, Sound.BLOCK_ANVIL_FALL, 10.0f, 1.0f);
				return;
			}
		}
				
		if ((pbankBalance) < nextLevelCost) {
			p.sendMessage(Text("&7[&6Banco&7] » &cBalance insuficiente para adquirir la mejora."));
			Tools.get().playSound(p, Sound.BLOCK_ANVIL_FALL, 10.0f, 1.0f);
			return;
		}
		
		sp.setBankBalance((pbankBalance-nextLevelCost));
		sp.setBankLevel((bankLevel+1));
		p.sendMessage(Text("&7[&6Banco&7] » Subiste de nivel tu cuenta bancaria, mejoras de intereses!"));
		
		//LEVELUP
		createAccountTrasaction(p, "LEVELUP", nextLevelCost);
		sendLevelUpToOwner(p, bankLevel, sp.getBankLevel(), nextLevelCost);
		addIngresos(nextLevelCost);
		
		Tools.get().playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
	}
	
	public Double getNextAccountLevelCost (String uuid) {
		SPlayer sp = PlayerManager.get().getPlayer(UUID.fromString(uuid));	
		Double inicial = 0.0;
        Integer nivel = sp.getBankLevel().intValue();
        if (nivel == 1) {
        	return 100000.0;//100,000
        } else if (nivel == 2) {
        	return 200000.0;//200,000
        } else if (nivel == 3) {
        	return 400000.0;//400,000
        } else if (nivel == 4) {
        	return 600000.0;//600,000
        } else if (nivel == 5) {
        	return 800000.0;//800,000
        } else if (nivel == 6) {
        	return 1000000.0;//1,000,000
        } else if (nivel == 7) {
        	return 3000000.0;//3,000,000
        } else if (nivel == 8) {
        	return 6000000.0;//6,000,000
        } else if (nivel == 9) {
        	return 10000000.0;//10,000,000
        } else if (nivel == 10) {
        	return 25000000.0;//25,000,000
        } else if (nivel == 11) {
        	return 50000000.0;//50,000,000
        } else if (nivel == 12) {
        	return 75000000.0;//75,000,000
        } else if (nivel == 13) {
        	return 100000000.0;//100,000,000
        } else if (nivel == 14) {
        	return 150000000.0;//150,000,000
        } else if (nivel == 15) {
        	return 0.0;
        }
		return inicial;
	}
		
	public Double limitInterestPay (Double interes, Integer nivel) {
		Double pago = interes;
		Double max = (50000.0 * nivel);		
		if (pago > max) {
			pago = max;
		}
		return pago;
	}
	
	public void playerBankInterests(Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());    	
		
		if (!MSRP.get().getDatabase().hasData(p, "bank_data")) {
			p.sendMessage(Text("&e&lSpazioBank &7» &cNo tienes una cuenta en el banco. Abre una en &e/warp banco&c."));
			return;
		}
		
		if (sp.getBankBalance() < 1000.0) {
			p.sendMessage(Text("&e&lSpazioBank &7» &cNo tienes suficiente dinero para recibir pagos de intereses..."));
			p.sendMessage(Text("&e&lSpazioBank &7» &cDebes tener mínimo &6&l$&e1.000 &cpara poder recibir intereses!"));
			return;
		}
		
		Double interes = limitInterestPay((sp.getBankBalance()/1000), sp.getBankLevel());
		pagosDiarios.put(p, interes);
		addIngresos(interes);
		
    	if (p != null) {
    		p.sendMessage(Text(" "));
    		Tools.get().sendCenteredMessage(p, Tools.get().Text("&7&l»&f&l» &6&lBANCO DE SPAZIO &f&l«&7&l«"));
    		Tools.get().sendCenteredMessage(p, Tools.get().Text("&fRecibiste un cheque de &a$" + Tools.get().formatMoney(interes) + " &fpor intereses."));
    		Tools.get().sendCenteredMessage(p, Tools.get().Text("&fTienes &e15 minutos &fpara reclamar el cheque y cobrar!"));
    		p.sendMessage(Text(" "));
    		Tools.get().playSound(p, Sound.BLOCK_BELL_USE, 1.0f, 1.0f);
    	}    	
	}
	
	public void givePlayerInterest(Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId()); 
		if (!pagosDiarios.containsKey(p)) {
			return;
		}		
		Double pago = pagosDiarios.get(p).doubleValue();
		
		createAccountTrasaction(p, "INTERESES", pago);
		sp.setBankBalance(sp.getBankBalance()+pago);
		p.sendMessage(Text("&e&lSpazioBank &7» &fReclamaste tu pago de &a$" + Tools.get().formatMoney(pago) + " &fen intereses del banco!"));
		
		pagosDiarios.remove(p);
	}	
	
	//
	
	public static void save(YamlConfiguration config, File file) {
        try { config.save(file); }
        catch (Exception e) {  e.printStackTrace(); }
    }
	
	//
		
	public void addPartner (Player player) {
		ArrayList<String> partners = new ArrayList<String>();
		
		for (String str : MSRP.bank.getStringList("banks.bank-owners")) {
			partners.add(str);
		}
		
		String uuid = player.getUniqueId().toString();
		String name = player.getName();
		String rank = "PARTNER";
		
		String format = uuid + " : " + name + " : " + rank + " : 0";
		partners.add(format);		
		MSRP.bank.set("banks.bank-owners", partners);
	}
	
	public void addPartner (String format) {
		ArrayList<String> partners = new ArrayList<String>();		
		for (String str : MSRP.bank.getStringList("banks.bank-owners")) {
			partners.add(str);
		}
		partners.add(format);		
		MSRP.bank.set("banks.bank-owners", partners);
	}
	
	public void removePartner (Player player) {
		ArrayList<String> partners = new ArrayList<String>();		
		for (String str : MSRP.bank.getStringList("banks.bank-owners")) {
			String uuid = str.split(" : ")[0];
			if (!player.getUniqueId().toString().equals(uuid)) {
				partners.add(str);
			}
		}		
		MSRP.bank.set("banks.bank-owners", partners);
	}
	
	public void removePartner (String uuid) {
		ArrayList<String> partners = new ArrayList<String>();		
		for (String str : MSRP.bank.getStringList("banks.bank-owners")) {
			String uid = str.split(" : ")[0];
			if (!uid.equals(uuid)) {
				partners.add(str);
			}
		}		
		MSRP.bank.set("banks.bank-owners", partners);
	}
	
	public Boolean isOwnerPartnerBank (Player player) { 
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String uuid = str2.split(" : ")[0];
			if (uuid.equals(player.getUniqueId().toString())) {
				return true;
			}
		}
		return false;
	}
	
	public Player getOwner () {
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String name = str2.split(" : ")[1];
			String rank = str2.split(" : ")[2];
			if (rank.equalsIgnoreCase("OWNER")) {
				Player player = Bukkit.getPlayer(name);
				if (player != null) {
					return player;
				}
			}
		}
		return null;
	}
	
	public Boolean getBank (Player player) {
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String uuid = str2.split(" : ")[0];
			if (uuid.equals(player.getUniqueId().toString())) {
				return true;
			}
		}
		return false;
	}
	
	public String getBankPartner (Player player) {
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String bankName = MSRP.bank.getString("banks.bank-name");
			String uuid = str2.split(" : ")[0];
			if (uuid.equals(player.getUniqueId().toString())) {
				return bankName;
			}
		}
		return "ERROR";
	}
	
	private String getBankRankYML (Player player) {
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String uuid = str2.split(" : ")[0];
			String rank = str2.split(" : ")[2];
			if (uuid.equals(player.getUniqueId().toString())) {
				return rank;
			}
		}
		return "ERROR";
	}
	
	public Boolean isBankOwner (Player player) {
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String uuid = str2.split(" : ")[0];
			String rank = str2.split(" : ")[2];
			if (uuid.equals(player.getUniqueId().toString())) {
				if (rank.equals("OWNER")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getBankRank (Player player) {
		String bankRank = getBankRankYML(player);
		if (bankRank.equals("OWNER")) {
			bankRank = MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER");
		}
		if (bankRank.equals("PARTNER")) {
			bankRank = MSRP.get().getConfig().getString("config.banks.bank-ranks.PARTNER");
		}
		return bankRank;
	}
	
	public String getBankRank(String rank) {
		String str = "";
		if (rank.equals("OWNER")) {
			str = MSRP.get().getConfig().getString("config.banks.bank-ranks.OWNER");
		}
		if (rank.equals("PARTNER")) {
			str = MSRP.get().getConfig().getString("config.banks.bank-ranks.PARTNER");
		}
		return str;
	}
	
	public ArrayList<String> getBankPartners() {
		ArrayList<String> partners = new ArrayList<String>();
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String name = str2.split(" : ")[1];
			String rank = str2.split(" : ")[2];
			String percent = str2.split(" : ")[3];
			
			if (rank.equals("OWNER")) {
				partners.add("&7- &a" + name + " &7(" + getBankRank(rank) + ") (" + percent + "%)");
			} else {
				partners.add("&7- &f" + name + " &7(" + getBankRank(rank) + ") (" + percent + "%)");
			}
		}		
		return partners;
	}
	
	public ArrayList<String> getBankPartner() {
		ArrayList<String> partners = new ArrayList<String>();		
		for (String str2 : MSRP.bank.getStringList("banks.bank-owners")) {
			String name = str2.split(" : ")[1];
			String rank = str2.split(" : ")[2];
			String percent = str2.split(" : ")[3];
			partners.add(name + " : " + rank + " : " + percent);
		}	
		return partners;
	}
	
	public ArrayList<String> getBankPartnersList() {
		ArrayList<String> partners = new ArrayList<String>();
		for (String str : MSRP.bank.getStringList("banks.bank-owners")) {
			partners.add(str);
		}
		return partners;
	}

	//
	
	public Double getIngresos() {
		return ingresosDaily;
	}

	public void setIngresos(Double ingresosDaily) {
		this.ingresosDaily = ingresosDaily;
	}
	
	public void addIngresos (Double ingresos) {
		this.ingresosDaily += ingresos;
	}
	
	public void takeIngresos (Double ingresos) {
		this.ingresosDaily -= ingresos;
	}
		
	public Map<Player, Double> getPagosDiarios() {
		return pagosDiarios;
	}
	
}
