package start;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

	private Player player;
	
	public Message (Player player) {
		this.player = player;
	}
	
	public void displayInfo () {
		successMessage("Current version: v.01 developed by Mash.");
	}
	
	public void unknownCommand () {
		errorMessage("Unknown command.  Use /sm help for help.");
	}
	
	public void errorMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketError" + ChatColor.WHITE + "]" + ChatColor.RED + " " + message);
		else
			System.out.println("[StockMarketError] " + message);
	}
	
	public void regularMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarket" + ChatColor.WHITE + "]" + ChatColor.BLUE + " " + message);
		else
			System.out.println("[StockMarket] " + message);
	}
	
	public void successMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarket" + ChatColor.WHITE + "]" + ChatColor.GREEN + " " + message);
		else
			System.out.println("[StockMarket] " + message);
	}
	
	public void displayHelp (int page) {
		if (page == 1) {
			successMessage("Help [Page 1 of 2]:");
			regularMessage("/sm help [page] - Displays a list of available commands and their info.");
			regularMessage("/sm info - Displays plugin version & status.");
			regularMessage("/sm list - Displays a list of stocks you are allowed to buy and their current price.");
			regularMessage("/sm list mine - Displays a list of stocks that you currently own and their current price.");
			regularMessage("/sm buy <stock ID> <amount> - Buys the stock specified.");
		} else if (page == 2) {
			successMessage("Help [Page 2 of 2]:");
			regularMessage("/sm sell <stock ID> <amount> - Sells the stock specified.");
			regularMessage("/sm <stockID> - displays more details about stock requested.");
		}
	}
	
}
