package com.github.mashlol;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.mashlol.Events.EventInstance;
import com.github.mashlol.Messages.Message;
import com.github.mashlol.Stocks.PlayerStocks;
import com.github.mashlol.Stocks.Stock;
import com.github.mashlol.Stocks.Stocks;

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
			if (args.length >= 1 && args[0].equalsIgnoreCase("help") && (player == null || StockMarket.permission.has(player, "stockmarket.user.help"))) {
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
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info") && (player == null || StockMarket.permission.has(player, "stockmarket.user.info"))) {
				m.displayInfo();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("mine") && player != null && StockMarket.permission.has(player, "stockmarket.user.list")) {
				// LIST ALL THE STOCKS THIS PLAYER OWNS
				PlayerStocks ps = new PlayerStocks(player);
				ps.listMine();
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("list") && (player == null || StockMarket.permission.has(player, "stockmarket.user.list"))) {
				// LIST ALL THE STOCKS THIS PLAYER CAN BUY
				PlayerStocks ps = new PlayerStocks(player);
				ps.listAll();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("buy") && player != null && StockMarket.permission.has(player, "stockmarket.user.buy")) {
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
				
				if (amount > 0) {
					PlayerStocks ps = new PlayerStocks(player);
					ps.buy(stock, amount);
				} else {
					m.errorMessage("Invalid amount.");
				}
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("sell") && player != null && StockMarket.permission.has(player, "stockmarket.user.sell")) {
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
				
				if (amount > 0) {
					PlayerStocks ps = new PlayerStocks(player);
					ps.sell(stock, amount);
				} else {
					m.errorMessage("Invalid amount.");
				}
			} else if (args.length >= 9 && args[0].equalsIgnoreCase("add") && (player == null || StockMarket.permission.has(player, "stockmarket.admin.add"))) {
				final String stockID = args[1];
				final double baseprice;
				final double minprice;
				final double maxprice;
				final double volatility;
				final int amount;
				final double dividend;
				try {
					baseprice = Double.parseDouble(args[2]);
					maxprice = Double.parseDouble(args[3]);
					minprice = Double.parseDouble(args[4]);
					volatility = Double.parseDouble(args[5]);
					amount = Integer.parseInt(args[6]);
					dividend = Double.parseDouble(args[7]);
				} catch (NumberFormatException e) {
					m.errorMessage("Invalid syntax.");
					return true;
				}
				
				if (amount < -1) {
					m.errorMessage("Invalid amount.");
					return true;
				}
				
				String name = args[8];
				for (int i=9; i<args.length; i++) {
					name += " ";
					name += args[i];
				}
				
				Stock stock = new Stock(stockID);
						
				if (!stock.exists()) {
					if (stock.add(name, stockID, baseprice, maxprice, minprice, volatility, amount, dividend))
						m.successMessage("Successfully created new stock.");
					else
						m.errorMessage("Failed to create new stock.  Make sure the ID was valid.");
				} else {
					m.errorMessage("A stock with that ID already exists!");
					return true;
				}
				
			} else if (args.length == 2 && args[0].equalsIgnoreCase("remove") && (player == null || StockMarket.permission.has(player, "stockmarket.admin.remove"))) {
				String stockID = args[1];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					stock.remove();
					
					m.successMessage("Successfully removed that stock.");
				} else {
					m.errorMessage("That stock does not exist.");
					return true;
				}
			} else if (args.length >= 8 && args[0].equalsIgnoreCase("set") && (player == null || StockMarket.permission.has(player, "stockmarket.admin.set"))){
				final String stockID = args[1];
				final double baseprice;
				final double minprice;
				final double maxprice;
				final double volatility;
				final int amount;
				final double dividend;
				try {
					baseprice = Double.parseDouble(args[2]);
					maxprice = Double.parseDouble(args[3]);
					minprice = Double.parseDouble(args[4]);
					volatility = Double.parseDouble(args[5]);
					amount = Integer.parseInt(args[6]);
					dividend = Double.parseDouble(args[7]);
				} catch (NumberFormatException e) {
					m.errorMessage("Invalid syntax.");
					return true;
				}
				
				if (amount < -1) {
					m.errorMessage("Invalid amount.");
					return true;
				}
				
				String name = args[8];
				for (int i=9; i<args.length; i++) {
					name += " ";
					name += args[i];
				}
				
				Stock stock = new Stock(stockID);
						
				if (stock.set(name, stockID, baseprice, maxprice, minprice, volatility, amount, dividend))
					m.successMessage("Successfully adjusted stock.");
				else
					m.errorMessage("Failed to adjust stock.  Make sure the ID was valid.");
				
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && (player == null || StockMarket.permission.has(player, "stockmarket.admin.reload"))) { 
				plugin.reloadConfig();
				plugin.loadConfiguration();
				m.successMessage("Successfully reloaded StockMarket.");
			}  else if (args.length == 1 && args[0].equalsIgnoreCase("forcerandom") && (player == null || StockMarket.permission.has(player, "stockmarket.admin.event"))) {
				Stocks s = new Stocks();
				if (s.numStocks() > 0) {
					EventInstance ei = new EventInstance();
					ei.forceRandomEvent(s.getRandomStock());
				}
			} else if (args.length == 1 && (player == null || StockMarket.permission.has(player, "stockmarket.user.detail"))) {
				// CHECK IF THIS IS A STOCK NAME
				String stockID = args[0];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					m.successMessage(stock.toString());
					m.regularMessage("Current Price: " + stock.getPrice());
					
					// BASE SHOULD ONLY DISPLAY FOR A SPECIAL PERMISSION NODE
					if (player == null || StockMarket.permission.has(player, "stockmarket.admin.baseprice"))
						m.regularMessage("Base Price: " + stock.getBasePrice());
					m.regularMessage("Max Price: " + stock.getMaxPrice());
					m.regularMessage("Min Price: " + stock.getMinPrice());
					m.regularMessage("Volatility: " + stock.getVolatility());
					m.regularMessage("Dividend: " + stock.getDividend() + "% per stock.");
					if (stock.getAmount() != -1) 
						m.regularMessage("Current Amount: " + stock.getAmount());
					else
						m.regularMessage("Current Amount: Infinite");
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
