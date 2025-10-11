package win.servername;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserRolesDB {
	private static String createUserRoleDB = "CREATE TABLE IF NOT EXISTS UserRole ("
			+ "UserId INT NOT NULL,"
			+ "RoleId INT NOT NULL,"
			+ "PRIMARY KEY(UserId, RoleId),"
			+ "FOREIGN KEY (UserId) REFERENCES User(UserId) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "FOREIGN KEY (RoleId) REFERENCES Role(RoleId) ON DELETE CASCADE ON UPDATE CASCADE"
			+ ");";
	
	private static UserRolesDB r = new UserRolesDB();
	
	public static UserRolesDB getInstance() {
		return r;
	}
	
	private UserRolesDB() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createUserRoleDB)) {
				System.out.println("UserRole table created successfully.");
			} else {
				System.out.println("UserRole table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}

	// Add a mapping between a user and a role
	void addUserRole(int userId, int roleId) {
		String sql = "INSERT INTO UserRole (UserId, RoleId) VALUES (?, ?)";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, userId);
			pstmt.setInt(2, roleId);
			
			int ar = pstmt.executeUpdate();
			
			if (ar > 0) {
				System.out.println("Successfully assigned RoleId " + roleId + " to UserId " + userId);
			} else {
				System.out.println("Failed to assign RoleId " + roleId + " to UserId " + userId);
			}
			
		} catch (SQLException e) {
			System.out.println("SQL Error adding UserRole mapping: " + e.getMessage());
		}
	}
	
    /* 
	// Remove a mapping between a user and a role
	void removeUserRole(int userId, int roleId) {
		String sql = "DELETE FROM UserRole WHERE UserId = ? AND RoleId = ?";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, userId);
			pstmt.setInt(2, roleId);
			
			int ra = pstmt.executeUpdate();
			
			if (ra > 0) {
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully removed RoleId " + roleId + " from UserId " + userId);
			} else {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("No UserRole mapping found for UserId " + userId + " and RoleId " + roleId);
			}
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error removing UserRole mapping: " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
	}

	// Retrieve all roles for a given user
	public ArrayList<Integer> getRolesForUser(int userId) {
		String sql = "SELECT RoleId FROM UserRole WHERE UserId = ?";
		
		ArrayList<Integer> roles = new ArrayList<>();
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				roles.add(rs.getInt("RoleId"));
			}
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully retrieved roles for UserId " + userId);
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error retrieving roles for UserId " + userId + ": " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
		
		return roles;
	}

	// Retrieve all users who have a given role
	void getUsersWithRole(int roleId) {
		String sql = "SELECT UserId FROM UserRole WHERE RoleId = ?";
		
		try (Connection conn = Connect.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, roleId);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println("Users with RoleId " + roleId + ":");
			while (rs.next()) {
				System.out.println("- UserId: " + rs.getInt("UserId"));
			}
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully retrieved users for RoleId " + roleId);
			
		} catch (SQLException e) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("SQL Error retrieving users for RoleId " + roleId + ": " + e.getMessage());
		}
		
		logInfo.addLog(logInfo);
	}
    */
}
