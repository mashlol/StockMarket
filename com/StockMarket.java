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
	
	public static Vector<Event> events = new Vector<Event>();
	
	public static Permission permission = null;
	public static Economy economy = null;
	
	public static String mysqlIP = "localhost";
	public static String mysqlPort = "3306";
	public static String mysqlDB = "sm";
	public static String mysqlUser = "root";
	public static String mysqlPW = "";
	
	public static int dividendFreq = 1440;
	public static int randomEventFreq = 60;
	public static int maxPerPlayer = 250;
	public static int maxPerPlayerPerStock = 50;
	
	public static boolean payOffline = true;
	
	private Logger log = Logger.getLogger("Minecraft");
	private StockMarketEventThread e;
	private StockMarketDividendThread d;
	
	public void onDisable() {
		try {
			e.finish();
			d.finish();
		} catch (NullPointerException e) {
			System.out.println("[StockMarket] A StockMarket thread never started!");
		}
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
		
		
		initCommands();
		
		loadConfiguration();
		
		e = new StockMarketEventThread();
		e.start();
		
		d = new StockMarketDividendThread();
		d.start();
	}
	
	public void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		mysqlIP = getConfig().getString("mysql.ip");
		mysqlPort = getConfig().getString("mysql.port");
		mysqlDB = getConfig().getString("mysql.database");
		mysqlUser = getConfig().getString("mysql.username");
		mysqlPW = getConfig().getString("mysql.password");
		
		dividendFreq = getConfig().getInt("dividend-frequency");
		randomEventFreq = getConfig().getInt("random-event-frequency");
		maxPerPlayer = getConfig().getInt("max-total-stocks-per-player");
		maxPerPlayerPerStock = getConfig().getInt("max-total-stocks-per-player-per-stock");
		
		payOffline = getConfig().getBoolean("pay-offline-players");
		
		// LOAD EVENTS
		events.clear();
		int i = 0;
		while(getConfig().getString("events." + i + ".message") != null) {
			events.add(new Event(getConfig().getString("events." + i + ".message"), getConfig().getInt("events." + i + ".effect"), getConfig().getBoolean("events." + i + ".up"), getConfig().getInt("events." + i + ".frequency")));
			i++;
		}
	}
	
	private void initCommands() {
		commands.add(new Command("help", "Displays StockMarket help.", "<page>",  "stockMarket.user.help"));
		commands.add(new Command("info", "Displays plugin version & status.", "",  "stockMarket.user.info"));
		commands.add(new Command("list", "Displays a list of stocks you are allowed to buy and their current price.", "",  "stockMarket.user.list"));
		commands.add(new Command("list mine", "Displays a list of stocks that you currently own and their current price.", "",  "stockMarket.user.list"));
		commands.add(new Command("buy", "Buys the stock & amount specified.", "<stockID> <amount>",  "stockMarket.user.buy"));
		commands.add(new Command("sell", "Sells the stock & amount specified.", "<stockID> <amount>",  "stockMarket.user.sell"));
		commands.add(new Command("add", "Adds a new stock to the list of all stocks.", "<stockID> <basePrice> <maxPrice> <minPrice> <volatility> <amount> <dividend> <stockName>",  "stockMarket.admin.add"));
		commands.add(new Command("remove", "Removes an existing stock from the list of all stocks.  Cannot be undone.", "<stockID>",  "stockMarket.admin.remove"));
		commands.add(new Command("set", "Sets all the values of the given stock to the new specified values. Does not affect the current price.", "<stockID> <newBasePrice> <newMaxPrice> <newMinPrice> <newVolatility> <newAmount> <newDividend> <newStockName>",  "stockMarket.admin.set"));
		commands.add(new Command("reload", "Reloads the StockMarket config.", "",  "stockMarket.admin.reload"));
		commands.add(new Command("forcerandom", "Forces a random event to occur on a random stock.", "",  "stockMarket.admin.event"));
		commands.add(new Command("", "Displays more info about stock requested.", "<stockID>",  "stockMarket.user.detail"));
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