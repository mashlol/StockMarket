package start;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StockMarketEventThread extends Thread {

	private boolean loop = true;
	private int loopTimes = 0;
	
	Random generator = new Random();
	
	public StockMarketEventThread (int loopTimes){
		super ("StockMarketEventThread");
		this.loopTimes = loopTimes;
	}
	
	public void run() {
		
		while (loop) {
			// DO SOME EVENT STUFF POSSIBLY
			
			// GENERATE A NUMBER FROM 1-(TOTAL VOLATILITIES OF ALL STOCKS).  WHEREVER THIS LANDS THAT STOCK GETS SOME EVENT, THE CATEGORY OF THE EVENT WILL DEPEND ON THE VOLATILITY
			
			if (loopTimes % 120 == 10) {
				int rand = generator.nextInt(4) + 1;
				if (rand == 4) {
					broadcastMessage(ChatColor.DARK_GREEN + "The CEO of ? has died of a heart attack.  The replacement is looking to be a poor one.");
				} else if (rand == 3) {
					broadcastMessage(ChatColor.DARK_GREEN + "? has announced a new product to be released in 5 minutes!");
				} else if (rand == 2) {
					broadcastMessage(ChatColor.DARK_GREEN + "?s announced product was poorly made, resulting in a lowered stock!");
				} else if (rand == 1) {
					broadcastMessage(ChatColor.DARK_GREEN + "?s announced product was a huge success, increasing their stock!");
				}
			}
			
			loopTimes++;
			// SLEEP
			try {
				Thread.sleep(30000); // THIS DELAY COULD BE CONFIG'D
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void broadcastMessage (String message) {
		Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketEvent" + ChatColor.WHITE + "] " + message);
	}
	
	public int finish() {
		loop = false;
		return loopTimes;
	}
	
	
}
