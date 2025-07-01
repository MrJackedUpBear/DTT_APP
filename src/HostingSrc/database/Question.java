package database;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;

public class Question {
	private static String user = "";
	private static String pass = "";
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS Question (QuestionId INT PRIMARY KEY AUTO_INCREMENT, Prompt TEXT UNIQUE, CorrectAnswer TEXT);";
	
	private Question q = new Question();
	
	public Question getInstance() {
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass)){
			
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
		
		return q;
	}
	
	private Question() {
		
	}
}
