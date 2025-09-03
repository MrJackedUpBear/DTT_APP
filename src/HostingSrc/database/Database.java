package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		ArrayList<Integer> questions = QuestionDB.getInstance().getAvailableQuestions();
		
		int totalQuestions = questions.size();
		
		String json = "{\"Question Total\":\"" + totalQuestions + "\"}";
		return json;
	}
	
	public Image getImage(String imageLoc) {
		Image img = ImageDB.getInstance().getImage(imageLoc);
		
		return img;
	}
	
	//This function accesses the question and wrong answer table to get complete questions. First it checks how many questions are in the database, then it
	//shuffles the question id's to get a random set of questions to return back as a JSON String
	public String getRandomQuestions(int numQuestions) {
		ArrayList<Integer> possibleQuestions = QuestionDB.getInstance().getAvailableQuestions();
		
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
			questions.Question question = QuestionDB.getInstance().getQuestion(possibleQuestions.get(i));
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			que.add(question);
		}
		
		json = formatQuestionsAsJson(que);
		
		return json;
	}
	
	public String getQuestionsFrom(int start, int end) {
		String json;
		
		ArrayList<Integer> possibleQuestions = QuestionDB.getInstance().getAvailableQuestions();
				
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (end > possibleQuestions.size()) {
			return "";
		}
		
		ArrayList<questions.Question> que = new ArrayList<>();
		for (int i = start; i < end; i++) {
			questions.Question question = QuestionDB.getInstance().getQuestion(possibleQuestions.get(i));
			
			que.add(question);
		}
		
		json = formatQuestionsAsJson(que);
		
		return json;
	}
	
	public Boolean addQuestions(String questionsJson, String fileUpload) {
		ArrayList<questions.Question> allQuestions = getQuestionsFromJson(questionsJson);
		String prompt = "";
		String correctAnswer = "";
		ArrayList<String> wrongAnswers;
		String justification = "";
		String taskLetter = "";
		boolean hasImage = false;
		
		int i = 0;
		for (questions.Question question : allQuestions) {
			prompt = question.getPrompt();
			correctAnswer = question.getCorrectAnswer();;
			wrongAnswers = question.getWrongAnswers();
			justification = question.getJustification();
			taskLetter = question.getTaskLetter();
			hasImage = question.getHasImage();
			
			if (!prompt.isEmpty() && !correctAnswer.isEmpty() && !wrongAnswers.isEmpty() && QuestionDB.getInstance().getQuestionId(prompt) == -1) {
				if (hasImage) {
					addQuestion(prompt, correctAnswer, wrongAnswers, justification, taskLetter, hasImage, fileUpload, question.getImages(i));
				}else {
					addQuestion(prompt, correctAnswer, wrongAnswers, justification, taskLetter, hasImage, fileUpload, new ArrayList<Image>());
				}
			}
			i++;
		}
		return true;
	}
	
	public void addQuestion(String prompt, String correctAnswer, ArrayList<String> wrongAnswers, String justification, String taskLetter, boolean hasImage, String fileUpload, ArrayList<Image> images) {
		QuestionDB.getInstance().createQuestion(prompt, correctAnswer, justification, taskLetter, hasImage);
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return;
		}
		
		addWrongAnswers(wrongAnswers, questionId);
		
		if (hasImage) {
			for (Image img : images) {
				String imageType = img.getImageType();
				String imageData = img.getImageLoc();
				img.setImageLoc("temp");
				img.setQuestionId(questionId);
				ImageDB.getInstance().addImage(img);
				img = ImageDB.getInstance().getImage(questionId, "temp");
				img.setImageType(imageType);
				fileUpload += String.valueOf("ImageID - " + img.getImageId()) + ", QuestionID - " + String.valueOf(img.getQuestionId()) + "." + img.getImageType();
								
				try {
					byte[] decodedBytes = Base64.getDecoder().decode(imageData);
					
					Files.write(Paths.get(fileUpload), decodedBytes);
					
					System.out.println("Image file created successfully at: " + fileUpload);
					img.setImageLoc(fileUpload);
					ImageDB.getInstance().updateImage(img);
				}catch(IOException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		}
	}
	
	public Boolean deleteQuestion(String prompt, String filePath) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		questions.Question q = QuestionDB.getInstance().getQuestion(questionId);
		
		if (q.getHasImage()) {
			try {
				ArrayList<Image> images = q.getImages();
				for (Image img : images) {
					Files.delete(Paths.get(filePath + img.getImageLoc()));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ImageDB.getInstance().deleteImages(questionId);
		}
		
		QuestionDB.getInstance().deleteQuestion(questionId);
		deleteAllWrongAnswers(questionId);
		
		return true;
	}
	
	public Boolean updateQuestionPrompt(String oldPrompt, String prompt) {
		int questionId = QuestionDB.getInstance().getQuestionId(oldPrompt);
		
		if (questionId == -1){
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionPrompt(questionId, prompt);
		
		return true;
	}
	
	public Boolean updateQuestionTaskLetter(String prompt, String taskLetter) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		QuestionDB.getInstance().updateTaskLetter(questionId, taskLetter);
		
		return true;
	}
	
	public Boolean updateQuestionJustification(String prompt, String justification) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionJustification(questionId, justification);
		
		return true;
	}
	
	public Boolean updateQuestionAnswer(String prompt, String answer) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionAnswer(questionId, answer);
		
		return true;
	}
	
	public String getQuestion(String prompt) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		questions.Question question = QuestionDB.getInstance().getQuestion(questionId);
		if (question == null) {
			return "{}";
		}
				
		ArrayList<questions.Question>que = new ArrayList<>();
		que.add(question);
		
		return formatQuestionsAsJson(que);
	}
	
	public void addWrongAnswers(ArrayList<String> wrongAnswers, int questionId) {
		int val = 0;
		for (String wrongAnswer : wrongAnswers) {
			WrongAnswer.getInstance().addWrongAnswer(val, wrongAnswer, questionId);
			val++;
		}
	}
	
	public Boolean addWrongAnswer(String answer, String prompt) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		addWrongAnswer(answer, questionId);
		
		return true;
	}
	
	public void addImage(String imageJson, String fileUpload) {
		Image image = getImageFromJson(imageJson);
		
		int questionId = image.getQuestionId();
		String imageData = image.getImageLoc();
		String imageType = image.getImageType();
		
		image.setImageLoc("temp");
		
		ImageDB.getInstance().addImage(image);
		
		image = ImageDB.getInstance().getImage(questionId, "temp");
		
		image.setImageType(imageType);
		fileUpload += String.valueOf("ImageID - " + image.getImageId()) + ", QuestionID - " + String.valueOf(image.getQuestionId()) + "." + image.getImageType();
						
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(imageData);
			
			Files.write(Paths.get(fileUpload), decodedBytes);
			
			System.out.println("Image file created successfully at: " + fileUpload);
			image.setImageLoc(fileUpload);
			ImageDB.getInstance().updateImage(image);
			if (!QuestionDB.getInstance().getQuestion(questionId).getHasImage()) {
				QuestionDB.getInstance().updateQuestionHasImage(questionId, true);
			}
		}catch(IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void addWrongAnswer(String answer, int questionId) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId);
		
		int maxVal = 0;
		
		for (int wrongAnswerId : wrongAnswerIds) {
			if (wrongAnswerId >= maxVal) {
				maxVal = wrongAnswerId + 1;
			}
		}
		
		WrongAnswer.getInstance().addWrongAnswer(maxVal, answer, questionId);
	}
	
	public Boolean deleteImage(Image image, String imageType) {
		int questionId = image.getQuestionId();
		
		if (questionId == -1) {
			return false;
		}
		
		questions.Question question = QuestionDB.getInstance().getQuestion(questionId);
		
		question.deleteImage(image);
		
		if (!question.getHasImage()) {
			QuestionDB.getInstance().updateQuestionHasImage(questionId, question.getHasImage());
		}
		
		ImageDB.getInstance().deleteImage(image.getImageId());
		
		Path filePath = Paths.get(image.getImageLoc());
		
		try {
			Files.delete(filePath);
			System.out.println("Deleted image.");
		}catch (IOException e) {
			System.out.println("Error deleting image: " + e.getMessage());
		}
		
		return true;
	}
	
	public Boolean deleteWrongAnswer(int wrongAnswerId, String prompt) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		WrongAnswer.getInstance().deleteWrongAnswer(wrongAnswerId, questionId);
		
		return true;
	}
	
	public Boolean deleteWrongAnswer(String wrongAnswer, String prompt) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (questionId == -1) {
			return false;
		}
		
		int wrongAnswerId = WrongAnswer.getInstance().getWrongAnswerId(questionId, wrongAnswer);
		
		WrongAnswer.getInstance().deleteWrongAnswer(wrongAnswerId, questionId);
		
		return true;
	}
	
	public void deleteAllWrongAnswers(int questionId) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId);
		
		questions.Question q = QuestionDB.getInstance().getQuestion(questionId);
		String prompt = q.getPrompt();
		
		for (int wrongAnswerId: wrongAnswerIds) {
			deleteWrongAnswer(wrongAnswerId, prompt);
		}
	}
	
	public Boolean updateWrongAnswer(String prompt, String wrongAnswer, int answerId) {
		int questionId = -1;
		
		questionId = QuestionDB.getInstance().getQuestionId(prompt);
		
		if (answerId == -1) {
			return false;
		}
		
		
		System.out.println(answerId);
		WrongAnswer.getInstance().updateWrongAnswer(answerId, wrongAnswer, questionId);
		
		return true;
	}
	
	public String getTaskDesc(String taskLetter) {
		return TaskList.getInstance().getTask(taskLetter);
	}
	
	public String escapeQuote(String input) {
		return input.replace("\"", "\\\"");
	}
	
	private ArrayList<questions.Question> getQuestionsFromJson(String questionsJson){
		ArrayList<questions.Question> allQuestions = new ArrayList<>(); 
		//ArrayList<HashMap<String, String>> questions = new ArrayList<>();
		
		questionsJson = questionsJson.substring("{Questions: ".length() + 1, questionsJson.lastIndexOf('}'));
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			allQuestions = objectMapper.readValue(questionsJson, new TypeReference<ArrayList<questions.Question>>() {}); 
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return allQuestions;
	}
	
	private Image getImageFromJson(String imageJson){
		Image image = new Image();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			image = objectMapper.readValue(imageJson, Image.class);
		}catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		return image;
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
