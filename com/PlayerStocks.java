package com;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerStocks {

	private Player player;
	private HashMap<String, PlayerStock> stock = new HashMap<String, PlayerStock>();
	private boolean exists;
	
	public PlayerStocks (Player player) {
		this.player = player;
		
		exists = getPlayerInfo();
	}
	
	private boolean getPlayerInfo() {
		// FIND THIS PLAYER IN THE DB, FILL IN HIS INFO
		MySQL mysql = new MySQL();
		
		// NOW LETS FIND EM
		PreparedStatement stmt = mysql.prepareStatement("SELECT * FROM players WHERE name LIKE ? ");
		try {
			stmt.setString(1, player.getName());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet result = mysql.query(stmt);
		
		
		try {
			while (result.next()) {
				// WE FOUND IT, STORE SOME INFO
				stmt = mysql.prepareStatement("SELECT stockID FROM stocks");
				ResultSet result2 = mysql.query(stmt);
				while (result2.next()) {
					PlayerStock newS = new PlayerStock();
					
					newS.stock = new Stock(result2.getString("stockID"));
					newS.amount = result.getInt(newS.stock.toID());
					
					this.stock.put(newS.stock.getID().toUpperCase(), newS);
				}
				
				mysql.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// WE DIDNT FIND IT, LETS CREATE IT
		stmt = mysql.prepareStatement("INSERT INTO players (name) Values(?)");
		try {
			stmt.setString(1, player.getName());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mysql.execute(stmt);
		
		
		mysql.close();
		return false;
	}
	
	public boolean exists() {
		return this.exists;
	}
	
	public boolean sell (Stock stock, int amount) {
		Message m = new Message(player);
		
		if (stock.exists()) {
				// CHECK THE PLAYER HAS ENOUGH TO SELL
				if (this.stock.get(stock.getID()).amount - amount < 0) {
					m.errorMessage("Failed to sell!  Check that you have that many!");
					return false;
				}
				
				// OKAY THEY DO, LETS SELL EM
				this.stock.get(stock.getID()).amount -= amount;
				
				// OK NOW LETS UPADTE THE DATABASE
				MySQL mysql = new MySQL();
				PreparedStatement stmt = mysql.prepareStatement("UPDATE players SET " + stock.getID() + " = ? WHERE name LIKE ?");
				try {
					stmt.setInt(1, this.stock.get(stock.getID()).amount);
					stmt.setString(2, player.getName());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mysql.execute(stmt);
				mysql.close();
				
				StockMarket.economy.depositPlayer(player.getName(), amount * stock.getPrice());
				m.successMessage("Successfully sold " + amount + " " + stock + " stocks for " + stock.getPrice() + " " + StockMarket.economy.currencyNameSingular() + " each.");
				return true;
		} else {
			m.errorMessage("Invalid stock ID");
			return false;
		}
	}
	
	public boolean buy (Stock stock, int amount) {
		Message m = new Message(player);
		
		if (stock.exists()) {
			// CHECK THE PLAYER HAS ENOUGH MONEY TO BUY THIS MANY
			if (StockMarket.economy.getBalance(player.getName()) < amount * stock.getPrice()) {
				m.errorMessage("Not enough money!");
				return false;
			}
			
			// OKAY THEY DO, LETS BUY EM
			this.stock.get(stock.getID()).amount += amount;
			
			// OK NOW LETS UPADTE THE DATABASE
			MySQL mysql = new MySQL();
			PreparedStatement stmt = mysql.prepareStatement("UPDATE players SET " + stock.getID() + " = ? WHERE name LIKE ?");
			try {
				stmt.setInt(1, this.stock.get(stock.getID()).amount);
				stmt.setString(2, player.getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mysql.execute(stmt);
			mysql.close();
			
			StockMarket.economy.depositPlayer(player.getName(), -1 * amount * stock.getPrice());
			m.successMessage("Successfully purchased " + amount + " " + stock + " stocks for " + stock.getPrice() + " " + StockMarket.economy.currencyNameSingular() + " each.");
			return true;
		} else {
			m.errorMessage("Invalid stock ID");
			return false;
		}
	}
	
	public void listAll () {
		Message m = new Message(player);
		
		m.successMessage("List of stocks:");
		for (PlayerStock ps : stock.values())
			m.regularMessage(ps.stock.getID() + " - Price: " + ps.stock.getPrice());
	}
	
	public void listMine () {
		Message m = new Message(player);
		
		if (!hasStocks()) {
			m.errorMessage("You don't own any stocks. /sm help for help.");
			return;
		}
		
		m.successMessage("List of your stocks:");
		for (PlayerStock ps : stock.values())
			if (ps.amount > 0) {
				m.regularMessage(ps.stock.getID() + " - Amount: " + ps.amount + " - Price: " + ps.stock.getPrice());
			}
	}
	
	public boolean hasStocks () {
		for (PlayerStock ps : stock.values())
			if (ps.amount > 0)
				return true;
		
		return false;
	}
	
}
