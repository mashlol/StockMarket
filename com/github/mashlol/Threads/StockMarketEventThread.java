package com.github.mashlol.Threads;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.mashlol.MySQL;
import com.github.mashlol.StockMarket;
import com.github.mashlol.Events.EventInstance;
import com.github.mashlol.Stocks.Stock;
import com.github.mashlol.Stocks.Stocks;

public class StockMarketEventThread extends Thread {

	private boolean loop = true;
	private int loopTimes = 0;
	
	public StockMarketEventThread (){
		super ("StockMarketEventThread");
		
		MySQL mysql = new MySQL();
		
		ResultSet result = mysql.query("SELECT looptime FROM looptime");
		
		try {
			while (result.next()) {
				loopTimes = result.getInt("looptime");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
	
	public void run() {
		if (StockMarket.randomEventFreq == 0)
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
				
				if (loopTimes % StockMarket.randomEventFreq == 0) {
					loopTimes = 0;
					Stocks stocks = new Stocks();
					
					if (stocks.numStocks() > 0) {
						Stock stock = stocks.getRandomStock();
						EventInstance ei = new EventInstance();
						ei.forceRandomEvent(stock);
					}
				}
			}
		}
	}
	
	public void finish() {
		loop = false;
		
		MySQL mysql = new MySQL();
		
		try {
			mysql.execute("UPDATE looptime SET looptime = " + loopTimes);
		} catch (SQLException e) {
			
		}
		
		mysql.close();
	}
	
	
}
