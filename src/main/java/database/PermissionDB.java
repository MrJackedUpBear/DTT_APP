package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import logging.LogInfo;

public class PermissionDB {
private static String createPermission = "CREATE TABLE IF NOT EXISTS Permission ("
		+ "PermissionId INT AUTO_INCREMENT PRIMARY KEY,"
		+ "PermissionName VARCHAR(255) UNIQUE NOT NULL,"
		+ "ResourceName VARCHAR(255) NOT NULL,"
		+ "ActionType VARCHAR(255) NOT NULL"
		+ ");";
	
	private static PermissionDB p = new PermissionDB();
	
	public static PermissionDB getInstance() {
		return p;
	}
	
	private PermissionDB() {
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createPermission)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void addPermission(String permissionName, String resourceName, String actionType, LogInfo logInfo) {
		String sql = "INSERT INTO Permission VALUES (?, ?, ?, ?, ?);";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, permissionName);
			pstmt.setString(2, resourceName);
			pstmt.setString(3, actionType);
			
			int rs = pstmt.executeUpdate();
			
			if (rs > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully added permission: " + permissionName);
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error adding permission: " + permissionName + ". This likely means it already exists.");
			}
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error adding permission: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	private int getPermissionIdByName(String permissionName, LogInfo logInfo) {
		logInfo.setLevel("Error");
		logInfo.setLogInfo("getPermissionIdByName function not set up yet.");
		logInfo.addLog(logInfo);
		return -1;
	}
	
	public Permission getPermission(int permissionId, Permission permission, LogInfo logInfo) {
		String sql = "SELECT * FROM Permission WHERE PermissionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, permissionId);
			
			ResultSet rs = pstmt.executeQuery();
			
			
			while (rs.next()) {
				String permissionName = rs.getString("PermissionName");
				String resourceName = rs.getString("ResourceName");
				String actionType = rs.getString("ActionType");
				
				permission.updatePermissions(permissionName, resourceName, actionType);
				
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got permission and updated variable.");
				logInfo.addLog(logInfo);
			}
		}catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting permission: " + e.getStackTrace());
			logInfo.addLog(logInfo);
		}
		
		return permission;
	}
	
	void deletePermission(String permissionName, LogInfo logInfo) {
		int permissionId = getPermissionIdByName(permissionName, logInfo);
		
		if (permissionId == -1) {
			return;
		}
		
		String sql = "DELETE FROM Permission WHERE PermissionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, permissionId);
			
			int ra = pstmt.executeUpdate();
			
			if (ra > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully deleted permission: " + permissionName);
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error deleting permission: " + permissionName + ". This likely means this permission does not exist.");
			}
		}catch(SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error delete permission: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	public int getTotalPermissions(LogInfo logInfo) {
		String sql = "SELECT COUNT(*) \"Total\"FROM Permission;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got total number of permissions.");
				logInfo.addLog(logInfo);
				return rs.getInt("Total");
			}else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Error getting total permissions. This likely means something is broken.");
			}
		}catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting total permissions: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return -1;
	}
}
