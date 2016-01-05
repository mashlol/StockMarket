package com.github.mashlol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

	private Connection con = null;
        public static boolean dbstatus = true;
        
        public void throwSQLException (SQLException e, String msg) {
            if (msg != null) {
                System.out.println("[StockMarket] " + msg);
            }
            if (StockMarket.debugMode == true) {
                e.printStackTrace();
            }
        }
	public MySQL () {
		final String driver = "com.mysql.jdbc.Driver";
		String connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort + "/" + StockMarket.mysqlDB;
		final String user = StockMarket.mysqlUser;
		final String password = StockMarket.mysqlPW;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(connection, user, password);
			
			
			setUpTables();
			
		} catch (SQLException e) {
			try {
				connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort;
				con = DriverManager.getConnection(connection, user, password);
				
				execute("CREATE DATABASE IF NOT EXISTS " + StockMarket.mysqlDB);
				
				connection = "jdbc:mysql://" + StockMarket.mysqlIP + ":" + StockMarket.mysqlPort + "/" + StockMarket.mysqlDB;
				con = DriverManager.getConnection(connection, user, password);
				
				setUpTables();
			} catch (SQLException e1) {
                                throwSQLException(e1, "Failed to create database during initialisation. Most likely due to incorrect database settings in config file.");
                                dbstatus = false;
			}
			
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
                        System.out.println("[StockMarket] " + "SQL Drivers not installed. The server and java installation must support JDBC connections.");
                        if (StockMarket.debugMode == true) {
                            e.printStackTrace();
                        }
                        dbstatus = false;
                }
	}
	
	private void setUpTables() {
		try {
			execute("CREATE TABLE IF NOT EXISTS stocks (id int NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name tinytext, stockID tinytext, price decimal(10, 2), basePrice decimal(10, 2), maxPrice decimal(10, 2), minPrice decimal(10, 2), volatility decimal(10, 2), amount int, dividend decimal(10, 2))");
			execute("CREATE TABLE IF NOT EXISTS players (id int NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name tinytext)");
			execute("CREATE TABLE IF NOT EXISTS looptime (looptime int NOT NULL DEFAULT 0, PRIMARY KEY(looptime), looptime2 int NOT NULL DEFAULT 0)");	
		} catch (SQLException e) {
                        throwSQLException(e,"Could not execute create table statements during initialisation.");
                        dbstatus = false;
                }
			ResultSet result = query("SELECT * FROM looptime");
			
			try {
				boolean found = false;
				while (result.next()) {
					found = true;
				}
				if (!found) {
					try {
                                            execute("INSERT INTO looptime (looptime, looptime2) VALUES(0, 0)");
					} catch (SQLException e) {
                                            throwSQLException(e,"Database Error - Could not update looptime!");
                                            dbstatus = false;
					}	
				}
			} catch (SQLException e) {
                            throwSQLException(e,"General Database error. Enable debug-mode for more info!");
                            dbstatus = false;
			}
			
			try {
                            // TODO: Move this to the onEnable function as it is being called repeatedly for no reason.
				execute("ALTER TABLE stocks ADD COLUMN amount int");
			} catch (SQLException e) {
                            if (StockMarket.debugMode == true) {
                                e.printStackTrace();
                            }
                            dbstatus = true;
                        }			
			try {
				execute("ALTER TABLE stocks ADD COLUMN dividend decimal(10, 2)");
			} catch (SQLException e) {
                            // TODO: Move this to the onEnable function as it is being called repeatedly for no reason.
                            if (StockMarket.debugMode == true) {
                                e.printStackTrace();
                            }	
                            dbstatus = true;
			}
	}
			
	
	public ResultSet query (PreparedStatement stmt) {
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();
		} catch (SQLException e4) {
                            throwSQLException(e4,"General Database error. Enable debug-mode for more info.");
                            dbstatus = false;
		}
		
		return rs;
	}
	
	public ResultSet query(String string) {
		ResultSet rs = null;

		try {
			PreparedStatement stmt = prepareStatement(string);
			rs = stmt.executeQuery();
		} catch (SQLException e4) {
                            throwSQLException(e4,"General Database error. Enable debug-mode for more info.");
                            dbstatus = false;
		}
		
		return rs;
	}
	
	public void execute (PreparedStatement stmt) {
	
		try {
			stmt.execute();
		} catch (SQLException e4) {
                            throwSQLException(e4,"General Database error. Enable debug-mode for more info.");
                            dbstatus = false;
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
                            throwSQLException(e,"General Database error. Could not close SQL connection after use! Enable debug-mode for more info.");
                            dbstatus = false;
		}
	}
	
	public PreparedStatement prepareStatement(String s) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(s);
		} catch (SQLException e) {
                            throwSQLException(e,"General Database error. Could not convert " + s + " to a prepared database statement! Enable debug-mode for more info.");
                            dbstatus = false;
		}

		return stmt;
	}
}