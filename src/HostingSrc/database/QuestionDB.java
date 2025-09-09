package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	
	Question getQuestion(int id) {
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
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(id);
		
		Question question = new Question(prompt, correctAnswer, wrongAnswers);
		
		if (hasImage) {
			ArrayList<Image> images = ImageDB.getInstance().getImages(id);
			
			for (Image image : images) {
				String img = image.getImageLoc().substring(image.getImageLoc().lastIndexOf("\\") + 1);
				question.addImage(img, id, img.substring(img.lastIndexOf(".") + 1));
			}
		}
		
		if (taskLetter != null) {
			question.setTaskLetter(taskLetter);
			String taskLetterDesc = TaskList.getInstance().getTask(taskLetter);
			question.setTaskLetterDesc(taskLetterDesc);
		}
		
		question.setJustification(justification);
		
		
		return question;
	}
	
	ArrayList<Integer> getAvailableQuestions() {
		String sql = "SELECT QuestionId FROM Question;";
		
		ArrayList<Integer> availableQuestions = new ArrayList<>();
		
		try (Connection conn = Connect.connect()){
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
	
	
	void createQuestion(String prompt, String answer, String justification, String taskLetter, boolean hasImage) {
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
			}else {
				System.out.println("Error adding to table");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
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
			
			pstmt.setInt(6, getQuestionId(question.getPrompt()));
			
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
	
	void deleteQuestion(int id) {
		String sql = "DELETE FROM Question WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			int rowsAffected = pstmt.executeUpdate();
			
			if (rowsAffected > 0) {
				System.out.println("Successfully deleted");
			}else {
				System.out.println("Error deleting");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void updateTaskLetter(int id, String taskLetter) {
		String sql = "UPDATE Question SET TaskLetter=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1,  taskLetter);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated justification.");
			}else {
				System.out.println("Error updating justification.");
			}
		}catch (SQLException e) {
			System.out.println("Error establshing connection: " + e.getMessage());
		}
	}
	
	void updateQuestionJustification(int id, String justification) {
		String sql = "UPDATE Question SET Justification=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, justification);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated prompt.");
			}else {
				System.out.println("Error updating prompt.");
			}
		}catch(SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void updateQuestionPrompt(int id, String prompt) {
		String sql = "UPDATE Question SET Prompt=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, prompt);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated prompt.");
			}else {
				System.out.println("Error updating prompt.");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void updateQuestionHasImage(int id, boolean hasImage) {
		String sql = "UPDATE Question SET HasImage=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setBoolean(1, hasImage);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated has image.");
			}else {
				System.out.println("Error updating has image.");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	void updateQuestionAnswer (int id, String answer) {
		String sql = "UPDATE Question SET CorrectAnswer=? WHERE QuestionId=?;";
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, answer);
			pstmt.setInt(2, id);
			
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Successfully updated correct answer.");
			}else {
				System.out.println("Error updating correct answer");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	int getQuestionId(String prompt) {
		String sql = "SELECT QuestionId FROM Question WHERE Prompt=?;";
		int questionId = -1;
		
		try (Connection conn = Connect.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, prompt);
			
			ResultSet set = pstmt.executeQuery();
			
			while (set.next()) {
				questionId = set.getInt("QuestionId");
			}
		}catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
		
		return questionId;
	}
}
