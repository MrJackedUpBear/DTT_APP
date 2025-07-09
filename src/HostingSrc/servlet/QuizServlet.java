package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import database.Database;

/**
 * Servlet implementation class QuizServlet
 */
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
			response.getOutputStream().println(Database.getInstance().getRandomQuestions(numQuestions));
		}else if (infoRequest.equals("SpecificQuestion")){
			String prompt = request.getParameter("Prompt");
			if (prompt == null || prompt.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			response.getOutputStream().print(Database.getInstance().getQuestion(prompt));
		}else if (infoRequest.equals("QuestionTotal")) {
			response.getOutputStream().print(Database.getInstance().getTotalQuestions());
		}else {
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
			questionJson = questionJson.substring("{\"Questions\":".length(), questionJson.length() - 1);
			
			if (questionJson == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			if (!questionJson.contains("]}]") || !questionJson.contains("Prompt")|| !questionJson.contains("Correct Answer") || 
					!questionJson.contains("Wrong Answers")) {
				response.sendError(HttpServletResponse.SC_UNPROCESSABLE_CONTENT);
				return;
			}
			
			if (!Database.getInstance().addQuestions(questionJson)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}else {
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
