package com;
 
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
 
public class StockMarket extends JavaPlugin {
 
	private StockMarketCommandExecutor myExecutor;
	
	static public Vector<Command> commands = new Vector<Command>();
	
	Logger log = Logger.getLogger("Minecraft");
	StockMarketEventThread s;
	
	public void onDisable() {
		s.finish();
	}

	public void onEnable() {
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
	
	
}