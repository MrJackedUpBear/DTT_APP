package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

import database.Database;
import database.User;
import database.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logging.LogInfo;
import login.Password;
import token.TokenGenerator;
import mail.EmailAuth;

/**
 * Servlet implementation class Auth
 */
@WebServlet("/Auth")
public class Auth extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Auth() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String requestURI = request.getRequestURI();
		String infoRequest = requestURI.substring("/DTT_APP/Auth/".length());
		String username = request.getParameter("Username");
		String token = request.getParameter("Token");
		
		if (username == null && token == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (infoRequest.equals("EmailAuth")) {
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("SendEmailAuth");
			logInfo.setUser(Database.getInstance().getUser(username, logInfo));
			logInfo.setLogInfo("Sending email auth...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
			
			Optional<User> user = UserDB.getInstance().getUserByEmail(username, logInfo);
			
			if (!user.isPresent()) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			String emailAccessToken = TokenGenerator.getInstance().putEmailAccessToken(username);
			
			Runnable run = new EmailAuth(emailAccessToken, username);
			
			new Thread(run).run();
			logInfo.log();
			
			response.sendError(HttpServletResponse.SC_OK);
			return;
		}else if (infoRequest.equals("IsValid")) {
			User system = new User();
			system.setFirstName("System");
			system.setLastName("System");
			system.setUserId(-1000000);
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("CheckIfTokenIsValid");
			logInfo.setUser(system);
			logInfo.setLogInfo("Verifying access token...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
			
			String u = TokenGenerator.getInstance().getUsername(token, logInfo);
			
			logInfo.log();
			
			if (u == null) {
				response.getWriter().println("{\"IsValid\":false}");
				return;
			}else {
				response.getWriter().println("{\"IsValid\":true}");
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
		// TODO Auto-generated method stub
		String auth = request.getHeader("Authorization");
		
		if (auth == null){
			System.out.println("Issue with authorization");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String authType = auth.substring(0, auth.indexOf(' '));
		
		String encodedAuth = auth.substring(auth.indexOf(' ') + 1);
		String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));
		String username = "";
		String password = "";
		String token = "";
		
		try {
			username = decodedAuth.substring(0, decodedAuth.indexOf(":"));
			password = decodedAuth.substring(decodedAuth.indexOf(":") + 1, decodedAuth.length());
		}catch (Exception e){
			Cookie[] cookies = request.getCookies();
			
			for (Cookie c : cookies) {
				if (c.getName().equals("RefreshToken")) {
					token = c.getValue();
				}
			}
		}
		
		if (authType.equals("Basic")) {
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("SendEmailAuth");
			logInfo.setUser(Database.getInstance().getUser(username, logInfo));
			logInfo.setLogInfo("Sending email auth...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
			
			if (!Password.getInstance().checkPassword(username, password, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
						
			String refreshToken = TokenGenerator.getInstance().putRefreshToken(username, logInfo);
			String accessToken = TokenGenerator.getInstance().putAccessToken(username);
			
			LocalDate expiryDate = LocalDate.now().plusMonths(6);
			
			String s = toCookieExpiresDate(expiryDate);
			
			String cookie = "RefreshToken=" + refreshToken +
	                "; HttpOnly" +
	                "; Path=/" +
	                "; SameSite=None" +
	                "; Secure" +
	                "; Expires=" + s; // s must be in RFC 1123 format (e.g., "Wed, 09 Sep 2025 15:00:00 GMT")

			logInfo.log();
			response.setHeader("Set-Cookie", cookie);	
			
			response.getWriter().print("{\"AccessToken\":" + "\"" + accessToken + "\"}");
		}else if (authType.equals("Email")) {
			String accessToken = TokenGenerator.getInstance().verifyEmailToken(password);
			
			if (accessToken == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			User system = new User();
			system.setFirstName("System");
			system.setLastName("System");
			system.setUserId(-1000000);
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("VerifyEmailToken");
			logInfo.setUser(system);
			logInfo.setLogInfo("Verifying email token...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
			
			username = TokenGenerator.getInstance().getUsername(accessToken, logInfo);
			
			String refreshToken = TokenGenerator.getInstance().putRefreshToken(username, logInfo);
			
			LocalDate expiryDate = LocalDate.now().plusMonths(6);
			
			String s = toCookieExpiresDate(expiryDate);
			long maxAgeSeconds = java.time.Duration.between(
			        LocalDateTime.now(),
			        expiryDate.atStartOfDay()
			).getSeconds();
			
			String cookie = "RefreshToken=" + refreshToken +
	                "; HttpOnly" +
	                "; Path=/" +
	                "; SameSite=None" +
	                "; Secure" +
	                "; Expires=" + s +
	                "; Max-Age=" + maxAgeSeconds; // s must be in RFC 1123 format (e.g., "Wed, 09 Sep 2025 15:00:00 GMT")

			response.setHeader("Set-Cookie", cookie);	
			
			logInfo.log();
			response.getWriter().print("{\"AccessToken\":" + "\"" + accessToken + "\"}");
		}else if (authType.equals("Refresh")) {
			String refreshToken = "";
		    Cookie[] cookies = request.getCookies();
		    
		    if (cookies != null) {
		        for (Cookie cookie : cookies) {
		            if (cookie.getName().equals("RefreshToken")) {
		                refreshToken = cookie.getValue();
		                // Do something with the cookieValue
		                break; // Found the cookie, no need to continue
		            }
		        }
		    }
		    
		    if (refreshToken.isEmpty()) {
		    	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    	return;
		    }
		    
		    User system = new User();
		    system.setFirstName("System");
		    system.setLastName("System");
		    system.setUserId(-1000000);
		    
		    LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("RefreshingToken");
			logInfo.setUser(system);
			logInfo.setLogInfo("Refreshing access token...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
		    
			String accessToken = TokenGenerator.getInstance().refreshAccessToken(refreshToken, logInfo);
				
				if (accessToken == null) {
					logInfo.log();
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
				logInfo.log();
				
				response.getWriter().print("{\"AccessToken\":" + "\"" + accessToken + "\"}");
				return;
		}else if (authType.equals("Password")) {
			String passHeader = request.getHeader("Password");
						
			if (passHeader == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			String pass = new String(Base64.getDecoder().decode(passHeader));
			
			User system = new User();
			system.setFirstName("System");
			system.setLastName("System");
			system.setUserId(-1000000);
			
			LogInfo logInfo = new LogInfo();
			
			logInfo.setLevel("Info");
			logInfo.setTypeOfRequest("PasswordChange");
			logInfo.setUser(system);
			logInfo.setLogInfo("Changing password...");
			logInfo.setLevel("Info");
			
			
			logInfo.addLog(logInfo);
			
			if (!Password.getInstance().changePassword(token, pass, logInfo)) {
				logInfo.log();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			logInfo.log();
		}
	}
	
	public static String toCookieExpiresDate(LocalDate localDate) {
	    ZonedDateTime zdt = localDate
	            .atStartOfDay(ZoneOffset.UTC)
	            .withZoneSameInstant(ZoneId.of("GMT"));

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	    return zdt.format(formatter);
	}
}
