package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WrongAnswer {
	private static String user = System.getenv("DB_USER");
	private static String pass = System.getenv("DB_PASSWORD");
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
	
	int getWrongAnswerId(int id, String wrongAnswer) {
		String sql = "SELECT AnswerId FROM WrongAnswer WHERE Answer=? AND QuestionId=?;";
		int wrongAnswerId = -1;
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, wrongAnswer);
			pstmt.setInt(2, id);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				wrongAnswerId = set.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return wrongAnswerId;
	}
	
	ArrayList<Integer> getWrongAnswerId(int id){
		String sql = "SELECT AnswerId FROM WrongAnswer WHERE QuestionId=?;";
		ArrayList<Integer> wrongAnswers = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				wrongAnswers.add(set.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return wrongAnswers;
	}
	
	void addWrongAnswer(int answerId, String wrongAnswer, int questionId) {
		String sql = "INSERT INTO WrongAnswer VALUES (?, ?, ?);";
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, answerId);
			pstmt.setString(2, wrongAnswer);
			pstmt.setInt(3, questionId);
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				System.out.println("Successfully added wrong answer");
			}else {
				System.out.println("Error adding wrong answer");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void deleteWrongAnswer(int answerId, int questionId) {
		String sql = "DELETE FROM WrongAnswer WHERE AnswerId=? AND QuestionId=?;";
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, answerId);
			pstmt.setInt(2, questionId);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully deleted answer");
			}else {
				System.out.println("Error deleting answer");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void updateWrongAnswer(int answerId, String wrongAnswer, int questionId) {
		String sql = "UPDATE WrongAnswer SET Answer=? WHERE AnswerId=? AND QuestionId=?;";
		
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, wrongAnswer);
			pstmt.setInt(2, answerId);
			pstmt.setInt(3, questionId);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated answer.");
			}else {
				System.out.println("Error updating answer.");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
}
