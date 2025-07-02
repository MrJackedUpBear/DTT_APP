package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Question {
	private static String user = "mrjackedupbear";
	private static String pass = "TempPass";
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS Question (QuestionId INT PRIMARY KEY AUTO_INCREMENT, Prompt TEXT UNIQUE, CorrectAnswer TEXT);";
	
	private static Question q = new Question();
	
	public static Question getInstance() {
		return q;
	}
	
	private Question() {
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
	
	HashMap<String, String> getQuestion(int id) {
		String sql = "SELECT QuestionId, Prompt, CorrectAnswer FROM Question WHERE QuestionId=?;";
		
		HashMap<String, String> question = new HashMap<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				question.put("Prompt", set.getString("Prompt"));
				question.put("Correct Answer", set.getString("CorrectAnswer"));
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return question;
	}
	
	ArrayList<Integer> getAvailableQuestions() {
		String sql = "SELECT QuestionId FROM Question;";
		
		ArrayList<Integer> availableQuestions = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass)){
			Statement stmt = conn.createStatement();
			
			ResultSet set = stmt.executeQuery(sql);
			
			while (set.next()) {
				availableQuestions.add(set.getInt("QuestionId"));
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return availableQuestions;
	}
}
