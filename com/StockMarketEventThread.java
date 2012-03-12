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
			// SLEEP
			try {
				Thread.sleep(60000); // THIS DELAY COULD BE CONFIG'D
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			loopTimes++;

			// DO SOME EVENT STUFF
			
			if (loopTimes % StockMarket.randomEventFreq == 0) {
				loopTimes = 0;
				Stocks stocks = new Stocks();
				
				if (stocks.stock.size() > 0) {
					Stock stock = stocks.randomStock();
					
					int rand = random.nextInt(4);
					if (rand == 0) {
						broadcastMessage("The CEO of " + stock.getID() + " has died of a heart attack.  The replacement is looking to be a poor one.");
						stock.changePrice(stock.updatePrice(false, 20));
					} else if (rand == 1) {
						broadcastMessage(stock.getID() + " has announced a new product to be released!  Speculation is high!");
						stock.changePrice(stock.updatePrice(false, random.nextInt(8) - 4));
					} else if (rand == 2) {
						broadcastMessage(stock.getID() + "s announced product was poorly made, causing poor customer satisfaction!");
						stock.changePrice(stock.updatePrice(false, 8));
					} else if (rand == 3) {
						broadcastMessage(stock.getID() + "s announced product was a huge success, customers are extremely satisfied!");
						stock.changePrice(stock.updatePrice(true, 12));
					} else if (rand == 4) {
						broadcastMessage(stock.getID() + " just announced that they are replacing their current CEO, who has a bad reputation.");
						stock.changePrice(stock.updatePrice(true, 3));
					} else if (rand == 5) {
						broadcastMessage(stock.getID() + " had a bad quarter.");
						stock.changePrice(stock.updatePrice(false, 15));
					} else if (rand == 6) {
						broadcastMessage(stock.getID() + " just invented the next best thing since sliced bread!");
						stock.changePrice(stock.updatePrice(true, 50));
					} else if (rand == 7) {
						broadcastMessage(stock.getID() + " is on the brink of bankrupcy.");
						stock.changePrice(stock.updatePrice(false, 50));
					} else if (rand == 8) {
						broadcastMessage(stock.getID() + " just went bankrupt.");
						stock.changePrice(stock.getPrice() * -1);
					} else if (rand == 9) {
						broadcastMessage(stock.getID() + " had a fantastic quarter.");
						stock.changePrice(stock.updatePrice(false, 15));
					}
				}
			}
			
			
		}
	}
	
	private void broadcastMessage (String message) {
		Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketEvent" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + message);
	}
	
	public int finish() {
		loop = false;
		return loopTimes;
	}
	
	
}
