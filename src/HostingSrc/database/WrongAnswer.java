package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import logging.LogInfo;

public class WrongAnswer {
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS WrongAnswer (AnswerId INT, Answer TEXT, QuestionId INT, PRIMARY KEY (AnswerId, QuestionId));";
	
	private static WrongAnswer w = new WrongAnswer();
	
	public static WrongAnswer getInstance() {
		return w;
	}
	
	private WrongAnswer() {
		try (Connection conn = Connect.connect()){
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
	
	ArrayList<String> getWrongAnswers(int id, LogInfo logInfo) {
		String sql = "SELECT Answer FROM WrongAnswer WHERE QuestionId=?;";
		ArrayList<String> wrongAnswers = new ArrayList<>();
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				wrongAnswers.add(set.getString(1));
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got wrong answer: " + wrongAnswers.getLast());
				logInfo.addLog(logInfo);
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting wrong answers: " + e.getStackTrace());
			logInfo.addLog(logInfo);
		}
		
		return wrongAnswers;
	}
	
	int getWrongAnswerId(int id, String wrongAnswer) {
		String sql = "SELECT AnswerId FROM WrongAnswer WHERE Answer=? AND QuestionId=?;";
		int wrongAnswerId = -1;
		
		try (Connection conn = Connect.connect();
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
	
	ArrayList<Integer> getWrongAnswerId(int id, LogInfo logInfo){
		String sql = "SELECT AnswerId FROM WrongAnswer WHERE QuestionId=?;";
		ArrayList<Integer> wrongAnswers = new ArrayList<>();
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				wrongAnswers.add(set.getInt(1));
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got wrong answer: " + wrongAnswers.getLast());
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting wrong answers: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return wrongAnswers;
	}
	
	void addWrongAnswer(int answerId, String wrongAnswer, int questionId, LogInfo logInfo) {
		String sql = "INSERT INTO WrongAnswer VALUES (?, ?, ?);";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, answerId);
			pstmt.setString(2, wrongAnswer);
			pstmt.setInt(3, questionId);
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				System.out.println("Successfully added wrong answer");
				logInfo.setLogInfo("Successfully added wrong answer: " + wrongAnswer);
			}else {
				System.out.println("Error adding wrong answer");
				logInfo.setLogInfo("Error adding wrong answer: " + wrongAnswer);
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error adding wrong answer: " + wrongAnswer + "\n Error: " + String.valueOf(e.getStackTrace()));
            logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	void deleteWrongAnswer(int answerId, int questionId, LogInfo logInfo) {
		String sql = "DELETE FROM WrongAnswer WHERE AnswerId=? AND QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, answerId);
			pstmt.setInt(2, questionId);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully deleted answer");
				logInfo.setLogInfo("Successfully deleted answer: " + answerId);
			}else {
				System.out.println("Error deleting answer");
				logInfo.setLogInfo("Error deleting answer: " + answerId);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error deleting answer: " + e.getStackTrace());
			logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateWrongAnswer(int answerId, String wrongAnswer, int questionId, LogInfo logInfo) {
		String sql = "UPDATE WrongAnswer SET Answer=? WHERE AnswerId=? AND QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, wrongAnswer);
			pstmt.setInt(2, answerId);
			pstmt.setInt(3, questionId);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated answer.");
				logInfo.setLogInfo("Successfully updated answer: " + wrongAnswer);
	            logInfo.setLevel("Info");
			}else {
				System.out.println("Error updating answer.");
				logInfo.setLogInfo("Error updating answer: " + wrongAnswer);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error updating answer: " + e.getStackTrace());
			logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
}
