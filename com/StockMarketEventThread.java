package com;

public class StockMarketEventThread extends Thread {

	private boolean loop = true;
	private int loopTimes = 0;
	
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
					Stock stock = stocks.getRandomStock();
					EventInstance ei = new EventInstance();
					ei.forceRandomEvent(stock);
				}
			}
			
			
		}
	}
	
	public int finish() {
		loop = false;
		return loopTimes;
	}
	
	
}
