package club.josn3rdev.pl.menu.bank.personal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import club.josn3rdev.pl.MSRP;
import club.josn3rdev.pl.controller.Bank;
import club.josn3rdev.pl.hooks.Vault;
import club.josn3rdev.pl.menu.Menu;
import club.josn3rdev.pl.menu.bank.BankConfig;
import club.josn3rdev.pl.player.PlayerManager;
import club.josn3rdev.pl.player.SPlayer;
import club.josn3rdev.pl.utils.ItemBuilder;
import club.josn3rdev.pl.utils.Tools;
import net.wesjd.anvilgui.AnvilGUI;

public class BankMenu extends Menu {

	private ArrayList<String> balanceLore = new ArrayList<String>();
	
	public String Text (String s) {
		return Tools.get().Text(s);
	}
	
	public BankMenu (Player p) { 
		super(Bank.get().getBankName(), 4);
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
	
		loadDescriptions(p);
		
		String balance = MSRP.lang.getString("messages.menus.bank-menu.bank-player-gui.player-bank.balance.displayname");
		
		String depositBottom = MSRP.lang.getString("messages.menus.default-bottoms.deposit-displayname");
		String withdrawBottom = MSRP.lang.getString("messages.menus.default-bottoms.withdraw-displayname");
		
		String stado = "&cDesactivado";
		if (sp.getBedrock()) {
			stado = "&aActivado";
		}
		set(0, ItemBuilder.crearItem(Material.COMPARATOR, 1, "&6&lBEDROCK", 
				"&7Si eres un jugador de Bedrock", 
				"&7habilita esta opción para modificar", 
				"&7los ajustes de las GUI's", 
				"",
				"&7Estado: " + stado));
			
		
		set(12, ItemBuilder.crearItem(Material.PAPER, 1, balance, balanceLore));
		
		set(14, ItemBuilder.crearItem(Material.MAP, 1, "&eInversiones", 
				"&7¿Quieres probar tu suertes?",
				"&7Invierte en negocios y prueba!",
				" ",
				"&7• Inversiones:",
				"&cNinguna...",
				" ",
				"&e» Click para abrir el menú."));
		
		if (sp.getBankLevel() < 15) {
			set(22, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
					"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
					"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
					"",
					"&7Siguiente Nivel: &6Lv " + (sp.getBankLevel()+1),
					"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * (sp.getBankLevel()+1))),
					"&7Costo: &6$" + Tools.get().formatMoney(Bank.get().getNextAccountLevelCost(p.getUniqueId().toString())),
					"",
					"" + (sp.getBedrock() ? "&a» [Click] para COMPRAR el Siguiente nivel." : "&a» [Shift + Click] para COMPRAR el Siguiente nivel.")));
		} else {
			set(22, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
					"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
					"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
					"",
					"&cTe encuentras en el máximo nivel."));
		}
		
		set(20, ItemBuilder.crearItem(Material.LIME_DYE, 1, depositBottom));
		set(24, ItemBuilder.crearItem(Material.RED_DYE, 1, withdrawBottom));
			
		set(35, ItemBuilder.crearItem(Material.CRAFTING_TABLE, 1, "&6Configuración", "&7¿Eres socio del banco?", "&7Click aquí para acceder..."));
		
		Tools.get().playSound(p, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
		Bank.get().givePlayerInterest(p);
	}

	@Override
	public void onClick(Player p, ItemStack stack) {
		
	}

	@Override
	public void onClick2(Player p, InventoryClickEvent e) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		String name = ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName());
		
		String depositBottom = MSRP.lang.getString("messages.menus.default-bottoms.deposit-displayname");
		String withdrawBottom = MSRP.lang.getString("messages.menus.default-bottoms.withdraw-displayname");
	
		String bankName = MSRP.bank.getString("banks.bank-name");
		
		if (name.equalsIgnoreCase(Text("&6Configuración"))) {
			if (!Bank.get().isOwnerPartnerBank(p)) {
				p.sendMessage(Text(MSRP.lang.getString("messages.banks.bank-owner.bank-config-error")));
				return;
			}			
			new BankConfig(p).open(p);
			return;
		}
				
		if (!sp.getBedrock()) {
			if (e.getClick() == ClickType.SHIFT_LEFT) {
				if (name.equalsIgnoreCase(Text("&eNivel"))) {
					Bank.get().bankAccountLevelup(p);
					
					if (sp.getBankLevel() < 15) {
						set(24, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
								"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
								"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
								"",
								"&7Siguiente Nivel: &6Lv " + (sp.getBankLevel()+1),
								"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * (sp.getBankLevel()+1))),
								"&7Costo: &6$" + Tools.get().formatMoney(Bank.get().getNextAccountLevelCost(p.getUniqueId().toString())),
								"",
								"" + (sp.getBedrock() ? "&a» [Click] para COMPRAR el Siguiente nivel." : "&a» [Shift + Click] para COMPRAR el Siguiente nivel.")));
					} else {
						set(24, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
								"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
								"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
								"",
								"&cTe encuentras en el máximo nivel."));
					}
					
					return;
				}
			}
		} else {
			if (name.equalsIgnoreCase(Text("&eNivel"))) {
				Bank.get().bankAccountLevelup(p);
				
				if (sp.getBankLevel() < 15) {
					set(24, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
							"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
							"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
							"",
							"&7Siguiente Nivel: &6Lv " + (sp.getBankLevel()+1),
							"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * (sp.getBankLevel()+1))),
							"&7Costo: &6$" + Tools.get().formatMoney(Bank.get().getNextAccountLevelCost(p.getUniqueId().toString())),
							"",
							"" + (sp.getBedrock() ? "&a» [Click] para COMPRAR el Siguiente nivel." : "&a» [Shift + Click] para COMPRAR el Siguiente nivel.")));
				} else {
					set(24, ItemBuilder.crearItem(Material.SUNFLOWER, 1, "&eNivel", 
							"&7Nivel Bancario: &6Lv " + sp.getBankLevel(),
							"&7Máximo Intereses: &6$" + Tools.get().formatMoney((50000.0D * sp.getBankLevel())),
							"",
							"&cTe encuentras en el máximo nivel."));
				}
				
				return;
			}
		}
				
		if (name.equalsIgnoreCase(Text("&6&lBEDROCK"))) {
			if (sp.getBedrock()) {
				sp.setBedrock(false);
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
				
				String stado = "&cDesactivado";
				if (sp.getBedrock()) {
					stado = "&aActivado";
				}
				set(0, ItemBuilder.crearItem(Material.COMPARATOR, 1, "&6&lBEDROCK", 
						"&7Si eres un jugador de Bedrock", 
						"&7habilita esta opción para modificar", 
						"&7los ajustes de las GUI's", 
						"",
						"&7Estado: " + stado));
			} else {
				sp.setBedrock(true);
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
				
				String stado = "&cDesactivado";
				if (sp.getBedrock()) {
					stado = "&aActivado";
				}
				set(0, ItemBuilder.crearItem(Material.COMPARATOR, 1, "&6&lBEDROCK", 
						"&7Si eres un jugador de Bedrock", 
						"&7habilita esta opción para modificar", 
						"&7los ajustes de las GUI's", 
						"",
						"&7Estado: " + stado));
			}
		}
		
		if (name.equalsIgnoreCase(Text(depositBottom))) {
			if (sp.getBedrock()) {
				p.closeInventory();
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
				Tools.get().clearChat(p, 20);

				sp.setTransactionType("DEPOSIT");
				sp.startChatTransaction();
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fEspecifica la cantidad que deseas depósitar en tu cuenta..."));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fEl formato es: &e$1545.23"));
				p.sendMessage(Tools.get().Text(" "));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fTienes &e30 segundos &fpara realizar el depósito..."));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fPara cancelar la transacción, escribe &cCancelar&f."));
				return;
			}
			anvil(p, "DEPOSIT");
		}
		
		if (name.equalsIgnoreCase(Text(withdrawBottom))) {
			if (sp.getBedrock()) {
				p.closeInventory();
				Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
				Tools.get().clearChat(p, 20);
				
				sp.setTransactionType("WITHDRAW");
				sp.startChatTransaction();
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &cEspecifica la cantidad que deseas retirar de tu cuenta..."));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &cEl formato es: &e$1545.23"));
				p.sendMessage(Tools.get().Text(" "));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fTienes &e30 segundos &fpara realizar el retiro..."));
				p.sendMessage(Tools.get().Text("&e&l" + bankName + ": &fPara cancelar la transacción, escribe &cCancelar&f."));
				return;
			}
			anvil(p, "WITHDRAW");
		}
		
	}
	
	@Override
	public void onClose(Player p) {
		Tools.get().playSound(p, Sound.BLOCK_CHEST_CLOSE, 1.0f, 10.0f);
	}
	
	private void loadDescriptions(Player p) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
				
		for (String str : MSRP.lang.getStringList("messages.menus.bank-menu.bank-player-gui.player-bank.balance.description")) {
			MSRP.get();
			str = str.replace("<BALANCE>", Tools.get().formatMoney(sp.getBankBalance()));
			if (str.equalsIgnoreCase("<TRANSACTIONS>")) {
				MSRP.get();
				for (String transfers : Bank.get().getAccountTrasaction(p)) {
					balanceLore.add(Text(transfers));
				}
			} else {
				balanceLore.add(Text(str));
			}
		}
				
	}

	public void anvil(Player p, String type) {
		SPlayer sp = PlayerManager.get().getPlayer(p.getUniqueId());
		
		new AnvilGUI.Builder()
		.onClose(pl -> {
			MSRP.get().getServer().getScheduler().runTaskLater(MSRP.get(), new Runnable() {
				@Override
				public void run() {
					new BankMenu(p).open(p);
				}
			}, 5L);
		})
		.onComplete((pl, text) -> {
			
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			String tcode = text;
			Pattern pattern = Pattern.compile("[0-9].*");
			Matcher match = pattern.matcher(tcode);
						
			if (!match.matches()) {
				return AnvilGUI.Response.text("Solo valor monetario");
			}
			
			Double value = Double.valueOf(text);
			
			if (type.equalsIgnoreCase("deposit")) {
				
				Double comission = MSRP.bank.getDouble("banks.comissions.deposit");
	            Double calcCommission = Tools.get().calcPercent(value, comission);
	            
				if (Vault.getMoney(p) < value) {
					return AnvilGUI.Response.text("Balance insuficiente");
				}
				
				if (value > 10000000000000.0) {
					return AnvilGUI.Response.text("Max 10$Trillon");
				}	
				
				Vault.removeMoney(p, value);
				Bank.get().setBalance(p, true, value, false);
				Bank.get().createAccountTrasaction(p, "DEPOSIT", (value - calcCommission));
			
				for (String str : MSRP.lang.getStringList("messages.banks.bank-personal.deposit.success-transaction")) {
					str = str.replace("<DEPOSIT>", Tools.get().formatMoney(Double.valueOf(text)))
							.replace("<DATE>", (new SimpleDateFormat("dd/MM/yyyy")).format(Calendar.getInstance().getTime()))
							.replace("<COMMISSION>", Tools.get().formatMoney(calcCommission));
					p.sendMessage(Text(str));
				}
			} else {
				
				Double comission = MSRP.bank.getDouble("banks.comissions.withdraw");
	            Double calcCommission = Tools.get().calcPercent(Double.valueOf(text), comission);
	            
				if (sp.getBankBalance() < value) {
					return AnvilGUI.Response.text("Balance insuficiente");
				}
				
				if (value > 10000000000000.0) {
					return AnvilGUI.Response.text("Max 10$Trillon");
				}	
				
				Vault.setMoney(p, (value-calcCommission));
				Bank.get().setBalance(p, false, Double.valueOf(text), false);
				Bank.get().createAccountTrasaction(p, "WITHDRAW", Double.valueOf(text));
			
				for (String str : MSRP.lang.getStringList("messages.banks.bank-personal.withdraw.success-transaction")) {
					str = str.replace("<WITHDRAW>", Tools.get().formatMoney(Double.valueOf(text)))
							.replace("<DATE>", (new SimpleDateFormat("dd/MM/yyyy")).format(Calendar.getInstance().getTime()))
							.replace("<COMMISSION>", Tools.get().formatMoney(calcCommission));
					p.sendMessage(Text(str));
				}
			}
			
			Tools.get().playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
			
			new BankMenu(p).open(p);
			return AnvilGUI.Response.close();
		})
		.text(" ")
		.itemLeft(new ItemStack(Material.SUNFLOWER))
		.title(Text("&e" + type + " Balance"))
		.plugin(MSRP.get())
		.open(p);
	}
}
