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
		//Initializes our questions variable and the possible questions from the database
		ArrayList<HashMap<String, String>> questions = new ArrayList<>();
		ArrayList<Integer> possibleQuestions = Question.getInstance().getAvailableQuestions();
		
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (numQuestions > possibleQuestions.size()) {
			return "";
		}
		
		//Shuffles up the ArrayList to mix up the possible questions
		Collections.shuffle(possibleQuestions);
		
		//Loops through the number of questions requested and adds questions to the questions variable
		for (int i = 0; i < numQuestions; i++) {
			//Initializes a question variable that is obtained by getting questions from the question table with the id provided from the possible questions
			//ArrayList
			HashMap<String, String> question = Question.getInstance().getQuestion(possibleQuestions.get(i));
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			ArrayList<String> temp = WrongAnswer.getInstance().getWrongAnswers(possibleQuestions.get(i));
			String wrongAnswers = "";
			
			//Loops through the number of wrong answer questions and adds them to the wrong answers string
			for (int j = 0; j < temp.size(); j++) {
				//Checks if the wrongAnswers variable is empty and then appends the first wrong answer and increments j
				if (wrongAnswers.equals("")) {
					wrongAnswers += temp.get(j);
					j++;
				}
				
				//Checks to make sure that j is not equal to the size of the ArrayList and adds a comma with a space before the next wrong answer.
				if (j != temp.size()) {
					wrongAnswers += ", " + temp.get(j);
				}
			}
			
			//Adds the wrong answers string to the map with the key 'Wrong Answers'
			question.put("Wrong Answers", wrongAnswers);
			
			//Adds the question to the questions variable.
			questions.add(question);
		}
		
		//The following code converts the ArrayList containing a HashMap into a JSON String. This initializes the JSON String with a bracket to start off
		//the JSON.
		String json = "{";
		
		//This loops through all of the questions and formats the elements into a JSON String.
		for (int i = 0; i < questions.size(); i++) {
			//This adds the question number to the start of the JSON and gets the Prompt and correct answer appended.
			json += "\"" + String.valueOf(i) + "\"";
			json += ":{";
			HashMap<String, String> question = questions.get(i);
			
			String prompt = question.get("Prompt").contains("\"")? escapeQuote(question.get("Prompt")): question.get("Prompt");
			json += "\"Prompt\":\"" + prompt + "\"";
			
			String correctAnswer = question.get("Correct Answer").contains("\"")? escapeQuote(question.get("Correct Answer")): question.get("Correct Answer");
			json += ",\"Correct Answer\":\"" + correctAnswer + "\"";
			
			json += ",\"Wrong Answers\":{";
			String wrong = question.get("Wrong Answers");
			
			//Splits the wrong string into a list to get all of the wrong answers
			String[] wrongAnswers = wrong.split(", ");
			String wrongAnswer = "";
			
			//Loops through the list and appends it to the JSON String
			for (int j = 0; j < wrongAnswers.length; j++) {
				//Checks for the first elements and appends the first wrong answer to the JSON String and increments j
				if (j == 0) {
					wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
					json += "\"" + j + "\":\"" + wrongAnswer + "\"";
					j++;
				}
				
				//Checks if we have are outside of the array after incrementing j. If so, we just add the ending bracket to finish off the wrong answers
				//list. Then checks if we are at the end of the array and appends the last answer and the curly bracket.
				//Otherwise, the JSON String appends a comma space and the next wrong answer.
				if (j == wrongAnswers.length) {
					json += "}";
				}else if (j == wrongAnswers.length - 1){
					wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
					json += ",\"" + j + "\":\"" + wrongAnswer + "\"}";
				}else {
					wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
					json += ",\"" + j + "\":\"" + wrongAnswer + "\"";
				}
			}
			
			//Checks if we have reached the last question. If so, we add the curly bracket to close out the JSON
			//Otherwise, a curly bracket is added with a comma to continue on with the next question.
			if (i == questions.size() - 1) {
				json += "}";
			}else {
				json += "},";
			}
		}
		//Appends the final curly bracket to close out the JSON String
		json += "}";
		
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
			
			String[] wrongAnswersSplit = wrongAnswersString.split(", ");
			
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
	
	public void deleteQuestion(String prompt) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return;
		}
		
		Question.getInstance().deleteQuestion(questionId);
		deleteAllWrongAnswers(questionId);
	}
	
	public void updateQuestionPrompt(String oldPrompt, String prompt) {
		int questionId = Question.getInstance().getQuestionId(oldPrompt);
		
		if (questionId == -1){
			return;
		}
		
		Question.getInstance().updateQuestionPrompt(questionId, prompt);
	}
	
	public void updateQuestionAnswer(String prompt, String answer) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return;
		}
		
		Question.getInstance().updateQuestionAnswer(questionId, answer);
	}
	
	public String getQuestion(String prompt) {
		int questionId = Question.getInstance().getQuestionId(prompt);
		
		HashMap<String, String> question = Question.getInstance().getQuestion(questionId);
		
		if (question == null || question.isEmpty()) {
			return "{}";
		}
		
		return formatQuestionAsJson(question, 0, questionId);
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
	
	public void updateWrongAnswer(int answerId, String wrongAnswer, int questionId) {
		WrongAnswer.getInstance().updateWrongAnswer(answerId, wrongAnswer, questionId);
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
			String tempWrongAnswers = question.split("\",\"Wrong Answers\":\\[\"")[1];
			question = question.split("\",\"Wrong Answers\":\\[\"")[0];
			
			String correctAnswer = question.split("\",\"Correct Answer\":\"")[1];
			question = question.split("\",\"Correct Answer\":\"")[0];
			
			String prompt = question.split("\\{\"Prompt\":\"")[1];
			
			String[] wrongAnswers = tempWrongAnswers.split(",");
			
			String wrongAnswersCSV = "";
			int i = 0;
			for (String wrongAnswer : wrongAnswers) {
				Boolean hasQuote = false;
				
				if (wrongAnswer.contains("\\\"")) {
					wrongAnswer = wrongAnswer.replaceAll("\\\"", "-9919299");
					hasQuote = true;
				}
				
				if (wrongAnswer.contains("]")) {
					wrongAnswer = wrongAnswer.replaceAll("]", "");
				}
				
				wrongAnswer = wrongAnswer.replaceAll("\"", "");
				
				if (hasQuote) {
					wrongAnswer = wrongAnswer.replaceAll("-9919299", "\\\"");
				}
				
				if (i == 0) {
					wrongAnswersCSV += wrongAnswer;
				}else {
					wrongAnswersCSV += ", " + wrongAnswer;
				}
				i++;
			}
			
			questionVal.put("Wrong Answers", wrongAnswersCSV);
			questionVal.put("Prompt", prompt);
			questionVal.put("Correct Answer", correctAnswer);
			
			questions.add(questionVal);
		}
		
		return questions;
	}
	
	private String formatQuestionAsJson(HashMap<String, String> question, int questionNum, int questionId) {
		ArrayList<String> temp = WrongAnswer.getInstance().getWrongAnswers(questionId);
		
		String answers = "";
		for (String wrongAnswer : temp) {
			if (wrongAnswer.equals(temp.get(temp.size() - 1))) {
				answers += wrongAnswer;
			}else {
				answers += wrongAnswer + ", ";
			}
		}
		
		question.put("Wrong Answers", answers);
		String json = "";
		
		//This adds the question number to the start of the JSON and gets the Prompt and correct answer appended.
		json += "\"" + String.valueOf(questionNum) + "\"";
		json += ":{";
		
		String prompt = question.get("Prompt").contains("\"")? escapeQuote(question.get("Prompt")): question.get("Prompt");
		json += "\"Prompt\":\"" + prompt + "\"";
		
		String correctAnswer = question.get("Correct Answer").contains("\"")? escapeQuote(question.get("Correct Answer")): question.get("Correct Answer");
		json += ",\"Correct Answer\":\"" + correctAnswer + "\"";
		
		json += ",\"Wrong Answers\":{";
		String wrong = question.get("Wrong Answers");
		
		//Splits the wrong string into a list to get all of the wrong answers
		String[] wrongAnswers = wrong.split(", ");
		String wrongAnswer = "";
		
		//Loops through the list and appends it to the JSON String
		for (int j = 0; j < wrongAnswers.length; j++) {
			//Checks for the first elements and appends the first wrong answer to the JSON String and increments j
			if (j == 0) {
				wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
				json += "\"" + j + "\":\"" + wrongAnswer + "\"";
				j++;
			}
			
			//Checks if we have are outside of the array after incrementing j. If so, we just add the ending bracket to finish off the wrong answers
			//list. Then checks if we are at the end of the array and appends the last answer and the curly bracket.
			//Otherwise, the JSON String appends a comma space and the next wrong answer.
			if (j == wrongAnswers.length) {
				json += "}";
			}else if (j == wrongAnswers.length - 1){
				wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
				json += ",\"" + j + "\":\"" + wrongAnswer + "\"}";
			}else {
				wrongAnswer = wrongAnswers[j].contains("\"")? escapeQuote(wrongAnswers[j]): wrongAnswers[j];
				json += ",\"" + j + "\":\"" + wrongAnswer + "\"";
			}
		}
		
		//Checks if we have reached the last question. If so, we add the curly bracket to close out the JSON
		//Otherwise, a curly bracket is added with a comma to continue on with the next question.
		json += "}";
		//Appends the final curly bracket to close out the JSON String
		json += "}";
		
		return json;
	}
	
//private ArrayList<String> parseCSV(String csv) {
//	ArrayList<String> list = new ArrayList<>();
//	
//	return list;
//}
}
