package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import logging.LogInfo;

public class RefreshToken {
	String createRefreshTokenTable = "CREATE TABLE IF NOT EXISTS RefreshToken (Token VARCHAR(255) NOT NULL, ExpirationDate DATE NOT NULL, UserId INT NOT NULL, CreationDate DATE, PRIMARY KEY(Token));";

	private static RefreshToken instance = new RefreshToken();
	
	public static RefreshToken getInstance() {
		return instance;
	}
	
	private RefreshToken() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			
			if(stmt.execute(createRefreshTokenTable)) {
				System.out.println("Table created successfully.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error connecting: " + e.getMessage());
		}
	}
	
	public void addToken(String token, LocalDate expirationDate, int userId, LogInfo logInfo) {
		String sql = "INSERT INTO RefreshToken VALUES (?, ?, ?, ?);";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, token);
			pstmt.setDate(2, Date.valueOf(expirationDate));
			pstmt.setInt(3, userId);
			pstmt.setDate(4, Date.valueOf(LocalDate.now()));
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected == 0) {
				System.out.println("Error adding token");
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Unknown error adding token.");
			}else {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully added refresh token.");
			}
		} catch(SQLException e) {
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error adding refresh token: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	public void deleteToken(String token) {
		String sql = "DELETE FROM RefreshToken WHERE Token=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, token);
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected == 0) {
				System.out.println("Error deleting token");
			}
		}catch(SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public int getUser(String token, LogInfo logInfo) {
		String sql = "SELECT ExpirationDate, UserId FROM RefreshToken WHERE Token=?;";
		
		int userId = -1;
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, token);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				Date expirationDate = rs.getDate(1);
				LocalDate currentDate = LocalDate.now();
				if (expirationDate.after(Date.valueOf(currentDate))) {
					logInfo.setLevel("Info");
					logInfo.setLogInfo("Successfully got user.");
					userId = rs.getInt(2);
					logInfo.setUser(UserDB.getInstance().getUserById(userId).get());
				}else {
					userId = rs.getInt(2);
					deleteToken(token);
					logInfo.setLevel("Info");
					logInfo.setLogInfo("Token has expired.");
					logInfo.setUser(UserDB.getInstance().getUserById(userId).get());
					userId = -1;
				}
							}
		}catch(SQLException e) {
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting user: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return userId;
	}
}
