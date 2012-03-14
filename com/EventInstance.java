package com;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class EventInstance {

	private Vector<Event> events = new Vector<Event>();
	private Random random = new Random();
	
	public EventInstance() {
		initEvents();
	}
	
	private void initEvents() {
		events.add(new Event("The CEO of %s has died of a heart attack.  The replacement is looking to be a poor one.", 40, false, 50));
		events.add(new Event("The CEO of %s has died of a heart attack.  The replacement looks like he is even better than his successor!", 40, true, 50));
		events.add(new Event("%s has announced a new product to be released!  Speculation is high!", 8, true, 250));
		events.add(new Event("%s has announced a new product to be released!  Speculation is high!", 8, false, 250));
		events.add(new Event("%s's announced product was a huge success, customers are extremely satisfied!", 45, true, 250));
		events.add(new Event("%s's announced product was poorly made, causing poor customer satisfaction!", 45, false, 250));
		events.add(new Event("%s just announced that they are replacing their current CEO, who has a bad reputation.", 25, true, 50));
		events.add(new Event("%s just announced that they are replacing their current CEO, who everyone loves.", 25, false, 50));
		events.add(new Event("%s had a bad quarter.", 30, false, 100));
		events.add(new Event("%s had a fantastic quarter.", 30, true, 100));
		events.add(new Event("Everyone thinks %s is about to come out with the most amazing product!", 65, true, 5));
		events.add(new Event("%s is on the brink of bankrupcy.", 65, false, 5));
		events.add(new Event("%s just invented the next best thing since sliced bread!", 400, true, 1));
		events.add(new Event("%s just went bankrupt.", 400, false, 1));
	}
	
	private Event getRandomEvent() {
		int r = random.nextInt(totalPossibilities());
		int i = 0;
		
		Iterator<Event> it = events.iterator();
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
		Iterator<Event> it = events.iterator();
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
		Bukkit.getServer().broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "StockMarketEvent" + ChatColor.WHITE + "] " + ChatColor.DARK_GREEN + message);
	}
	
}
