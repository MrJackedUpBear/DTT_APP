package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

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
import database.Database;
import mail.Mail;
import questions.QuestionGenerator;
import questions.Question;
import database.Image;
import database.QuestionDB;


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
		response.setHeader("Access-Control-Allow-Origin", "*");
		String requestURI = request.getRequestURI();
		String infoRequest = requestURI.substring("/DTT_APP/QuizServlet/".length());
		
		if (infoRequest.equals("RandomQuestions")) {
			String temp = request.getParameter("NumQuestions");
			
			if (!isNum(temp)) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			int numQuestions = Integer.parseInt(temp);
			response.getWriter().println(Database.getInstance().getRandomQuestions(numQuestions));
		}else if (infoRequest.equals("SpecificQuestion")){
			String prompt = request.getParameter("Prompt");
			if (prompt == null || prompt.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			response.getWriter().print(Database.getInstance().getQuestion(prompt));
		}else if (infoRequest.equals("QuestionTotal")) {
			response.getWriter().print(Database.getInstance().getTotalQuestions());
		}else if (infoRequest.equals("QuestionsFrom")) {
			String temp1 = request.getParameter("Start");
			String temp2 = request.getParameter("End");
			
			if (!isNum(temp1) || !isNum(temp2)) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			int start = Integer.parseInt(temp1);
			int end = Integer.parseInt(temp2);
			
			response.getWriter().print(Database.getInstance().getQuestionsFrom(start, end));
		}else if (infoRequest.equals("Image")) {
			String imageName = request.getParameter("ImageName");
			
			Image image = Database.getInstance().getImage(imageName);
			
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
		response.setHeader("Access-Control-Allow-Origin", "*");
		String postRequest = request.getRequestURI().substring("/DTT_APP/QuizServlet/".length());
		
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
			
			
			if (!Database.getInstance().addQuestions(questionJson, pathUpload)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
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
			
			if (!Database.getInstance().updateQuestionPrompt(oldPrompt, newPrompt)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
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
			
			if (!Database.getInstance().updateQuestionAnswer(prompt, answer)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
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
			
			
			if (!Database.getInstance().deleteQuestion(prompt, pathUpload)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}else if (postRequest.equals("UpdateWrongAnswer")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String req = stringRequest.toString();
			
			String tempQuestionId = req.split("\"Answer Id\":")[1];
			req = req.split("\"Answer Id\":")[0];
			tempQuestionId = tempQuestionId.replaceAll("}", "");
			
			if (!isNum(tempQuestionId)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			int questionId = Integer.parseInt(tempQuestionId);
			
			String wrongAnswer = req.split("\"Wrong Answer\":\"")[1];
			wrongAnswer = wrongAnswer.split("\",")[0];
			req = req.split("\"Wrong Answer\":\"")[0];
			
			String prompt = req.split("\"Prompt\":\"")[1];
			prompt = prompt.split("\",")[0];
			
			if (!Database.getInstance().updateWrongAnswer(prompt, wrongAnswer, questionId)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			
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
			
			Runnable run = new Mail(message);
			
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
				
				ArrayList<Question> questions = QuestionGenerator.readPDF(filePath);
				
				String json = Database.getInstance().formatQuestionsAsJson(questions);
				
				response.getWriter().println(json);
			}catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
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
			
			if (!Database.getInstance().updateQuestionJustification(prompt, justification)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
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
			
			if (!Database.getInstance().updateQuestionTaskLetter(prompt, taskLetter)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
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
			
			
			if (!Database.getInstance().deleteWrongAnswer(wrongAnswer, prompt)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
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
			
			if (!Database.getInstance().addWrongAnswer(wrongAnswer, prompt)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
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
			
			Database.getInstance().addImage(req, pathUpload);
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
			
			Image image = Database.getInstance().getImage(imageName);
			
			if (image == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			String typeOfImage = image.getImageLoc().substring(image.getImageLoc().lastIndexOf('.') + 1);
			
			if (!Database.getInstance().deleteImage(image, typeOfImage)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
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
