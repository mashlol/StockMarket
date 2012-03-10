package start;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.bukkit.entity.Player;

public class PlayerStocks {

	private Player player;
	private Vector<PlayerStock> stock = new Vector<PlayerStock>();
	private boolean exists;
	
	public PlayerStocks (Player player) {
		this.player = player;
		
		exists = getPlayerInfo();
	}
	
	public boolean getPlayerInfo() {
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
					
					this.stock.add(newS);
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
	
	public PlayerStock get (int i) { 
		return this.stock.get(i);
	}
	
	public boolean sell (Stock stock, int amount) {
		Message m = new Message(player);
		
		if (stock.exists()) {
			for (int i=0; i<this.stock.size(); i++) {
				if (this.stock.get(i).stock.toString().equalsIgnoreCase(stock.toString())) {
					// FOUND IT
					
					// CHECK THE PLAYER HAS ENOUGH TO SELL
					if (this.stock.get(i).amount - amount < 0) {
						return false;
					}
					
					// OKAY THEY DO, LETS SELL EM
					this.stock.get(i).amount -= amount;
					
					// OK NOW LETS UPADTE THE DATABASE
					MySQL mysql = new MySQL();
					PreparedStatement stmt = mysql.prepareStatement("UPDATE players SET " + stock.getID() + " = ? WHERE name LIKE ?");
					try {
						stmt.setInt(1, this.stock.get(i).amount);
						stmt.setString(2, player.getName());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mysql.execute(stmt);
					
					
					mysql.close();
					
					m.successMessage("Successfully sold " + amount + " " + stock + " stocks which are currently at " + stock.getPrice() + " each.");
					return true;
				}
			}
			
			m.errorMessage("Failed to sell!  Check that you have that many!");
			return false;
		} else {
			m.errorMessage("Invalid stock ID");
			return false;
		}
	}
	
	public boolean buy (Stock stock, int amount) {
		Message m = new Message(player);
		
		if (stock.exists()) {
			for (int i=0; i<this.stock.size(); i++) {
				if (this.stock.get(i).stock.toString().equalsIgnoreCase(stock.toString())) {
					// FOUND IT
					
					// CHECK THE PLAYER HAS ENOUGH MONEY TO BUY THIS MANY
					
					// OKAY THEY DO, LETS BUY EM
					this.stock.get(i).amount += amount;
					
					// OK NOW LETS UPADTE THE DATABASE
					MySQL mysql = new MySQL();
					PreparedStatement stmt = mysql.prepareStatement("UPDATE players SET " + stock.getID() + " = ? WHERE name LIKE ?");
					try {
						stmt.setInt(1, this.stock.get(i).amount);
						stmt.setString(2, player.getName());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mysql.execute(stmt);
					mysql.close();
					
					m.successMessage("Successfully purchased " + amount + " " + stock + " stocks which are currently at " + stock.getPrice() + " each.");
					return true;
				}
			}
			m.errorMessage("Failed to buy!  Check that you have enough money!");
			return false;
		} else {
			m.errorMessage("Invalid stock ID");
			return false;
		}
	}
	
	public Vector<PlayerStock> list() {
		return stock;
	}
	
}
