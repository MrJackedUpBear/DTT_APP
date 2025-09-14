package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;

import database.Database;
import database.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logging.Log;
import logging.LogInfo;
import token.TokenGenerator;

/**
 * Servlet implementation class Settings
 */
@WebServlet("/Settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Settings() {
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
    	baseLog.setTypeOfRequest("GetUser");
    	baseLog.setLevel("Info");
    	
    	User admin = new User();
    	admin.setFirstName("System");
    	admin.setLastName("System");
    	admin.setUserId(-1000000);
    	
    	baseLog.setUser(admin);
    	baseLog.setLogInfo("Getting user...");
    	
    	Log.getInstance().log(baseLog);
		
		String user = TokenGenerator.getInstance().getUsername(accessToken, baseLog);
		
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String requestURI = request.getRequestURI();
		String infoRequest = requestURI.substring("/DTT_APP/Settings/".length());
		
		if (infoRequest.equals("AppSettings")) {
			LogInfo logInfo = new LogInfo();
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("GetAppSettings");
			logInfo.setUser(Database.getInstance().getUser(user, logInfo));
			logInfo.setLogInfo("Getting app settings...");
			
			
			logInfo.addLog(logInfo);
			User u = Database.getInstance().getUser(user, logInfo);
			
			String settings = Database.getInstance().getSettings(u.getSettingId());
			
			logInfo.log();
			
			response.getWriter().println(settings);
		}else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
String auth = request.getHeader("Authorization");
		
		if (auth == null){
			System.out.println("Issue with authorization from: " + request.getHeader("User-Agent"));
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
    	baseLog.setTypeOfRequest("GetUser");
    	baseLog.setLevel("Info");
    	
    	User admin = new User();
    	admin.setFirstName("System");
    	admin.setLastName("System");
    	admin.setUserId(-1000000);
    	
    	baseLog.setUser(admin);
    	baseLog.setLogInfo("Getting user...");
    	
    	Log.getInstance().log(baseLog);
		
		String user = TokenGenerator.getInstance().getUsername(accessToken, baseLog);
		
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String requestURI = request.getRequestURI();
		String infoRequest = requestURI.substring("/DTT_APP/Settings/".length());
		
		if (infoRequest.equals("Update")) {
			StringBuilder stringRequest = new StringBuilder();
			
			try (BufferedReader reader = request.getReader()){
				String line = "";
				
				while ((line = reader.readLine()) != null) {
					stringRequest.append(line);
				}
			}
			
			String settingJson = stringRequest.toString();
			
			Database.getInstance().updateSettings(settingJson);
		}else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}

}
