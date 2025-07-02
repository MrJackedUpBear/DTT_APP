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
	
	//Possible Need to dos: Fix the JSON formatter to ensure that if a prompt or answer contains "", that we give the correct escape sequence to avoid issues.
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
			json += "\"Prompt\":\"" + question.get("Prompt") + "\"";
			json += ",\"Correct Answer\":\"" + question.get("Correct Answer") + "\"";
			json += ",\"Wrong Answers\":{";
			String wrong = question.get("Wrong Answers");
			
			//Splits the wrong string into a list to get all of the wrong answers
			String[] wrongAnswers = wrong.split(", ");
			
			//Loops through the list and appends it to the JSON String
			for (int j = 0; j < wrongAnswers.length; j++) {
				//Checks for the first elements and appends the first wrong answer to the JSON String and increments j
				if (j == 0) {
					json += "\"" + j + "\":\"" + wrongAnswers[j] + "\"";
					j++;
				}
				
				//Checks if we have are outside of the array after incrementing j. If so, we just add the ending bracket to finish off the wrong answers
				//list. Then checks if we are at the end of the array and appends the last answer and the curly bracket.
				//Otherwise, the JSON String appends a comma space and the next wrong answer.
				if (j == wrongAnswers.length) {
					json += "}";
				}else if (j == wrongAnswers.length - 1){
					json += ",\"" + j + "\":\"" + wrongAnswers[j] + "\"}";
				}else {
					json += ",\"" + j + "\":\"" + wrongAnswers[j] + "\"";
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
}
