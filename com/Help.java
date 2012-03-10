package com;

import java.util.Iterator;

import org.bukkit.entity.Player;

public class Help {

	private final int NUM_PER_PAGE = 5;
	
	Player player;
	
	public Help (Player player) {
		this.player = player;
	}
	

	public void display(int page) {
		Message m = new Message(player);
		
		m.successMessage("Help: [Page " + page + " of " + numPages() + "]");
		int n = 0;
		Iterator<Command> it = StockMarket.commands.iterator();
		while (it.hasNext()) {
			Command c = it.next();
			// CHECK FOR PERMISSION NODE
			n++;
			if (n > NUM_PER_PAGE * page)
				return;
			else if (n > NUM_PER_PAGE * (page-1))
				m.helpMessage("/sm " + c.getCommand() + " " + c.getParameters() + " - " + c.getCommandHelp());
		}
	}
	
	private int numPages () {
		int n = 0;
		
		Iterator<Command> it = StockMarket.commands.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unused")
			Command c = it.next();
			// CHECK FOR PERMISSION NODE
			n++;
		}
		
		return ((int) n / NUM_PER_PAGE) + 1;
	}
	
}
