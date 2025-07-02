package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WrongAnswer {
	private static String user = "mrjackedupbear";
	private static String pass = "TempPass";
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS WrongAnswer (AnswerId INT, Answer TEXT, QuestionId INT, PRIMARY KEY (AnswerId, QuestionId));";
	
	private static WrongAnswer w = new WrongAnswer();
	
	public static WrongAnswer getInstance() {
		return w;
	}
	
	private WrongAnswer() {
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
	
	ArrayList<String> getWrongAnswers(int id) {
		String sql = "SELECT Answer FROM WrongAnswer WHERE QuestionId=?;";
		ArrayList<String> wrongAnswers = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				wrongAnswers.add(set.getString(1));
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return wrongAnswers;
	}
}
