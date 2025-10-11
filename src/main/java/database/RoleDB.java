package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import logging.LogInfo;

public class RoleDB {
	private static String createRoleDB = "CREATE TABLE IF NOT EXISTS Role ("
			+ "RoleId INT AUTO_INCREMENT PRIMARY KEY,"
			+ "RoleName VARCHAR(255) UNIQUE NOT NULL"
			+ ");";
	
	private static RoleDB r = new RoleDB();
	
	public RoleDB getInstance() {
		return r;
	}
	
	private RoleDB() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createRoleDB)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void createRole(String roleName, LogInfo logInfo) {
		String sql = "INSERT INTO TABLE VALUES (?);";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, roleName);
			
			int ar = pstmt.executeUpdate();
			
			if (ar > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully add role: " + roleName);
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error adding role: " + roleName + ". This likely means the role already exists.");
			}
		}catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error creating role: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	void deleteRole(String roleName, LogInfo logInfo) {
		String sql = "DELETE FROM Role WHERE RoleName=?";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, roleName);
			
			int ra = pstmt.executeUpdate();
			
			if (ra > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully deleted role: " + roleName);
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error deleting role: " + roleName + ". This likely means the role does not exist.");
			}
		}catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error deleting roll: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	String getRole(int roleID, LogInfo logInfo) {
		String sql = "SELECT RoleName FROM Role WHERE roleId=?;";
		
		String role = "";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, roleID);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				role = rs.getString("RoleName");
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got role: " + role);
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error getting role: " + roleID);
			}
		}catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting role: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return role;
	}
}
