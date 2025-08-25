package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import questions.*;

public class Database {
	private static Database db = new Database();
	
	public static Database getInstance() {
		return db;
	}
	
	private Database() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
	}
	
	public String getTotalQuestions() {
		ArrayList<Integer> questions = Question.getInstance().getAvailableQuestions();
		
		int totalQuestions = questions.size();
		
		String json = "{\"Question Total\":\"" + totalQuestions + "\"}";
		return json;
	}
	
	//This function accesses the question and wrong answer table to get complete questions. First it checks how many questions are in the database, then it
	//shuffles the question id's to get a random set of questions to return back as a JSON String
	public String getRandomQuestions(int numQuestions) {
		ArrayList<Integer> possibleQuestions = Question.getInstance().getAvailableQuestions();
		
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (numQuestions > possibleQuestions.size()) {
			return "";
		}
		
		//Shuffles up the ArrayList to mix up the possible questions
		Collections.shuffle(possibleQuestions);
		
		String json = "{\"Questions\":[";
		
		ArrayList<questions.Question> que = new ArrayList<>();
		
		//Loops through the number of questions requested and adds questions to the questions variable
		for (int i = 0; i < numQuestions; i++) {
			//Initializes a question variable that is obtained by getting questions from the question table with the id provided from the possible questions
			//ArrayList
			HashMap<String, String> question = Question.getInstance().getQuestion(possibleQuestions.get(i));
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(possibleQuestions.get(i));
			
			questions.Question q = new questions.Question(question.get("Prompt"), question.get("Correct Answer"), wrongAnswers);
			que.add(q);
		}
		
		json = formatQuestionsAsJson(que);
		
		return json;
	}
	
	public String getQuestionsFrom(int start, int end) {
		String json;
		
		ArrayList<Integer> possibleQuestions = Question.getInstance().getAvailableQuestions();
				
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (end > possibleQuestions.size()) {
			return "";
		}
		
		ArrayList<questions.Question> que = new ArrayList<>();
		for (int i = start; i < end; i++) {
			HashMap<String, String> question = Question.getInstance().getQuestion(possibleQuestions.get(i));
			ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(possibleQuestions.get(i));
			
			questions.Question q = new questions.Question(question.get("Prompt"), question.get("Correct Answer"), wrongAnswers);
			que.add(q);
		}
		
		json = formatQuestionsAsJson(que);
		
		return json;
	}
	
	public Boolean addQuestions(String questionsJson) {
		ArrayList<questions.Question> allQuestions = getQuestionsFromJson(questionsJson);
		String prompt = "";
		String correctAnswer = "";
		ArrayList<String> wrongAnswers;
		
		for (questions.Question question : allQuestions) {
			prompt = question.getPrompt();;
			correctAnswer = question.getCorrectAnswer();;
			wrongAnswers = question.getWrongAnswers();
			
			if (!prompt.isEmpty() && !correctAnswer.isEmpty() && !wrongAnswers.isEmpty()) {
				addQuestion(prompt, correctAnswer, wrongAnswers);
			}
		}
		return true;
	}
	
	public void addQuestion(String prompt, String correctAnswer, ArrayList<String> wrongAnswers) {
		Question.getInstance().createQuestion(prompt, correctAnswer);
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return;
		}
		
		addWrongAnswers(wrongAnswers, questionId);
	}
	
	public Boolean deleteQuestion(String prompt) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		Question.getInstance().deleteQuestion(questionId);
		deleteAllWrongAnswers(questionId);
		
		return true;
	}
	
	public Boolean updateQuestionPrompt(String oldPrompt, String prompt) {
		int questionId = Question.getInstance().getQuestionId(oldPrompt);
		
		if (questionId == -1){
			return false;
		}
		
		Question.getInstance().updateQuestionPrompt(questionId, prompt);
		
		return true;
	}
	
	public Boolean updateQuestionAnswer(String prompt, String answer) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		Question.getInstance().updateQuestionAnswer(questionId, answer);
		
		return true;
	}
	
	public String getQuestion(String prompt) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		HashMap<String, String> question = Question.getInstance().getQuestion(questionId);
		ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(questionId);
		if (question == null || question.isEmpty()) {
			return "{}";
		}
		
		questions.Question q = new questions.Question(question.get("Prompt"), question.get("Correct Answer"), wrongAnswers);
		
		ArrayList<questions.Question>que = new ArrayList<>();
		que.add(q);
		
		return formatQuestionsAsJson(que);
	}
	
	public void addWrongAnswers(ArrayList<String> wrongAnswers, int questionId) {
		int val = 0;
		for (String wrongAnswer : wrongAnswers) {
			WrongAnswer.getInstance().addWrongAnswer(val, wrongAnswer, questionId);
			val++;
		}
	}
	
	public void addWrongAnswer(String answer, int questionId) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId);
		
		int maxVal = 0;
		
		for (int wrongAnswerId : wrongAnswerIds) {
			if (wrongAnswerId > maxVal && wrongAnswerId - maxVal == 1) {
				maxVal = wrongAnswerId;
			}else {
				maxVal++;
				break;
			}
		}
		
		WrongAnswer.getInstance().addWrongAnswer(maxVal, answer, questionId);
	}
	
	public void deleteWrongAnswer(int answerId, int questionId) {
		WrongAnswer.getInstance().deleteWrongAnswer(answerId, questionId);
	}
	
	public void deleteAllWrongAnswers(int questionId) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId);
		
		for (int wrongAnswerId: wrongAnswerIds) {
			deleteWrongAnswer(wrongAnswerId, questionId);
		}
	}
	
	public Boolean updateWrongAnswer(String prompt, String wrongAnswer, int answerId) {
		int questionId = -1;
		
		questionId = Question.getInstance().getQuestionId(prompt);
		
		if (answerId == -1) {
			return false;
		}
		
		
		System.out.println(answerId);
		WrongAnswer.getInstance().updateWrongAnswer(answerId, wrongAnswer, questionId);
		
		return true;
	}
	
	public String escapeQuote(String input) {
		return input.replace("\"", "\\\"");
	}
	
	private ArrayList<questions.Question> getQuestionsFromJson(String questionsJson){
		ArrayList<questions.Question> allQuestions = new ArrayList<>(); 
		//ArrayList<HashMap<String, String>> questions = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode jsonNodeRoot = objectMapper.readTree(questionsJson);
			for (int i = 0; i < jsonNodeRoot.size(); i++) {
				String prompt = jsonNodeRoot.get(i).get("Prompt").asText();
				String correctAnswer = jsonNodeRoot.get(i).get("Correct Answer").asText();
				
				ArrayList<String> wrongAnswers = new ArrayList<>();
				
				for (JsonNode element : jsonNodeRoot.get(i).get("Wrong Answers")) {
					wrongAnswers.add(element.asText());
				}
				
				questions.Question q = new questions.Question(prompt, correctAnswer, wrongAnswers);
				
				allQuestions.add(q);
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return allQuestions;
	}
	
	public String formatQuestionsAsJson(ArrayList<questions.Question>question) {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		try {
			HashMap<String, ArrayList<questions.Question>> finalBoss = new HashMap<>();
			finalBoss.put("Questions", question);
			json = objectMapper.writeValueAsString(finalBoss);
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return json;
	}
	
//private ArrayList<String> parseCSV(String csv) {
//	ArrayList<String> list = new ArrayList<>();
//	
//	return list;
//}
}
