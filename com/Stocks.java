package com;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

public class Stocks {

	public Vector<Stock> stock = new Vector<Stock>();
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
	
	Stock randomStock () {
		return stock.get(random.nextInt(stock.size()));
	}
	
}
