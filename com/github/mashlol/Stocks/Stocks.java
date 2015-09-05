package com.github.mashlol.Stocks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

import com.github.mashlol.MySQL;

public class Stocks {

	private Vector<Stock> stock = new Vector<Stock>();
	private Random random = new Random();
	
	public Stocks () {
		// WE FOUND IT, STORE SOME INFO
		MySQL mysql = new MySQL();
		
		PreparedStatement stmt = mysql.prepareStatement("SELECT stockID FROM stocks");
		ResultSet result = mysql.query(stmt);
		try {
			while (result.next()) {
				stock.add(new Stock(result.getString("stockID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
	
	public Stock getRandomStock () {
		return stock.get(random.nextInt(stock.size()));
	}
	
	public int numStocks () {
		return stock.size();
	}
	
}
