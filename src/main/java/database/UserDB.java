package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import logging.LogInfo;

public class UserDB {
	private static String createUserDB = "CREATE TABLE IF NOT EXISTS User (UserId INT NOT NULL AUTO_INCREMENT, FirstName VARCHAR(255) NOT NULL, LastName VARCHAR(255), Email VARCHAR(255) NOT NULL, Password VARBINARY(255), "
			+ "SettingId INT NOT NULL, Salt VARBINARY(255), RefreshToken VARCHAR(255), PRIMARY KEY(UserId), UNIQUE(Email), UNIQUE(RefreshToken));";

	private static UserDB user = new UserDB();
	
	private UserDB() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			
			if (stmt.execute(createUserDB)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error creating table: " + e.getMessage());
		}
	}
	
	public static UserDB getInstance() {
		return user;
	}
	
	public boolean createUser(User user) {
        String sql = "INSERT INTO User (FirstName, LastName, Email, Password, Salt, SettingId) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setBytes(4, user.getPassword());
            stmt.setBytes(5, user.getSalt());
            stmt.setInt(6, user.getSettingId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ by ID
    public Optional<User> getUserById(int userId) {
        String sql = "SELECT UserId, FirstName, LastName, Email, Password, Salt, SettingId FROM User WHERE UserId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // READ by Email
    public Optional<User> getUserByEmail(String email, LogInfo logInfo) {
        String sql = "SELECT UserId, FirstName, LastName, Email, Password, Salt, SettingId FROM User WHERE Email = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                	logInfo.setLevel("Info");
                	logInfo.setLogInfo("Successfully got user.");
                	logInfo.addLog(logInfo);
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLevel("Error");
            logInfo.setLogInfo("Error getting user: " + e.getStackTrace());
            logInfo.addLog(logInfo);
        }
        return Optional.empty();
    }

    // UPDATE (general info)
    public boolean updateUser(User user) {
        String sql = "UPDATE User SET FirstName = ?, LastName = ?, Email = ?, SettingId = ? WHERE UserId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getSettingId());
            stmt.setInt(5, user.getUserId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE credentials (Password + Salt together)
    public boolean updateCredentials(int userId, byte[] password, byte[] salt) {
        String sql = "UPDATE User SET Password = ?, Salt = ? WHERE UserId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, password);
            stmt.setBytes(2, salt);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM User WHERE UserId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Map result row -> User
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserId"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));
        user.setEmail(rs.getString("Email"));
        user.setPassword(rs.getBytes("Password"));
        user.setSalt(rs.getBytes("Salt"));
        user.setSettingId(rs.getInt("SettingId"));
        return user;
    }
}
