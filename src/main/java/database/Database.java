package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import logging.LogInfo;

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
	
	public String getTotalQuestions(LogInfo logInfo) {
		ArrayList<Integer> questions = QuestionDB.getInstance().getAvailableQuestions(logInfo);
		
		int totalQuestions = questions.size();
		
		String json = "{\"Question Total\":\"" + totalQuestions + "\"}";
		return json;
	}
	
	public Image getImage(String imageLoc, LogInfo logInfo) {
		Image img = ImageDB.getInstance().getImage(imageLoc, logInfo);
		
		return img;
	}
	
	public String getUserInfo(String username, LogInfo logInfo) {
		User user = getUser(username, logInfo);
		
		if (user == null) {
			return "";
		}
		
		user.setPassword(null);
		user.setSalt(null);
		
		return formatClassAsJson(user);
	}
	
	public User getUser(String username, LogInfo logInfo) {
		Optional<User> user = UserDB.getInstance().getUserByEmail(username, logInfo);
		
		if (!user.isPresent()) {
			return null;
		}
		
		ArrayList<Integer> roles = UserRolesDB.getInstance().getRolesForUser(user.get().getUserId(), logInfo);
		
		Permission permissions = new Permission();
		
		for (int role : roles) {
			ArrayList<Integer> p = RolePermissionsDB.getInstance().getPermissionsForRole(role, logInfo);
			
			for (int permId : p) {
				permissions = PermissionDB.getInstance().getPermission(permId, permissions, logInfo);
			}
		}
		
		user.get().setPermissions(permissions);
		
		return user.get();
	}
	
	public String getSettings(int settingId) {
		Optional<Setting> settings = SettingDB.getInstance().getSettingById(settingId);
		
		if (!settings.isPresent()) {
			return "";
		}
		
		return formatClassAsJson(settings.get());
	}
	
	//This function accesses the question and wrong answer table to get complete questions. First it checks how many questions are in the database, then it
	//shuffles the question id's to get a random set of questions to return back as a JSON String
	public String getRandomQuestions(int numQuestions, LogInfo logInfo) {
		ArrayList<Integer> possibleQuestions = QuestionDB.getInstance().getAvailableQuestions(logInfo);
		
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			logInfo.setLevel("Info");
			logInfo.setLogInfo("No questions available.");
			logInfo.addLog(logInfo);
			return "";
		} else if (numQuestions > possibleQuestions.size()) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Number of questions requested greater than available.");
			logInfo.addLog(logInfo);
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
			questions.Question question = QuestionDB.getInstance().getQuestion(possibleQuestions.get(i), logInfo);
			//Creates a temporary variable that will hold an ArrayList of the wrong answers; this also initializes the wrong answers string to put in the question
			//HashMap.
			que.add(question);
		}
		
		json = formatQuestionsAsJson(que, logInfo);
		
		return json;
	}
	
	public String getQuestionsFrom(int start, int end, LogInfo logInfo) {
		String json;
		
		ArrayList<Integer> possibleQuestions = QuestionDB.getInstance().getAvailableQuestions(logInfo);
				
		//If there are no possible questions, return.
		if (possibleQuestions.size() == 0) {
			return "";
		} else if (end > possibleQuestions.size()) {
			return "";
		}
		
		ArrayList<questions.Question> que = new ArrayList<>();
		for (int i = start; i < end; i++) {
			questions.Question question = QuestionDB.getInstance().getQuestion(possibleQuestions.get(i), logInfo);
			
			que.add(question);
		}
				
		json = formatQuestionsAsJson(que, logInfo);
		
		return json;
	}
	
	public Boolean addQuestions(String questionsJson, String fileUpload, LogInfo logInfo) {
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
			
			if (!prompt.isEmpty() && !correctAnswer.isEmpty() && !wrongAnswers.isEmpty() && QuestionDB.getInstance().getQuestionId(prompt, logInfo) == -1) {
				if (hasImage) {
					addQuestion(prompt, correctAnswer, wrongAnswers, justification, taskLetter, hasImage, fileUpload, question.getImages(i), logInfo);
				}else {
					addQuestion(prompt, correctAnswer, wrongAnswers, justification, taskLetter, hasImage, fileUpload, new ArrayList<Image>(), logInfo);
				}
			}
			i++;
		}
		return true;
	}
	
	public void addQuestion(String prompt, String correctAnswer, ArrayList<String> wrongAnswers, String justification, String taskLetter, boolean hasImage, String fileUpload, ArrayList<Image> images, LogInfo logInfo) {
		QuestionDB.getInstance().createQuestion(prompt, correctAnswer, justification, taskLetter, hasImage, logInfo);
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return;
		}
		
		addWrongAnswers(wrongAnswers, questionId, logInfo);
		
		if (hasImage) {
			for (Image img : images) {
				String imageType = img.getImageType();
				String imageData = img.getImageLoc();
				img.setImageLoc("temp");
				img.setQuestionId(questionId);
				ImageDB.getInstance().addImage(img, logInfo);
				img = ImageDB.getInstance().getImage(questionId, "temp", logInfo);
				img.setImageType(imageType);
				fileUpload += String.valueOf("ImageID - " + img.getImageId()) + ", QuestionID - " + String.valueOf(img.getQuestionId()) + "." + img.getImageType();
								
				try {
					byte[] decodedBytes = Base64.getDecoder().decode(imageData);
					
					Files.write(Paths.get(fileUpload), decodedBytes);
					
					System.out.println("Image file created successfully at: " + fileUpload);
					img.setImageLoc(fileUpload);
					ImageDB.getInstance().updateImage(img, logInfo);
				}catch(IOException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		}
	}
	
	public Boolean deleteQuestion(String prompt, String filePath, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		questions.Question q = QuestionDB.getInstance().getQuestion(questionId, logInfo);
		
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
			ImageDB.getInstance().deleteImages(questionId, logInfo);
		}
		
		QuestionDB.getInstance().deleteQuestion(questionId, logInfo);
		deleteAllWrongAnswers(questionId, logInfo);
		
		return true;
	}
	
	public Boolean updateQuestionPrompt(String oldPrompt, String prompt, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(oldPrompt, new LogInfo());
		
		if (questionId == -1){
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting question id from prompt.");
			logInfo.addLog(logInfo);
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionPrompt(questionId, prompt, logInfo);
		
		return true;
	}
	
	public Boolean updateQuestionTaskLetter(String prompt, String taskLetter, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		QuestionDB.getInstance().updateTaskLetter(questionId, taskLetter, logInfo);
		
		return true;
	}
	
	public Boolean updateSettings(String settingsJson) {	
		Setting setting = getSettingFromJson(settingsJson);
		
		if (setting == null) {
			return false;
		}
		
		SettingDB.getInstance().updateSetting(setting);
		
		return true;
	}
	
	public Boolean updateQuestionJustification(String prompt, String justification, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionJustification(questionId, justification, logInfo);
		
		return true;
	}
	
	public Boolean updateQuestionAnswer(String prompt, String answer, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting question id from prompt.");
			logInfo.addLog(logInfo);
			return false;
		}
		
		QuestionDB.getInstance().updateQuestionAnswer(questionId, answer, logInfo);
		
		return true;
	}
	
	public String getQuestion(String prompt, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		questions.Question question = QuestionDB.getInstance().getQuestion(questionId, logInfo);
		if (question == null) {
			return "{}";
		}
				
		ArrayList<questions.Question>que = new ArrayList<>();
		que.add(question);
				
		return formatQuestionsAsJson(que, logInfo);
	}
	
	public void addWrongAnswers(ArrayList<String> wrongAnswers, int questionId, LogInfo logInfo) {
		int val = 0;
		for (String wrongAnswer : wrongAnswers) {
			WrongAnswer.getInstance().addWrongAnswer(val, wrongAnswer, questionId, logInfo);
			val++;
		}
	}
	
	public Boolean addWrongAnswer(String answer, String prompt, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		addWrongAnswer(answer, questionId, logInfo);
		
		return true;
	}
	
	public void addImage(String imageJson, String fileUpload, LogInfo logInfo) {
		Image image = getImageFromJson(imageJson, logInfo);
		
		int questionId = image.getQuestionId();
		String imageData = image.getImageLoc();
		String imageType = image.getImageType();
		
		image.setImageLoc("temp");
		
		ImageDB.getInstance().addImage(image, logInfo);
		
		image = ImageDB.getInstance().getImage(questionId, "temp", logInfo);
		
		image.setImageType(imageType);
		fileUpload += String.valueOf("ImageID - " + image.getImageId()) + ", QuestionID - " + String.valueOf(image.getQuestionId()) + "." + image.getImageType();
						
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(imageData);
			
			Files.write(Paths.get(fileUpload), decodedBytes);
			
			System.out.println("Image file created successfully at: " + fileUpload);
			image.setImageLoc(fileUpload);
			ImageDB.getInstance().updateImage(image, logInfo);
			if (!QuestionDB.getInstance().getQuestion(questionId, logInfo).getHasImage()) {
				QuestionDB.getInstance().updateQuestionHasImage(questionId, true, logInfo);
			}
		}catch(IOException e) {
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting image data: " + e.getStackTrace());
			logInfo.addLog(logInfo);
		}
	}
	
	public void addWrongAnswer(String answer, int questionId, LogInfo logInfo) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId, logInfo);
				
		int maxVal = 0;
		
		for (int wrongAnswerId : wrongAnswerIds) {
			if (wrongAnswerId >= maxVal) {
				maxVal = wrongAnswerId + 1;
			}
		}
		
		WrongAnswer.getInstance().addWrongAnswer(maxVal, answer, questionId, logInfo);
	}
	
	public Boolean deleteImage(Image image, String imageType, LogInfo logInfo) {
		int questionId = image.getQuestionId();
		
		if (questionId == -1) {
			return false;
		}
		
		questions.Question question = QuestionDB.getInstance().getQuestion(questionId, logInfo);
		
		question.deleteImage(image);
		
		if (!question.getHasImage()) {
			QuestionDB.getInstance().updateQuestionHasImage(questionId, question.getHasImage(), logInfo);
		}
		
		ImageDB.getInstance().deleteImage(image.getImageId(), logInfo);
		
		Path filePath = Paths.get(image.getImageLoc());
		
		try {
			Files.delete(filePath);
			System.out.println("Deleted image.");
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully deleted image from file system.");
		}catch (IOException e) {
			System.out.println("Error deleting image: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error deleting image from file system: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return true;
	}
	
	public Boolean deleteWrongAnswer(int wrongAnswerId, String prompt, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		WrongAnswer.getInstance().deleteWrongAnswer(wrongAnswerId, questionId, logInfo);
		
		return true;
	}
	
	public Boolean deleteWrongAnswer(String wrongAnswer, String prompt, LogInfo logInfo) {
		int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		int wrongAnswerId = WrongAnswer.getInstance().getWrongAnswerId(questionId, wrongAnswer);
		
		WrongAnswer.getInstance().deleteWrongAnswer(wrongAnswerId, questionId, logInfo);
		
		return true;
	}
	
	public void deleteAllWrongAnswers(int questionId, LogInfo logInfo) {
		ArrayList<Integer> wrongAnswerIds = WrongAnswer.getInstance().getWrongAnswerId(questionId, logInfo);
		
		questions.Question q = QuestionDB.getInstance().getQuestion(questionId, logInfo);
		String prompt = q.getPrompt();
		
		for (int wrongAnswerId: wrongAnswerIds) {
			deleteWrongAnswer(wrongAnswerId, prompt, logInfo);
		}
	}
	
	public Boolean updateWrongAnswer(String prompt, String newWrongAnswer, String wrongAnswer, LogInfo logInfo) {
		int questionId = -1;
		
		questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
		
		if (questionId == -1) {
			return false;
		}
		
		int wrongAnswerId = WrongAnswer.getInstance().getWrongAnswerId(questionId, wrongAnswer);
		
		System.out.println("New wrong answer: " + newWrongAnswer + "\nWrong answer: " + wrongAnswer + "\nWrong answer Id: " + wrongAnswerId);
		
		if (wrongAnswerId == -1) {
			return false;
		}
		
		WrongAnswer.getInstance().updateWrongAnswer(wrongAnswerId, newWrongAnswer, questionId, logInfo);
		
		return true;
	}
	
	public String getTaskDesc(String taskLetter, LogInfo logInfo) {
		return TaskList.getInstance().getTask(taskLetter, logInfo);
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
	
	private Image getImageFromJson(String imageJson, LogInfo logInfo){
		Image image = new Image();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			image = objectMapper.readValue(imageJson, Image.class);
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully got image from JSON object.");
		}catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting image from JSON object: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return image;
	}
	
	public Setting getSettingFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			Setting t = objectMapper.readValue(json, Setting.class);
			
			return t;
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		return null;
	}
	
	public <T> String formatClassAsJson(T user) {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		try {
			json = objectMapper.writeValueAsString(user);
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return json;
	}
	
	public String formatQuestionsAsJson(ArrayList<questions.Question>question, LogInfo logInfo) {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		try {
			HashMap<String, ArrayList<questions.Question>> finalBoss = new HashMap<>();
			finalBoss.put("Questions", question);
			json = objectMapper.writeValueAsString(finalBoss);
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully converted questions into JSON String.");
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error converting questions into JSON String: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		return json;
	}
	
//private ArrayList<String> parseCSV(String csv) {
//	ArrayList<String> list = new ArrayList<>();
//	
//	return list;
//}
}

class Box<T> {
  T value; // T is a placeholder for any data type

  void set(T value) {
    this.value = value;
  }

  T get() {
    return value;
  }
}