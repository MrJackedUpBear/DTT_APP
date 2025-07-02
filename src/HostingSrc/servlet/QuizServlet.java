package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
		}else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private boolean isNum(String num) {
		if (num == null || num.isEmpty() || num.isBlank()) {
			return false;
		}
		
		for (char c : num.toCharArray()) {
			switch(c) {
				case '1':
					break;
				case '2':
					break;
				case '3':
					break;
				case '4':
					break;
				case '5':
					break;
				case '6':
					break;
				case '7':
					break;
				case '8':
					break;
				case '9':
					break;
				default:
					return false;
			}
		}
	
		
		return true;
	}
}
