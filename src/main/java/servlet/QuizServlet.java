package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import logging.Log;
import logging.LogInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;

import database.Database;
import mail.Mail;
import questions.QuestionGenerator;
import token.TokenGenerator;
import questions.Question;
import database.Image;
import database.User;


/**
 * Servlet implementation class QuizServlet
 */

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50   // 50MB
    )
@WebServlet("/QuizServlet")
public class QuizServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public QuizServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String auth = request.getHeader("Authorization");
				
		if (auth == null){
			System.out.println("Issue with authorization");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String authType = auth.substring(0, auth.indexOf(' '));
				
		if (!authType.equals("Bearer")) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String encodedAuth = auth.substring(auth.indexOf(' ') + 1);
		String accessToken = new String(Base64.getDecoder().decode(encodedAuth));
		
		LogInfo baseLog = new LogInfo();
		
		User system = new User();
    	system.setFirstName("System");
    	system.setLastName("System");
    	system.setUserId(-1000000);
		
		baseLog.setLevel("Info");
		baseLog.setTypeOfRequest("GetUser");
		baseLog.setUser(system);
		baseLog.setLogInfo("Getting user...");
		baseLog.setLevel("Info");
		
		
		Log.getInstance().log(baseLog);
		
		
		String user = TokenGenerator.getInstance().getUsername(accessToken, baseLog);
		
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String requestURI = request.getRequestURI();
		String infoRequest = requestURI.substring("/DTT_APP/Quiz/".length());
		
		if (infoRequest.equals("RandomQuestions")) {
			String temp = request.getParameter("NumQuestions");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetRandomQuestions");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting random questions...");
			
			
			logInfo.addLog(logInfo);
			
			if (!isNum(temp)) {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Not an integer");
				logInfo.addLog(logInfo);
				logInfo.log();
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			int numQuestions = Integer.parseInt(temp);
			response.getWriter().println(Database.getInstance().getRandomQuestions(numQuestions, logInfo));
			
			logInfo.log();
		}else if (infoRequest.equals("SpecificQuestion")){
			String prompt = request.getParameter("Prompt");
			if (prompt == null || prompt.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetSpecificQuestion");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting specified question...");
			
			
			logInfo.addLog(logInfo);
			
			response.getWriter().print(Database.getInstance().getQuestion(prompt, logInfo));
			
			logInfo.log();
		}else if (infoRequest.equals("QuestionTotal")) {
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetNumberQuestions");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting total number of questions...");
			
			
			logInfo.addLog(logInfo);
			
			response.getWriter().print(Database.getInstance().getTotalQuestions(logInfo));
			
			logInfo.log();
		}else if (infoRequest.equals("QuestionsFrom")) {
			String temp1 = request.getParameter("Start");
			String temp2 = request.getParameter("End");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetQuestionsFrom");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting questions from start to end...");
			
			
			logInfo.addLog(logInfo);
			
			if (!isNum(temp1) || !isNum(temp2)) {
				logInfo.setLevel("Error");
				logInfo.setLogInfo("Either value provided is not an integer.");
				logInfo.addLog(logInfo);
				
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			int start = Integer.parseInt(temp1);
			int end = Integer.parseInt(temp2);
			
			response.getWriter().print(Database.getInstance().getQuestionsFrom(start, end, logInfo));
			
			logInfo.log();
		}else if (infoRequest.equals("Image")) {
			String imageName = request.getParameter("ImageName");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetImage");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting image...");
			
			
			logInfo.addLog(logInfo);
			
			Image image = Database.getInstance().getImage(imageName, logInfo);
			
			logInfo.log();
			
			if (image == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			String file = image.getImageLoc();
			
			File imageFile = new File(file);
			
			String typeOfImage = file.substring(file.lastIndexOf('.') + 1);
			
			response.setContentType("image/" + typeOfImage);
			
			try (FileInputStream fis = new FileInputStream(imageFile);
				ServletOutputStream os = response.getOutputStream()){
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
			}
		}else if (infoRequest.equals("User")) {
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetUser");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting image...");
			
			
			logInfo.addLog(logInfo);
			
			response.getWriter().print(Database.getInstance().getUserInfo(user, logInfo));
			
			logInfo.log();
		}
		else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String auth = request.getHeader("Authorization");
				
		if (auth == null){
			System.out.println("Issue with authorization");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String authType = auth.substring(0, auth.indexOf(' '));
		
		if (!authType.equals("Bearer")) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String encodedAuth = auth.substring(auth.indexOf(' ') + 1);
		String accessToken = new String(Base64.getDecoder().decode(encodedAuth));
		
		LogInfo baseLog = new LogInfo();
		
		User system = new User();
    	system.setFirstName("System");
    	system.setLastName("System");
    	system.setUserId(-1000000);
		
		baseLog.setLevel("Info");
		baseLog.setTypeOfRequest("GetUser");
		baseLog.setUser(system);
		baseLog.setLogInfo("Getting user...");
		baseLog.setLevel("Info");
		
		
		Log.getInstance().log(baseLog);
		
		String user = TokenGenerator.getInstance().getUsername(accessToken, baseLog);
		
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String postRequest = request.getRequestURI().substring("/DTT_APP/Quiz/".length());
		
		if (postRequest.equals("AddQuestions")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String questionJson = stringRequest.toString();
			
			if (questionJson == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			String pathUpload = System.getenv("UPLOAD_LOCATION");
			
			if (!questionJson.contains("prompt")|| !questionJson.contains("correctAnswer") || 
					!questionJson.contains("wrongAnswers") || !questionJson.contains("Questions")){
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			if (pathUpload == null) {
				System.out.println("You have not sent the environment variable UPLOAD_LOCATION. Please do so to continue");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			pathUpload += "\\";
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("AddQuestions");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Adding questions to db...");
			
			
			logInfo.addLog(logInfo);
			if (!Database.getInstance().addQuestions(questionJson, pathUpload, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}else if (postRequest.equals("UpdateQuestionPrompt")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String prompts = stringRequest.toString();
			
			prompts = prompts.split("\"Prompts\":\\[\"")[1];
			
			String oldPrompt = prompts.split("\",")[0];
			String newPrompt = prompts.split(",\"")[1];
			
			newPrompt = newPrompt.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("UpdatePrompt");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Updating question prompt...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().updateQuestionPrompt(oldPrompt, newPrompt, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			logInfo.log();
		}else if (postRequest.equals("UpdateQuestionAnswer")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String promptAnswer = stringRequest.toString();
			
			promptAnswer = promptAnswer.split("\"Prompt and Answer\":\\[\"")[1];
			
			String prompt = promptAnswer.split("\",")[0];
			String answer = promptAnswer.split(",\"")[1];
			
			answer = answer.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("UpdateCorrectAnswer");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Updating correct answer...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().updateQuestionAnswer(prompt, answer, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}else if (postRequest.equals("DeleteQuestion")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String prompt = stringRequest.toString();
			
			prompt = prompt.split("\"Prompt\":\"")[1];
			prompt = prompt.split("\"\\}")[0];
			
			String pathUpload = System.getenv("UPLOAD_LOCATION");
			
			if (pathUpload == null) {
				System.out.println("You have not sent the environment variable UPLOAD_LOCATION. Please do so to continue");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			pathUpload += "\\";
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("DeleteQuestion");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Deleting Question...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().deleteQuestion(prompt, pathUpload, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}else if (postRequest.equals("UpdateWrongAnswer")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
			
			String tempQuestionId = req.split("\"Answer Id\":\"")[1];
			tempQuestionId = tempQuestionId.substring(0, tempQuestionId.lastIndexOf("\""));
			req = req.split("\"Answer Id\":")[0];
			String wrongAnswer = tempQuestionId.replaceAll("}", "");
			
			
			String newWrongAnswer = req.split("\"Wrong Answer\":\"")[1];
			newWrongAnswer = newWrongAnswer.split("\",")[0];
			req = req.split("\"Wrong Answer\":\"")[0];
			
			String prompt = req.split("\"Prompt\":\"")[1];
			prompt = prompt.split("\",")[0];
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("UpdateWrongAnswer");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Updating wrong answer...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().updateWrongAnswer(prompt, newWrongAnswer, wrongAnswer, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			logInfo.log();
			
		}else if (postRequest.equals("Email")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
			
			String message = req.split("\"Message\":")[1];
			req = req.split("\"Message\":")[0];
			message = message.replaceAll("}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("SendSuggestion");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Sending suggestion...");
			
			
			logInfo.addLog(logInfo);
			
			Runnable run = new Mail(message, logInfo);
			logInfo.log();
			
			new Thread(run).run();
		}else if (postRequest.equals("ReadPDF")) {
			try {
				Part filePart = request.getPart("PDF");
				String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
				InputStream fileContent = filePart.getInputStream();
				
				if (!fileName.substring(fileName.indexOf('.')).equalsIgnoreCase(".pdf")) {
					Exception e = new Exception("Incorrect file type.");
					throw e;
				}
				
				Path uploadPath = Paths.get(getServletContext().getRealPath("/WEB-INF/uploads"));
				
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path filePath = uploadPath.resolve(fileName);
				
				Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
				
				LogInfo logInfo = new LogInfo();
				
				logInfo.setLevel("Info");
				logInfo.setTypeOfRequest("ReadPDF");
				logInfo.setUser(Database.getInstance().getUser(user, logInfo));
				logInfo.setLogInfo("Reading PDF...");
				
				
				logInfo.addLog(logInfo);
				
				ArrayList<Question> questions = QuestionGenerator.readPDF(filePath, logInfo);
				
				logInfo.setLogInfo("Formatting as Json...");
				logInfo.addLog(logInfo);
				
				String json = Database.getInstance().formatQuestionsAsJson(questions, logInfo);
				
				logInfo.log();
				
				response.getWriter().println(json);
			}catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				LogInfo logInfo = new LogInfo();
				
				logInfo.setLevel("Error");
				logInfo.setTypeOfRequest("ReadPDF");
				logInfo.setUser(Database.getInstance().getUser(user, logInfo));
				logInfo.setLogInfo("Error reading PDF: " + e.getStackTrace());
				
				
				logInfo.addLog(logInfo);
				logInfo.log();
				response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			}
		}else if (postRequest.equals("UpdateJustification")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String promptJustification = stringRequest.toString();
			
			promptJustification = promptJustification.split("\"Prompt and Justification\":\\[\"")[1];
			
			String prompt = promptJustification.split("\",")[0];
			String justification = promptJustification.split(",\"")[1];
			
			justification = justification.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("UpdateJustification");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Updating justification...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().updateQuestionJustification(prompt, justification, logInfo)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			
			logInfo.log();
		}else if (postRequest.equals("UpdateTaskLetter")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String promptTaskLetter = stringRequest.toString();
			
			promptTaskLetter = promptTaskLetter.split("\"Prompt and Task Letter\":\\[\"")[1];
			
			String prompt = promptTaskLetter.split("\",")[0];
			String taskLetter = promptTaskLetter.split(",\"")[1];
			
			taskLetter = taskLetter.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("UpdateTaskLetter");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Updating task letter...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().updateQuestionTaskLetter(prompt, taskLetter, logInfo)){
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}else if (postRequest.equals("DeleteWrongAnswer")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
						
			String promptWrongAnswer = req.split("\"Prompt and Wrong Answer\":\\[\"")[1];
			String prompt = promptWrongAnswer.split("\",")[0];
			String wrongAnswer = promptWrongAnswer.split("\",\"")[1];
			
			wrongAnswer = wrongAnswer.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("DeleteWrongAnswer");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Deleting wrong answer...");
			
			
			logInfo.addLog(logInfo);
			
			
			if (!Database.getInstance().deleteWrongAnswer(wrongAnswer, prompt, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			logInfo.log();
		}else if (postRequest.equals("AddWrongAnswer")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
			
			String promptWrongAnswer = req.split("\"Prompt and Wrong Answer\":\\[\"")[1];
			String prompt = promptWrongAnswer.split("\",")[0];
			String wrongAnswer = promptWrongAnswer.split(",\"")[1];
			
			wrongAnswer = wrongAnswer.replaceAll("\"\\]\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("AddWrongAnswer");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Adding wrong answer...");
			
			
			logInfo.addLog(logInfo);
			
			if (!Database.getInstance().addWrongAnswer(wrongAnswer, prompt, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}else if (postRequest.equals("AddImage")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String pathUpload = System.getenv("UPLOAD_LOCATION");
			
			if (pathUpload == null) {
				System.out.println("You have not sent the environment variable UPLOAD_LOCATION. Please do so to continue");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			pathUpload += "\\";
			
			String req = stringRequest.toString();
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("AddImage");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Adding image...");
			
			
			logInfo.addLog(logInfo);
			
			Database.getInstance().addImage(req, pathUpload, logInfo);
			
			logInfo.log();
		}else if (postRequest.equals("DeleteImage")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
			String imageName = req.split("\"Image Name\":\"")[1];
			
			imageName = imageName.replaceAll("\"\\}", "");
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("DeleteImage");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Deleting image...");
			
			
			logInfo.addLog(logInfo);
			
			Image image = Database.getInstance().getImage(imageName, logInfo);
			
			if (image == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			String typeOfImage = image.getImageLoc().substring(image.getImageLoc().lastIndexOf('.') + 1);
			
			if (!Database.getInstance().deleteImage(image, typeOfImage, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			logInfo.log();
		}
		else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

	private boolean isNum(String num) {
		if (num == null || num.isEmpty() || num.isBlank()) {
			return false;
		}
		
		try {
			Integer.parseInt(num);
			return true;
		}catch (Exception e) {
			return false;
		}
	
	}
}
