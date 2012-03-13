package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

	private Connection con = null;
	
	public MySQL () {
		final String driver = "com.mysql.jdbc.Driver";
		String connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort + "/" + StockMarket.mysqlDB;
		final String user = StockMarket.mysqlUser;
		final String password = StockMarket.mysqlPW;
		
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(connection, user, password);
			
			
			setUpTables();
			
		}
		catch (SQLException e) {
			try {
				connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort;
				con = DriverManager.getConnection(connection, user, password);
				
				execute("CREATE DATABASE IF NOT EXISTS " + StockMarket.mysqlDB);
				
				connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort + "/" + StockMarket.mysqlDB;
				con = DriverManager.getConnection(connection, user, password);
				
				setUpTables();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setUpTables() {
		try {
			execute("CREATE TABLE IF NOT EXISTS stocks (id int NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name tinytext, stockID tinytext, price double, basePrice int, maxPrice int, minPrice int, volatility int)");
			execute("CREATE TABLE IF NOT EXISTS players (id int NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name tinytext)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet query (PreparedStatement stmt) {
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();
		} catch (SQLException e4) {
			e4.printStackTrace();
		}
		
		return rs;
	}
	
	public void execute (PreparedStatement stmt) {
	
		try {
			stmt.execute();
		} catch (SQLException e4) {
			e4.printStackTrace();
		}
	}
	
	public void execute (String s) throws SQLException {
		
		PreparedStatement stmt = prepareStatement(s);
		
		stmt.execute();
	}
	
	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("A bad call to mysql.close() occurred");	
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String s) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(s);
		} catch (SQLException e) {
			System.out.println("An unexpected error occurred while generating a PreparedStatement"); 
			e.printStackTrace();
		}

		return stmt;
	}
}