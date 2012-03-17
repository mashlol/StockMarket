package com.github.mashlol.Messages;

import java.util.Iterator;

import org.bukkit.entity.Player;

import com.github.mashlol.StockMarket;

public class Help {

	private final int NUM_PER_PAGE = 4;
	
	private Player player;
	
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
			if (player == null || StockMarket.permission.has(player, c.getPermissionNode())) {
				n++;
				if (n > NUM_PER_PAGE * page)
					return;
				else if (n > NUM_PER_PAGE * (page-1)) {
					m.helpMessage("/sm " + c.getCommand() + " " + c.getParameters());
					m.regularMessage(c.getCommandHelp());
				}
			}
		}
	}
	
	private int numPages () {
		int n = 0;
		
		Iterator<Command> it = StockMarket.commands.iterator();
		while (it.hasNext()) {
			Command c = it.next();
			// CHECK FOR PERMISSION NODE
			if (player == null || StockMarket.permission.has(player, c.getPermissionNode()))
				n++;
		}
		
		return ((int) (n-1) / NUM_PER_PAGE) + 1;
	}
	
}
