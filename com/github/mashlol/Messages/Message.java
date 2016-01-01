package com.github.mashlol.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

	private Player player;
	
	public Message (Player player) {
		this.player = player;
	}
	
	public void displayInfo () {
		successMessage("Current version: v" + Bukkit.getServer().getPluginManager().getPlugin("StockMarket").getDescription().getVersion() + " developed by Mash.");
	}
	
	public void unknownCommand () {
		errorMessage("Unknown command.  Use /sm help for help.");
	}
	
	public void errorMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "SM" + ChatColor.WHITE + "]" + ChatColor.RED + " " + message);
		else
			System.out.println("[SM] " + message);
	}
	
	public void regularMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "SM" + ChatColor.WHITE + "]" + ChatColor.BLUE + " " + message);
		else
			System.out.println("[SM] " + message);
	}
	
	public void successMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "SM" + ChatColor.WHITE + "]" + ChatColor.GREEN + " " + message);
		else
			System.out.println("[SM] " + message);
	}
	
	public void helpMessage(String message) {
		if (player != null)
			player.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "SM" + ChatColor.WHITE + "]" + ChatColor.AQUA + " " + message);
		else
			System.out.println("[SM] " + message);
	}
	
	public void displayHelp (int page) {
		Help h = new Help(player);
		h.display(page);
	}
	
}
