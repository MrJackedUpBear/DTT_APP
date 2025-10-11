package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import logging.LogInfo;

public class RolePermissionsDB {
	private static String createRolePermissionsDB = "CREATE TABLE IF NOT EXISTS RolePermission ("
			+ "RoleId INT NOT NULL,"
			+ "PermissionId INT NOT NULL,"
			+ "PRIMARY KEY(RoleId, PermissionId),"
			+ "FOREIGN KEY (RoleId) REFERENCES Role(RoleId) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "FOREIGN KEY (PermissionId) REFERENCES Permission(PermissionId) ON DELETE CASCADE ON UPDATE CASCADE"
			+ ");";
	
	private static RolePermissionsDB r = new RolePermissionsDB();
	
	public static RolePermissionsDB getInstance() {
		return r;
	}
	
	private RolePermissionsDB() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createRolePermissionsDB)) {
				System.out.println("RolePermission table created successfully.");
			} else {
				System.out.println("RolePermission table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}

	// Add a mapping between a role and a permission
	void addRolePermission(int roleId, int permissionId, LogInfo logInfo) {
		String sql = "INSERT INTO RolePermission (RoleId, PermissionId) VALUES (?, ?)";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, roleId);
			pstmt.setInt(2, permissionId);
			
			int ar = pstmt.executeUpdate();
			
			if (ar > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully assigned PermissionId " + permissionId + " to RoleId " + roleId);
			} else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Failed to assign PermissionId " + permissionId + " to RoleId " + roleId);
			}
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error adding role-permission mapping: " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
	}
	
	// Remove a mapping between a role and a permission
	void removeRolePermission(int roleId, int permissionId, LogInfo logInfo) {
		String sql = "DELETE FROM RolePermission WHERE RoleId = ? AND PermissionId = ?";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, roleId);
			pstmt.setInt(2, permissionId);
			
			int ra = pstmt.executeUpdate();
			
			if (ra > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully removed PermissionId " + permissionId + " from RoleId " + roleId);
			} else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("No RolePermission mapping found for RoleId " + roleId + " and PermissionId " + permissionId);
			}
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error removing role-permission mapping: " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
	}

	// Retrieve all permissions assigned to a specific role
	public ArrayList<Integer> getPermissionsForRole(int roleId, LogInfo logInfo) {
		String sql = "SELECT PermissionId FROM RolePermission WHERE RoleId = ?";
		
		ArrayList<Integer> permissions = new ArrayList<>();
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, roleId);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				permissions.add(rs.getInt("PermissionId"));
			}
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully retrieved permissions for RoleId " + roleId);
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error retrieving permissions for RoleId " + roleId + ": " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
		
		return permissions;
	}

	// Retrieve all roles that have a specific permission
	void getRolesWithPermission(int permissionId, LogInfo logInfo) {
		String sql = "SELECT RoleId FROM RolePermission WHERE PermissionId = ?";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, permissionId);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println("Roles with PermissionId " + permissionId + ":");
			while (rs.next()) {
				System.out.println("- RoleId: " + rs.getInt("RoleId"));
			}
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully retrieved roles for PermissionId " + permissionId);
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error retrieving roles for PermissionId " + permissionId + ": " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
	}
}
