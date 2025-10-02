package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

public class QuestionUserInformationDB {
	private static String createQUIDB = "CREATE TABLE IF NOT EXISTS Question_User_Information (QUIId INT AUTO INCREMENT, DateAccessed DATETIME NOT NULL, QuestionId INT NOT NULL, UserId INT NOT NULL, "
			+ "AnsweredCorrectly TINYINT, PRIMARY KEY(QUIId));";

	private QuestionUserInformationDB() {
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			
			if (stmt.execute(createQUIDB)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error creating table: " + e.getMessage());
		}
	}
	
	// CREATE
    public boolean createQUI(QuestionUserInformation qui) {
        String sql = "INSERT INTO Question_User_Information (DateAccessed, QuestionId, UserId, AnsweredCorrectly) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(qui.getDateAccessed()));
            stmt.setInt(2, qui.getQuestionId());
            stmt.setInt(3, qui.getUserId());
            
            if (qui.getAnsweredCorrectly() != null) {
                stmt.setBoolean(4, qui.getAnsweredCorrectly());
            } else {
                stmt.setNull(4, Types.TINYINT);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    qui.setQuiId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ by ID
    public Optional<QuestionUserInformation> getQUIById(int quiId) {
        String sql = "SELECT QUIId, DateAccessed, QuestionId, UserId, AnsweredCorrectly " +
                     "FROM Question_User_Information WHERE QUIId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quiId);
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
    public boolean updateQUI(QuestionUserInformation qui) {
        String sql = "UPDATE Question_User_Information SET DateAccessed = ?, QuestionId = ?, UserId = ?, AnsweredCorrectly = ? " +
                     "WHERE QUIId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(qui.getDateAccessed()));
            stmt.setInt(2, qui.getQuestionId());
            stmt.setInt(3, qui.getUserId());

            if (qui.getAnsweredCorrectly() != null) {
                stmt.setBoolean(4, qui.getAnsweredCorrectly());
            } else {
                stmt.setNull(4, Types.TINYINT);
            }

            stmt.setInt(5, qui.getQuiId());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteQUI(int quiId) {
        String sql = "DELETE FROM Question_User_Information WHERE QUIId = ?";
        try (Connection conn = Connect.connect();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quiId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Map result row -> QuestionUserInformation
    private QuestionUserInformation mapRow(ResultSet rs) throws SQLException {
        QuestionUserInformation qui = new QuestionUserInformation();
        qui.setQuiId(rs.getInt("QUIId"));
        Timestamp ts = rs.getTimestamp("DateAccessed");
        if (ts != null) {
            qui.setDateAccessed(ts.toLocalDateTime());
        }
        qui.setQuestionId(rs.getInt("QuestionId"));
        qui.setUserId(rs.getInt("UserId"));

        Object answeredCorrectly = rs.getObject("AnsweredCorrectly");
        if (answeredCorrectly != null) {
            qui.setAnsweredCorrectly(rs.getBoolean("AnsweredCorrectly"));
        } else {
            qui.setAnsweredCorrectly(null);
        }
        return qui;
    }
}
