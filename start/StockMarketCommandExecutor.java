package start;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		
		Message m = new Message(player);
		
		if (command.getName().equalsIgnoreCase("sm")) {
			if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
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
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				m.displayInfo();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("list") && args[1].equalsIgnoreCase("mine") && player != null) {
				// LIST ALL THE STOCKS THIS PLAYER OWNS
				PlayerStocks ps = new PlayerStocks(player);
				ps.listMine();
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
				// LIST ALL THE STOCKS THIS PLAYER CAN BUY
				PlayerStocks ps = new PlayerStocks(player);
				ps.listAll();
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("buy") && player != null) {
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
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("sell") && player != null) {
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
					m.errorMessage("Invalid syntax.");
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
						m.errorMessage("A stock with that ID already exists!");
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
				
				m.successMessage("Successfully created new stock.");
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
					
					m.successMessage("Successfully removed that stock.");
				} else {
					m.errorMessage("That stock does not exist.");
					return true;
				}
				
			} else if (args.length == 1) {
				// CHECK IF THIS IS A STOCK NAME
				String stockID = args[0];
				
				Stock stock = new Stock(stockID);
				
				if (stock.exists()) {
					m.successMessage(stock.toString());
					m.regularMessage("Current Price: " + stock.getPrice());
					
					// THIS SHOULD ONLY DISPLAY FOR ADMIN
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
