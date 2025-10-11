package win.servername;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class SettingDB {
	private static String createSettingDB = "CREATE TABLE IF NOT EXISTS Setting (SettingId INT AUTO_INCREMENT, TimeLimit INT NOT NULL, NumQuestions INT NOT NULL, PRIMARY KEY(SettingId));";

	private SettingDB() {
        try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }

		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			
			if (stmt.execute(createSettingDB)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error creating table: " + e.getMessage());
		}
	}

    static SettingDB settingDb = new SettingDB();

    public static SettingDB getInstance(){
        return settingDb;
    }
	
	public Boolean createSetting(Setting setting) {
        String sql = "INSERT INTO Setting (TimeLimit, NumQuestions) VALUES (?, ?)";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, setting.getTimeLimit());
            stmt.setInt(2, setting.getNumQuestions());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    setting.setSettingId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // READ by ID
    public Optional<Setting> getSettingById(int settingId) {
        String sql = "SELECT SettingId, TimeLimit, NumQuestions FROM Setting WHERE SettingId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, settingId);
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

    // UPDATE
    public boolean updateSetting(Setting setting) {
        String sql = "UPDATE Setting SET TimeLimit = ?, NumQuestions = ? WHERE SettingId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, setting.getTimeLimit());
            stmt.setInt(2, setting.getNumQuestions());
            stmt.setInt(3, setting.getSettingId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteSetting(int settingId) {
        String sql = "DELETE FROM Setting WHERE SettingId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, settingId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Map result row -> Setting
    private Setting mapRow(ResultSet rs) throws SQLException {
        Setting setting = new Setting();
        setting.setSettingId(rs.getInt("SettingId"));
        setting.setTimeLimit(rs.getInt("TimeLimit"));
        setting.setNumQuestions(rs.getInt("NumQuestions"));
        return setting;
    }
}
