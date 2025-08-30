package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TaskList {
	private static String user = System.getenv("DB_USER");
	private static String pass = System.getenv("DB_PASSWORD");
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS TaskList (TaskLetter CHAR(2) NOT NULL PRIMARY KEY, Description TEXT NOT NULL);";
	
	private static TaskList tl = new TaskList();
	
	public static TaskList getInstance() {
		return tl;
	}
	
	private TaskList() {
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass)){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createQuestion)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, user, pass);
    }
	
	String getTask(String tl) {
		String task = "";
		String sql = "SELECT Description FROM TaskList WHERE TaskLetter = ?;";
		
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, tl);
			ResultSet s = pstmt.executeQuery();
			
			while (s.next()) {
				task = s.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: " + e.getMessage());
		}
		
		return task;
	}
}
