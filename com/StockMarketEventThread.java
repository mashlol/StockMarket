package com;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StockMarketEventThread extends Thread {

	private boolean loop = true;
	private int loopTimes = 0;
	
	private Random random = new Random();
	
	public StockMarketEventThread (int loopTimes){
		super ("StockMarketEventThread");
		this.loopTimes = loopTimes;
	}
	
	public void run() {
		
		while (loop) {
			// DO SOME EVENT STUFF POSSIBLY

			// GENERATE A NUMBER FROM 1-(TOTAL VOLATILITIES OF ALL STOCKS).  WHEREVER THIS LANDS THAT STOCK GETS SOME EVENT, THE CATEGORY OF THE EVENT WILL DEPEND ON THE VOLATILITY
			
			if (loopTimes % 60 == 10) {
				Stocks stocks = new Stocks();
				
				if (stocks.stock.size() > 0) {
					Stock stock = stocks.randomStock();
					
					int rand = random.nextInt(4) + 1;
					if (rand == 4) {
						broadcastMessage(ChatColor.DARK_GREEN + "The CEO of " + stock.getID() + " has died of a heart attack.  The replacement is looking to be a poor one.");
						stock.changePrice(stock.updatePrice(false, 15));
					} else if (rand == 3) {
						broadcastMessage(ChatColor.DARK_GREEN + "" + stock.getID() + " has announced a new product to be released!  Speculation is high!");
						stock.changePrice(stock.updatePrice(false, random.nextInt(8) - 4));
					} else if (rand == 2) {
						broadcastMessage(ChatColor.DARK_GREEN + "" + stock.getID() + "s announced product was poorly made, resulting in a lowered stock!");
						stock.changePrice(stock.updatePrice(false, 8));
					} else if (rand == 1) {
						broadcastMessage(ChatColor.DARK_GREEN + "" + stock.getID() + "s announced product was a huge success, increasing their stock!");
						stock.changePrice(stock.updatePrice(true, 12));
					}
				}
			}
			
			loopTimes++;
			// SLEEP
			try {
				Thread.sleep(60000); // THIS DELAY COULD BE CONFIG'D
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
