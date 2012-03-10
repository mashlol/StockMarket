package start;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StockMarketCommandExecutor implements CommandExecutor {

	//private StockMarket plugin;
	 
	public StockMarketCommandExecutor(StockMarket plugin) {
		//this.plugin = plugin;
	}
 
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		
		if (command.getName().equalsIgnoreCase("sm")) {
			if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
				int page = 1;
				
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						errorMessage(player, "Inavlid Syntax. /sm help for help.");
						return true;
					}
				}
				
				displayHelp(player, page);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				displayInfo(player);
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("mine") && player != null) {
				// LIST ALL THE STOCKS THIS PLAYER OWNS
				PlayerStocks ps = new PlayerStocks(player);
				
				successMessage(player, "List of stocks you own:");
				
				boolean found = false;
				
				for (int i=0; i<ps.list().size(); i++) {
					PlayerStock playerstock = ps.list().get(i);
					if (playerstock.amount > 0) {
						regularMessage(player, playerstock.stock.getID() + " - Amount: " + playerstock.amount + " - Price: " + playerstock.stock.getPrice());
						found = true;
					}
				}
				
				if (!found) {
					errorMessage(player, "You don't own any stocks.");
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
				// LIST ALL THE STOCKS THIS PLAYER CAN BUY
				PlayerStocks ps = new PlayerStocks(player);
				
				successMessage(player, "List of stocks:");
				
				for (int i=0; i<ps.list().size(); i++) {
					PlayerStock playerstock = ps.list().get(i);
					regularMessage(player, playerstock.stock.getID() + " - Price: " + playerstock.stock.getPrice());
				}
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("buy") && player != null) {
				Stock stock = new Stock(args[1]);
				int amount = 1;
				
				if (args.length == 3) {
					amount = Integer.parseInt(args[2]);
				}
				
				// ADD THIS STOCK TO THE PLAYER, TAKE HIS MONEY
				buyStock(player, stock, amount);
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("sell") && player != null) {
				Stock stock = new Stock(args[1]);
				int amount = 1;
				
				if (args.length == 3) {
					amount = Integer.parseInt(args[2]);
				}
				
				// REMOVE THIS STOCK FROM THE PLAYER, TAKE HIS MONEY
				sellStock(player, stock, amount);
			} else if (args.length >= 6 && args[0].equalsIgnoreCase("add")){
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
					errorMessage(player, "Invalid syntax.");
					return true;
				}
				
				String name = args[6];
				for (int i=7; i<args.length; i++) {
					name += " ";
					name += args[i];
				}
				
				MySQL mysql = new MySQL();
				// LETS MAKE SURE IT DOESNT ALREADY EXIST!
				PreparedStatement stmt = mysql.prepareStatement("SELECT stockID FROM stocks WHERE stockID LIKE ?");
				try {
					stmt.setString(1, stockID);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				ResultSet result = mysql.query(stmt);
				
				try {
					while (result.next()) {
						// THIS EXISTS!
						errorMessage(player, "A stock with that ID already exists!");
						return true;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		
				
				//PreparedStatement stmt = mysql.prepareStatement("IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'players' AND COLUMN_NAME = '" + stockID + "') BEGIN ALTER TABLE players ADD " + stockID + " TINTYTEXT DEFAULT '0'");
				mysql.execute("ALTER TABLE players ADD COLUMN " + stockID + " INT DEFAULT 0");
				
				stmt = mysql.prepareStatement("INSERT INTO stocks (name, stockID, price, basePrice, maxPrice, minPrice, volatility) VALUES (?, ?, ?, ?, ?, ?, ?)");
				try {
					stmt.setString(1, name);
					stmt.setString(2, stockID);
					stmt.setInt(3, baseprice);
					stmt.setInt(4, baseprice);
					stmt.setInt(5, maxprice);
					stmt.setInt(6, minprice);
					stmt.setInt(7, volatility);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				mysql.execute(stmt);
				
				mysql.close();
				
				successMessage(player, "Successfully created new stock.");
			} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
				String stockID = args[1];
				
				Stock stock = new Stock(stockID);
				
				
				if (stock.exists()) {
				// REMOVE COLUMN FROM players, REMOVE ROW FROM stocks
				// LETS FIRST LOOK AT STOCKS AND RETURN IF NOT FOUND
					MySQL mysql = new MySQL();
					
					mysql.execute("ALTER TABLE players DROP COLUMN " + stockID);
					
					PreparedStatement stmt = mysql.prepareStatement("DELETE FROM stocks WHERE StockID LIKE ?");
					try {
						stmt.setString(1, stockID);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					mysql.execute(stmt);
					
					mysql.close();
					
					successMessage(player, "Successfully removed that stock.");
				} else {
					errorMessage(player, "That stock does not exist.");
					return true;
				}
				
			} else if (args.length == 1) {
				// CHECK IF THIS IS A STOCK NAME
				String stockID = args[0];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					successMessage(player, stock.toString());
					regularMessage(player, "Current Price: " + stock.getPrice());
					
					// THIS SHOULD ONLY DISPLAY FOR ADMIN
					regularMessage(player, "Base Price: " + stock.getBasePrice());
					regularMessage(player, "Max Price: " + stock.getMaxPrice());
					regularMessage(player, "Min Price: " + stock.getMinPrice());
					regularMessage(player, "Volatility: " + stock.getVolatility());
					
				} else {
					unknownCommand(player);
					return true;
				}
				
			} else if (args.length > 0){
				// UNKNOWN COMMAND
				unknownCommand(player);
			} else {
				displayInfo(player);
			}
		}
		
		
		return true;
	}
	
	private void displayHelp (Player player, int page) {
		if (page == 1) {
			successMessage(player, "Help [Page 1 of 2]:");
			regularMessage(player, "/sm help [page] - Displays a list of available commands and their info.");
			regularMessage(player, "/sm info - Displays plugin version & status.");
			regularMessage(player, "/sm list - Displays a list of stocks you are allowed to buy and their current price.");
			regularMessage(player, "/sm list mine - Displays a list of stocks that you currently own and their current price.");
			regularMessage(player, "/sm buy <stock ID> <amount> - Buys the stock specified.");
		} else if (page == 2) {
			successMessage(player, "Help [Page 2 of 2]:");
			regularMessage(player, "/sm sell <stock ID> <amount> - Sells the stock specified.");
			regularMessage(player, "/sm <stockID> - displays more details about stock requested.");
		}
	}
	
	private void displayInfo (Player player) {
		successMessage(player, "Current version: v.01 developed by Mash.");
	}
	
	private void unknownCommand (Player player) {
		errorMessage(player, "Unknown command.  Use /sm help for commands.");
	}
	
	private void errorMessage(Player player, String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketError" + ChatColor.WHITE + "]" + ChatColor.RED + " " + message);
		else
			System.out.println("[StockMarketError] " + message);
	}
	
	private void regularMessage(Player player, String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarket" + ChatColor.WHITE + "]" + ChatColor.BLUE + " " + message);
		else
			System.out.println("[StockMarket] " + message);
	}
	
	private void successMessage(Player player, String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarket" + ChatColor.WHITE + "]" + ChatColor.GREEN + " " + message);
		else
			System.out.println("[StockMarket] " + message);
	}
	
	private int sellStock (Player player, Stock stock, int amount) {
		
		if (stock.exists()) {
			PlayerStocks ps = new PlayerStocks(player);
			
			if (ps.sell(stock, amount))
				successMessage(player, "Successfully sold " + amount + " " + stock + " stocks which are currently at " + stock.getPrice() + " each.");
			else
				errorMessage(player, "Failed to sell!  Check that you have that many!");
			
			return 0;
		} else {
			errorMessage(player, "Invalid stock ID");
		}
		
		return -1;
	}
	
	private int buyStock (Player player, Stock stock, int amount) {
		
		if (stock.exists()) {
			
			PlayerStocks ps = new PlayerStocks(player);
			
			if (ps.buy(stock, amount))
				successMessage(player, "Successfully bought " + amount + " " + stock + " stocks which are currently at " + stock.getPrice() + " each.");
			else
				errorMessage(player, "Failed to buy!  Check that you have enough money!");
			
			return 0;
		} else {
			errorMessage(player, "Invalid stock ID");
		}
		
		return -1;
	}
	
}
