package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import logging.LogInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import questions.Question;

public class QuestionDB {
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS Question (QuestionId INT NOT NULL AUTO_INCREMENT, Prompt TEXT UNIQUE, CorrectAnswer TEXT, Justification TEXT, TaskLetter CHAR(4), HasImage TINYINT(1) NOT NULL, PRIMARY KEY(QuestionId));";
	
	private static QuestionDB q = new QuestionDB();
	
	public static QuestionDB getInstance() {
		return q;
	}
	
	private QuestionDB() {
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
	
	Question getQuestion(int id, LogInfo logInfo) {
		String sql = "SELECT Prompt, CorrectAnswer, Justification, TaskLetter, HasImage FROM Question WHERE QuestionId=?;";
		
		String prompt = "";
		String correctAnswer = "";
		String justification = "";
		String taskLetter = "";
		boolean hasImage = false;
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				prompt = set.getString("Prompt");
				correctAnswer = set.getString("CorrectAnswer");
				justification = set.getString("Justification");
				taskLetter = set.getString("TaskLetter");
				hasImage = set.getBoolean("HasImage");
			}
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully got question.");
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting question: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(id, logInfo);
		
		Question question = new Question(prompt, correctAnswer, wrongAnswers);
		
		if (hasImage) {
			ArrayList<Image> images = ImageDB.getInstance().getImages(id, logInfo);
			
			for (Image image : images) {
				String img = image.getImageLoc().substring(image.getImageLoc().lastIndexOf("\\") + 1);
				question.addImage(img, id, img.substring(img.lastIndexOf(".") + 1));
			}
		}
		
		if (taskLetter != null) {
			question.setTaskLetter(taskLetter);
			String taskLetterDesc = TaskList.getInstance().getTask(taskLetter, logInfo);
			question.setTaskLetterDesc(taskLetterDesc);
		}
		
		question.setJustification(justification);
		
		
		return question;
	}
	
	ArrayList<Integer> getAvailableQuestions(LogInfo logInfo) {
		String sql = "SELECT QuestionId FROM Question;";
		
		ArrayList<Integer> availableQuestions = new ArrayList<>();
		
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			
			ResultSet set = stmt.executeQuery(sql);
			
			while (set.next()) {
				availableQuestions.add(set.getInt("QuestionId"));
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully got question: " + availableQuestions.getLast());
				logInfo.addLog(logInfo);
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting questions: " + e.getStackTrace());
			logInfo.addLog(logInfo);
		}
		
		return availableQuestions;
	}
	
	
	void createQuestion(String prompt, String answer, String justification, String taskLetter, boolean hasImage, LogInfo logInfo) {
		String sql = "INSERT INTO Question (Prompt, CorrectAnswer, Justification, TaskLetter, HasImage) VALUES (?, ?, ?, ?, ?);";
				
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, prompt);
			pstmt.setString(2, answer);
			pstmt.setString(3, justification);
			pstmt.setString(4, taskLetter);
			pstmt.setBoolean(5, hasImage);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully added.");
				logInfo.setLogInfo("Successfully added question to table with prompt: " + prompt);
	            logInfo.setLevel("Info");
			}else {
				System.out.println("Error adding to table");
				logInfo.setLogInfo("Error adding question to table with prompt: " + prompt);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo(String.valueOf("Error adding question to table: " + e.getStackTrace()));
            logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateQuestion(questions.Question question) {
		String sql = "UPDATE Question SET Prompt=?, CorrectAnswer=?, Justification=?, TaskLetter=?, HasImage=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1,  question.getPrompt());
			pstmt.setString(2, question.getCorrectAnswer());
			pstmt.setString(3, question.getJustification());
			pstmt.setString(4, question.getTaskLetter());
			pstmt.setBoolean(5, question.getHasImage());
			
			pstmt.setInt(6, getQuestionId(question.getPrompt(), new LogInfo()));
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated question.");
			}else {
				System.out.println("Error updating question.");
			}
		}catch (SQLException e) {
			System.out.println("Error establshing connection: " + e.getMessage());
		}
	}
	
	void deleteQuestion(int id, LogInfo logInfo) {
		String sql = "DELETE FROM Question WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				System.out.println("Successfully deleted");
				logInfo.setLogInfo("Successfully deleted question: " + id);
	            logInfo.setLevel("Info");
			}else {
				System.out.println("Error deleting");
				logInfo.setLogInfo("Error deleting question: " + id);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error deleting question: " + e.getStackTrace());
			logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateTaskLetter(int id, String taskLetter, LogInfo logInfo) {
		String sql = "UPDATE Question SET TaskLetter=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1,  taskLetter);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated task letter.");
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully updated task letter: " + taskLetter);
			}else {
				System.out.println("Error updating task letter.");
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Unknown error updating task letter.");
			}
		}catch (SQLException e) {
			System.out.println("Error establshing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error updating task letter: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateQuestionJustification(int id, String justification, LogInfo logInfo) {
		String sql = "UPDATE Question SET Justification=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, justification);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated prompt.");
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully updated justification.");
			}else {
				System.out.println("Error updating prompt.");
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Unknown error updating justification");
			}
		}catch(SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error updating justification: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateQuestionPrompt(int id, String prompt, LogInfo logInfo) {
		String sql = "UPDATE Question SET Prompt=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, prompt);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated prompt.");
				logInfo.setLogInfo("Successfully updated prompt to: " + prompt);
	            logInfo.setLevel("Info");
			}else {
				System.out.println("Error updating prompt.");
				logInfo.setLogInfo("Error updating prompt to : " + prompt);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error updating prompt: " + e.getStackTrace());
			logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateQuestionHasImage(int id, boolean hasImage, LogInfo logInfo) {
		String sql = "UPDATE Question SET HasImage=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setBoolean(1, hasImage);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated has image.");
				logInfo.setLevel("Info");
				logInfo.setLogInfo("Successfully updated has image.");
			}else {
				System.out.println("Error updating has image.");
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Unknown error updating has image.");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error updating has image: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
	}
	
	void updateQuestionAnswer (int id, String answer, LogInfo logInfo) {
		String sql = "UPDATE Question SET CorrectAnswer=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, answer);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated correct answer.");
				logInfo.setLogInfo("Successfully updated correct answer.");
	            logInfo.setLevel("Info");
			}else {
				System.out.println("Error updating correct answer to: " + answer);
				logInfo.setLogInfo("Error updating correct answer to: " + answer);
				logInfo.setLevel("Error");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLogInfo("Error updating correct answer: " + e.getStackTrace());
			logInfo.setLevel("Error");
		}
		
		logInfo.addLog(logInfo);
	}
	
	int getQuestionId(String prompt, LogInfo logInfo) {
		String sql = "SELECT QuestionId FROM Question WHERE Prompt=?;";
		int questionId = -1;
		
		prompt = prompt.replaceAll("\\\\\"", "\"");
		
		System.out.println(prompt);
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, prompt);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				questionId = set.getInt("QuestionId");
				logInfo.setLogInfo("Successfully got question id: " + questionId);
				logInfo.setLevel("Info");
			}
			
			if (questionId == -1) {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Could not find question id for: " + prompt);
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting question id: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		return questionId;
	}
}
