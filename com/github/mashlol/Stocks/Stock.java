package com.github.mashlol.Stocks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;

import com.github.mashlol.MySQL;

public class Stock {

	private String name;
	private String stockID;
	private double price;
	private double basePrice;
	private double maxPrice;
	private double minPrice;
	private double volatility;
	private int amount;
	private double dividend;
	
	private boolean exists;
	
	public Stock (String name) {
		this.stockID = name;
		
		exists = getInfo();
	}
	
	private boolean getInfo () {
		// FIND THIS STOCK IN THE DB IF IT EXISTS
		MySQL mysql = new MySQL();
		
		PreparedStatement stmt = mysql.prepareStatement("SELECT * FROM stocks WHERE stockID LIKE ? ");
		try {
			stmt.setString(1, stockID);
		} catch (SQLException e) {
			
		}
		ResultSet result = mysql.query(stmt);
		
		try {
			while (result.next()) {
				// WE FOUND IT, STORE SOME INFO
				name = result.getString("name");
				price = result.getDouble("price");
				basePrice = result.getDouble("basePrice");
				maxPrice = result.getDouble("maxPrice");
				minPrice = result.getDouble("minPrice");
				volatility = result.getDouble("volatility");
				amount = result.getInt("amount");
				dividend = result.getDouble("dividend");
				mysql.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
		
		return false;
	}
	
	public boolean add (String name, String stockID, double baseprice, double maxprice, double minprice, double volatility, int amount, double dividend) {
		MySQL mysql = new MySQL();
		try {
			mysql.execute("ALTER TABLE players ADD COLUMN " + stockID + " INT DEFAULT 0");
		} catch (SQLException e) {
			return false;
		}
		
		PreparedStatement stmt = mysql.prepareStatement("INSERT INTO stocks (name, stockID, price, basePrice, maxPrice, minPrice, volatility, amount, dividend) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		try {
			stmt.setString(1, name);
			stmt.setString(2, stockID);
			stmt.setDouble(3, baseprice);
			stmt.setDouble(4, baseprice);
			stmt.setDouble(5, maxprice);
			stmt.setDouble(6, minprice);
			stmt.setDouble(7, volatility);
			stmt.setInt(8, amount);
			stmt.setDouble(9, dividend);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		mysql.execute(stmt);
		mysql.close();
		
		return true;
	}
	
	public boolean set (String name, String stockID, double baseprice, double maxprice, double minprice, double volatility, int amount, double dividend) {
		MySQL mysql = new MySQL();
		
		PreparedStatement stmt = mysql.prepareStatement("UPDATE stocks SET name = ?, basePrice = ?, maxPrice = ?, minPrice = ?, volatility = ?, amount = ?, dividend = ? WHERE StockID LIKE ?");
		try {
			stmt.setString(1, name);
			stmt.setDouble(2, baseprice);
			stmt.setDouble(3, maxprice);
			stmt.setDouble(4, minprice);
			stmt.setDouble(5, volatility);
			stmt.setInt(6, amount);
			stmt.setDouble(7, dividend);
			stmt.setString(8, stockID);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		mysql.execute(stmt);
		mysql.close();
		
		return true;
	}
	
	public boolean remove () {
		MySQL mysql = new MySQL();
		
		try {
			mysql.execute("ALTER TABLE players DROP COLUMN " + stockID);
		} catch (SQLException e1) {
			return false;
		}
		
		PreparedStatement stmt = mysql.prepareStatement("DELETE FROM stocks WHERE StockID LIKE ?");
		try {
			stmt.setString(1, stockID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.execute(stmt);
		
		mysql.close();
		
		return true;
	}
	
	public boolean changePrice (double amount) {
		MySQL mysql = new MySQL();
		
		DecimalFormat newFormat = new DecimalFormat("#.##");
		amount =  Double.valueOf(newFormat.format(amount));
		
		PreparedStatement stmt = null;
		if (getPrice() + amount > getMaxPrice()) {
			stmt = mysql.prepareStatement("UPDATE stocks SET price = ? WHERE stockID = ?");
			try {
				stmt.setDouble(1, getMaxPrice());
				stmt.setString(2, getID());
			} catch (SQLException e) {
				
			}
		} else if (getPrice() + amount < getMinPrice()) {
			stmt = mysql.prepareStatement("UPDATE stocks SET price = ? WHERE stockID = ?");
			try {
				stmt.setDouble(1, getMinPrice());
				stmt.setString(2, getID());
			} catch (SQLException e) {
				
			}
		} else {
			stmt = mysql.prepareStatement("UPDATE stocks SET price = price + ? WHERE stockID = ?");
			try {
				stmt.setDouble(1, amount);
				stmt.setString(2, getID());
			} catch (SQLException e) {
				
			}
		}
		
		
		mysql.execute(stmt);
		return true;
	}
	
	public double updatePrice(boolean up, double scalar) {
		double d = 0;
		Random random = new Random();
		double a = random.nextDouble();
		
		if (up) {
			d = ((double) getVolatility() / 100) * (a * (scalar * .01) * (getBasePrice() + 1));
//			if (getPrice() == getBasePrice()) {
//				d = ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			} else if (getPrice() - getBasePrice() > 0) {
//				d = (1/((getPrice() - getBasePrice())/getBasePrice())) * ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			} else {
//				d = ((double) Math.abs(getPrice() - getBasePrice()) / 5) * ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			}
		} else {
			d = (-1) * ((double) getVolatility() / 100) * (a * (scalar * .01) * (getBasePrice() + 1));
//			if (getPrice() - getBasePrice() == 0) {
//				d = (-1) * ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			} else if (getPrice() - getBasePrice() > 0) {
//				d = (-1) * ((double) Math.abs(getPrice() - getBasePrice()) / 5) * ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			} else {
//				d = (-1) * (1/((getPrice() - getBasePrice())/getBasePrice())) * ((double) getVolatility() / 100) * ((a * scalar) + 1);
//			}
		}
		
		//System.out.println("Number: " + d + ", Random number: " + a + ", Random & Scalar: " + (a * (scalar * .01) * getBasePrice()));
		
		return d;
	}
	
	public boolean exists() {
		return this.exists;
	}
	
	public double getMinPrice() {
		return this.minPrice;
	}
	
	public double getMaxPrice() {
		return this.maxPrice;
	}
	
	public double getBasePrice() {
		return this.basePrice;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public double getVolatility() {
		return this.volatility;
	}
	
	public String getID() {
		return this.stockID.toUpperCase();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toID() {
		return this.stockID.toUpperCase();
	}
	
	public String toString() {
		return this.name;
	}
	
	public int getAmount () {
		return this.amount;
	}
	
	public double getDividend () {
		return this.dividend;
	}
	
}
