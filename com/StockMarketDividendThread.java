package com;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StockMarketDividendThread extends Thread {

	private boolean loop = true;
	private int loopTimes = 0;
	
	public StockMarketDividendThread (){
		super ("StockMarketDividendThread");
		
		MySQL mysql = new MySQL();
		
		ResultSet result = mysql.query("SELECT looptime2 FROM looptime");
		
		try {
			while (result.next()) {
				loopTimes = result.getInt("looptime2");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
	
	public void run() {
		if (StockMarket.dividendFreq == 0)
			loop = false;
		while (loop) {
			// SLEEP
			try {
				Thread.sleep(60000); // THIS DELAY COULD BE CONFIG'D
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (loop) {
				loopTimes++;
	
				// DO SOME EVENT STUFF
				
				if (loopTimes % StockMarket.dividendFreq == 0) {
					broadcastMessage("Paying out all stock dividends");
					
					if (StockMarket.payOffline == true) {
						MySQL mysql = new MySQL();
						ResultSet result = mysql.query("SELECT name FROM players");
						
						try {
							while (result.next()) {
								String playerName = result.getString("name");
								Player p = Bukkit.getServer().getPlayer(playerName);
								PlayerStocks ps;
								if (p != null)
									 ps = new PlayerStocks(p);
								else
									ps = new PlayerStocks(playerName);
								ps.payoutDividends();
							}
						} catch (SQLException e) {
							
						}
					} else {
						Player[] players = Bukkit.getOnlinePlayers();
		                //loop through all of the online players and give them all a random item and amount of something, The diamond ore breaker will not get a reward.
		                for (Player player : players) {
		                	PlayerStocks ps = new PlayerStocks(Bukkit.getServer().getPlayer(player.getName()));
							ps.payoutDividends();
		                }
						
					}
				}
			}
		}
	}
	
	public void finish() {
		loop = false;
		
		MySQL mysql = new MySQL();
		
		try {
			mysql.execute("UPDATE looptime SET looptime2 = " + loopTimes);
		} catch (SQLException e) {
			
		}
		
		mysql.close();
	}
	
	private void broadcastMessage (String message) {
		Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketPayday" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + message);
	}
	
	
}
