package com;
 
import java.util.Vector;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
 
public class StockMarket extends JavaPlugin {
 
	private StockMarketCommandExecutor myExecutor;
	public static Vector<Command> commands = new Vector<Command>();
	
	public static Permission permission = null;
	public static Economy economy = null;
	
	Logger log = Logger.getLogger("Minecraft");
	StockMarketEventThread s;
	
	public void onDisable() {
		s.finish();
	}

	public void onEnable() {
		if (setupEconomy()) {
			log.info("[StockMarket] Economy plugin detected and hooked into.");
		} else {
			log.info("[StockMarket] Economy plugin not detected! Disabling StockMarket!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (setupPermissions()) {
			log.info("[StockMarket] Permissions plugin detected and hooked into.");
		} else {
			log.info("[StockMarket] Permissions plugin not detected! Disabling StockMarket!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		myExecutor = new StockMarketCommandExecutor(this);
		getCommand("sm").setExecutor(myExecutor);
		
		// FETCH FROM MYSQL THE LOOPTIME v
		int loopTime = 0;
		
		s = new StockMarketEventThread(loopTime);
		s.start();
		
		initCommands();
	}
	
	private void initCommands() {
		Command c;
		
		c = new Command("help", "Displays StockMarket help.", "<page>",  "stockMarket.help");
		commands.add(c);
		c = new Command("info", "Displays plugin version & status.", "",  "stockMarket.info");
		commands.add(c);
		c = new Command("list", "Displays a list of stocks you are allowed to buy and their current price.", "",  "stockMarket.list");
		commands.add(c);
		c = new Command("list mine", "Displays a list of stocks that you currently own and their current price.", "",  "stockMarket.list");
		commands.add(c);
		c = new Command("buy", "Buys the stock & amount specified.", "<stockID> <amount>",  "stockMarket.buy");
		commands.add(c);
		c = new Command("sell", "Sells the stock & amount specified.", "<stockID> <amount>",  "stockMarket.sell");
		commands.add(c);
		c = new Command("", "displays more info about stock requested.", "<stockID>",  "stockMarket.detail");
		commands.add(c);
	}
	
	private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	 private Boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}