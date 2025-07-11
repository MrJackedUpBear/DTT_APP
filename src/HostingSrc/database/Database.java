package database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
		
		//Loops through the number of questions requested and adds questions to the questions variable
		for (int i = 0; i < numQuestions; i++) {
			//Initializes a question variable that is obtained by getting questions from the question table with the id provided from the possible questions
			//ArrayList
			HashMap<String, String> question = Question.getInstance().getQuestion(possibleQuestions.get(i));
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(possibleQuestions.get(i));
			
			if (i != 0){
				json += ", ";
			}
			
			json += formatQuestionAsJson(question, wrongAnswers);
		}
		
		json += "]}";
		
		return json;
	}
	
	public String getQuestionsFrom(int start, int end) {
		String json = "{\"Questions\":[";
		
		ArrayList<Integer> possibleQuestions = Question.getInstance().getAvailableQuestions();
				
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (end > possibleQuestions.size()) {
			return "";
		}
		
		for (int i = start; i < end; i++) {
			//Initializes a question variable that is obtained by getting questions from the question table with the id provided from the possible questions
			//ArrayList
			HashMap<String, String> question = Question.getInstance().getQuestion(possibleQuestions.get(i));
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			ArrayList<String> wrongAnswers = WrongAnswer.getInstance().getWrongAnswers(possibleQuestions.get(i));
			
			if (i != start){
				json += ", ";
			}
			
			json += formatQuestionAsJson(question, wrongAnswers);
		}
		
		json += "]}";
		
		return json;
	}
	
	public Boolean addQuestions(String questionsJson) {
		ArrayList<HashMap<String, String>> questions = getQuestionsFromJson(questionsJson);
		String prompt = "";
		String correctAnswer = "";
		ArrayList<String> wrongAnswers = new ArrayList<>();
		
		for (HashMap<String, String> question : questions) {
			prompt = question.get("Prompt");
			correctAnswer = question.get("Correct Answer");
			String wrongAnswersString = question.get("Wrong Answers");
			
			String[] wrongAnswersSplit = wrongAnswersString.split("\", ");
			
			for (String wrongAnswer : wrongAnswersSplit) {
				wrongAnswers.add(wrongAnswer);
			}
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
		
		return formatQuestionAsJson(question, wrongAnswers);
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
	
	private String escapeQuote(String input) {
		return input.replace("\"", "\\\"");
	}
	
	private ArrayList<HashMap<String, String>> getQuestionsFromJson(String questionsJson){
		ArrayList<HashMap<String, String>> questions = new ArrayList<>();
		System.out.println(questionsJson);
		
		String[] questionInfo = questionsJson.split("}},");
		
		for (String question : questionInfo) {
			HashMap<String, String> questionVal = new HashMap<>();
			String tempWrongAnswers = question.split("\",\"Wrong Answers\":\\[")[1];
			question = question.split("\",\"Wrong Answers\":\\[\"")[0];
			
			String correctAnswer = question.split("\",\"Correct Answer\":\"")[1];
			question = question.split("\",\"Correct Answer\":\"")[0];
			
			String prompt = question.split("\\{\"Prompt\":\"")[1];
			
			String[] wrongAnswers = tempWrongAnswers.split(",\"");
			
			String wrongAnswersCSV = "";
			int i = 0;
			for (String wrongAnswer : wrongAnswers) {
				Boolean hasQuote = false;
				System.out.println(wrongAnswer);
				
				if (wrongAnswer.contains("\\\"")) {
					wrongAnswer = wrongAnswer.replaceAll("\\\"", "-9919299");
					hasQuote = true;
				}
				
				if (wrongAnswer.contains("]")) {
					wrongAnswer = wrongAnswer.replaceAll("]", "");
				}
				
				if (wrongAnswer.contains("}")) {
					wrongAnswer = wrongAnswer.replaceAll("}", "");
				}
				
				wrongAnswer = wrongAnswer.replaceAll("\"", "");
				
				if (hasQuote) {
					wrongAnswer = wrongAnswer.replaceAll("-9919299", "\\\"");
				}
				
				if (i == 0) {
					wrongAnswersCSV += wrongAnswer;
				}else {
					wrongAnswersCSV += "\", " + wrongAnswer;
				}
				i++;
			}
			
			System.out.println(wrongAnswersCSV);
			questionVal.put("Wrong Answers", wrongAnswersCSV);
			questionVal.put("Prompt", prompt);
			questionVal.put("Correct Answer", correctAnswer);
			
			questions.add(questionVal);
		}
		
		return questions;
	}
	
	private String formatQuestionAsJson(HashMap<String, String> question, ArrayList<String> wrongAnswers) {
		String json = "";
		String prompt = (question.get("Prompt").contains("\""))? escapeQuote(question.get("Prompt")) : question.get("Prompt");
		String correctAnswer = (question.get("Correct Answer").contains("\""))? escapeQuote(question.get("Correct Answer")) : question.get("Correct Answer");
		
		json += "{";
		json += "\"Prompt\":\"" + prompt + "\",";
		json += "\"Correct Answer\":\"" + correctAnswer + "\",";
		json += "\"Wrong Answers\":[";
		
		for (String wrongAnswer : wrongAnswers) {
			wrongAnswer= (wrongAnswer.contains("\""))? escapeQuote(wrongAnswer) : wrongAnswer;
			
			if (wrongAnswer.equals(wrongAnswers.getFirst())){
				json += "\"" + wrongAnswer + "\"";
			}else {
				json += ", \"" + wrongAnswer + "\"";
			}
		}
		
		json += "]}";
		
		return json;
	}
	
//private ArrayList<String> parseCSV(String csv) {
//	ArrayList<String> list = new ArrayList<>();
//	
//	return list;
//}
}
