package com.github.mashlol.Events;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.github.mashlol.StockMarket;
import com.github.mashlol.Stocks.Stock;

public class EventInstance {

	private Random random = new Random();
	
	private Event getRandomEvent() {
		int r = random.nextInt(totalPossibilities());
		int i = 0;
		
		Iterator<Event> it = StockMarket.events.iterator();
		while (it.hasNext()) {
			Event e = it.next();
			i += e.getFrequency();
			if (r < i) {
				return e;
			}
		}
		
		return null;
	}
	
	private int totalPossibilities () {
		int i = 0;
		Iterator<Event> it = StockMarket.events.iterator();
		while (it.hasNext()) {
			Event e = it.next();
			i += e.getFrequency();
		}
		
		return i;
	}
	
	public boolean forceRandomEvent(Stock s) {
		Event e = getRandomEvent();
		
		broadcastMessage(e.getMessage().replaceAll("%s", s.getID()));
		s.changePrice(s.updatePrice(e.getUp(), e.getScalar()));
		
		return true;
	}
	
	private void broadcastMessage (String message) {
		if (StockMarket.broadcastEvents)
			Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "SM" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + message);
	}
	
}
