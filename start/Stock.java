package start;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {

	private String name;
	private String stockID;
	private int price;
	private int basePrice;
	private int maxPrice;
	private int minPrice;
	private int volatility;
	
	private boolean exists;
	
	public Stock (String name) {
		this.stockID = name;
		
		getInfo();
	}
	
	public boolean getInfo () {
		// FIND THIS STOCK IN THE DB IF IT EXISTS
		MySQL mysql = new MySQL();
		
		PreparedStatement stmt = mysql.prepareStatement("SELECT * FROM stocks WHERE stockID LIKE ? ");
		try {
			stmt.setString(1, stockID);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet result = mysql.query(stmt);
		
		
		try {
			while (result.next()) {
				// WE FOUND IT, STORE SOME INFO
				name = result.getString("name");
				price = result.getInt("price");
				basePrice = result.getInt("basePrice");
				maxPrice = result.getInt("maxPrice");
				minPrice = result.getInt("minPrice");
				volatility = result.getInt("volatility");
				exists = true;
				mysql.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
		
		exists = false;
		return false;
	}
	
	public boolean exists() {
		return this.exists;
	}
	
	public int getMinPrice() {
		return this.minPrice;
	}
	
	public int getMaxPrice() {
		return this.maxPrice;
	}
	
	public int getBasePrice() {
		return this.basePrice;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public int getVolatility() {
		return this.volatility;
	}
	
	public String getID() {
		return this.stockID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toID() {
		return this.stockID;
	}
	
	public String toString() {
		return this.name;
	}
	
}
