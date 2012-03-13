package com;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StockMarketCommandExecutor implements CommandExecutor {

	private StockMarket plugin;
	 
	public StockMarketCommandExecutor(StockMarket plugin) {
		this.plugin = plugin;
	}
 
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		
		Message m = new Message(player);
		
		if (command.getName().equalsIgnoreCase("sm")) {
			if (args.length >= 1 && args[0].equalsIgnoreCase("help") && StockMarket.permission.has(player, "stockmarket.help")) {
				int page = 1;
				
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						
						m.errorMessage("Invalid Syntax. /sm help for help.");
						return true;
					}
				}
				
				m.displayHelp(page);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info") && StockMarket.permission.has(player, "stockmarket.info")) {
				m.displayInfo();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("mine") && player != null && StockMarket.permission.has(player, "stockmarket.list")) {
				// LIST ALL THE STOCKS THIS PLAYER OWNS
				PlayerStocks ps = new PlayerStocks(player);
				ps.listMine();
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("list") && StockMarket.permission.has(player, "stockmarket.list")) {
				// LIST ALL THE STOCKS THIS PLAYER CAN BUY
				PlayerStocks ps = new PlayerStocks(player);
				ps.listAll();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("buy") && player != null && StockMarket.permission.has(player, "stockmarket.buy")) {
				Stock stock = new Stock(args[1]);
				int amount = 1;
				
				if (args.length == 3) {
					try {
						amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						m.errorMessage("Invalid Syntax");
						return true;
					}
				}
				
				// REMOVE THIS STOCK FROM THE PLAYER, TAKE HIS MONEY
				PlayerStocks ps = new PlayerStocks(player);
				ps.buy(stock, amount);
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("sell") && player != null && StockMarket.permission.has(player, "stockmarket.sell")) {
				Stock stock = new Stock(args[1]);
				int amount = 1;
				
				if (args.length == 3) {
					try {
						amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						m.errorMessage("Invalid Syntax");
						return true;
					}
				}
				
				// REMOVE THIS STOCK FROM THE PLAYER, TAKE HIS MONEY
				PlayerStocks ps = new PlayerStocks(player);
				ps.sell(stock, amount);
			} else if (args.length >= 6 && args[0].equalsIgnoreCase("add") && (StockMarket.permission.has(player, "stockmarket.add") || player == null)){
				// ADD A ROW IN THE stocks TABLE, ADD A COLUMN IN THE players TABLE.
				String stockID = args[1];
				int baseprice;
				int minprice;
				int maxprice;
				int volatility;
				try {
					baseprice = Integer.parseInt(args[2]);
					maxprice = Integer.parseInt(args[3]);
					minprice = Integer.parseInt(args[4]);
					volatility = Integer.parseInt(args[5]);
				} catch (NumberFormatException e) {
					m.errorMessage("Invalid syntax.");
					return true;
				}
				
				String name = args[6];
				for (int i=7; i<args.length; i++) {
					name += " ";
					name += args[i];
				}
				
				Stock stock = new Stock(stockID);
						
				if (!stock.exists()) {
					if (stock.add(name, stockID, baseprice, maxprice, minprice, volatility))
						m.successMessage("Successfully created new stock.");
					else
						m.errorMessage("Failed to create new stock.  Make sure the ID was valid.");
				} else {
					m.errorMessage("A stock with that ID already exists!");
					return true;
				}
				
			} else if (args.length == 2 && args[0].equalsIgnoreCase("remove") && (StockMarket.permission.has(player, "stockmarket.remove") || player == null)) {
				String stockID = args[1];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					stock.remove();
					
					m.successMessage("Successfully removed that stock.");
				} else {
					m.errorMessage("That stock does not exist.");
					return true;
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) { 
				plugin.reloadConfig();
				plugin.loadConfiguration();
				m.successMessage("Successfully reloaded StockMarket.");
			} else if (args.length == 1 && StockMarket.permission.has(player, "stockmarket.detail")) {
				// CHECK IF THIS IS A STOCK NAME
				String stockID = args[0];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					m.successMessage(stock.toString());
					m.regularMessage("Current Price: " + stock.getPrice());
					
					// BASE SHOULD ONLY DISPLAY FOR A SPECIAL PERMISSION NODE
					if (StockMarket.permission.has(player, "stockmarket.baseprice"))
						m.regularMessage("Base Price: " + stock.getBasePrice());
					m.regularMessage("Max Price: " + stock.getMaxPrice());
					m.regularMessage("Min Price: " + stock.getMinPrice());
					m.regularMessage("Volatility: " + stock.getVolatility());
					
				} else {
					m.unknownCommand();
					return true;
				}
			} else if (args.length > 0){
				// UNKNOWN COMMAND
				m.unknownCommand();
			} else {
				m.displayInfo();
			}
		}
		
		
		return true;
	}
	
}
