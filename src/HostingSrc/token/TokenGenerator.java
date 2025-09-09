package token;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import database.Database;
import database.RefreshToken;
import database.User;
import database.UserDB;

public class TokenGenerator {
	private final int NUM_MONTHS = 6;
	
	private HashMap<String, TokenData> tokenMap = new HashMap<>();
	
	private static TokenGenerator instance = new TokenGenerator();
	
	public static TokenGenerator getInstance() {
		return instance;
	}
	
	private TokenGenerator() {
		
	}
	
	public String refreshAccessToken(String token) {
		int userId;
		if ((userId = RefreshToken.getInstance().getUser(token)) != -1) {
			Optional<User> user = UserDB.getInstance().getUserById(userId);
			
			if (!user.isPresent()) {
				return null;
			}
			
			return putAccessToken(user.get().getEmail());
		}
		
		return null;
	}
	
	public String putAccessToken(String username) {
		String token = UUID.randomUUID().toString();
				
		tokenMap.put(token, new TokenData(username));
		
		return token;
	}
	
	public String putRefreshToken(String username) {
		String token = UUID.randomUUID().toString();
		
		Optional<User> user = UserDB.getInstance().getUserByEmail(username);
		
		if (!user.isPresent()) {
			System.out.println("User not present");
			return null;
		}
		
		LocalDate expirationDate = LocalDate.now().plusMonths(NUM_MONTHS);
		
		RefreshToken.getInstance().addToken(token, expirationDate, user.get().getUserId());
		
		return token;
	}
	
	public String putEmailAccessToken(String username) {
		SecureRandom random = new SecureRandom();
		
	    int number = random.nextInt(1000000); // Generates from 0 to 999999
	    String token = String.format("%06d", number);
				
		tokenMap.put(token, new TokenData(username, 5));
		
		return token;
	}
	
	/*private void removeToken(String username) {
		for (HashMap.Entry<String, TokenData> entry : tokenMap.entrySet()) {
			if (entry.getValue().username.equals(username)){
				String key = entry.getKey();
				tokenMap.remove(key);
				return;
			}
		}
	}
	*/

	public String getUsername(String token) {
		if (tokenMap.containsKey(token)) {
			if (tokenMap.get(token).expirationTime > System.currentTimeMillis()) {
				return tokenMap.get(token).username;
			}else {
				tokenMap.remove(token);
			}
		}
		return null;
	}
	
	public String verifyEmailToken(String token) {
		if (tokenMap.containsKey(token) && tokenMap.get(token).expirationTime > System.currentTimeMillis() && tokenMap.get(token).isEmail) {
			String t = putAccessToken(tokenMap.get(token).username);
			tokenMap.remove(token);
			return t;
		}
		return null;
	}
}

class TokenData{
	String username;
	long expirationTime;
	boolean isEmail;
	
	int oneMinute = 60 * 1000;
	
	//One hour in milliseconds
	int oneHour = oneMinute * 60;
	
	TokenData(String username) {
		this.username = username;
		this.isEmail = false;
		this.expirationTime = System.currentTimeMillis() + oneHour;
	}
	
	TokenData(String username, int numMinutes){
		this.username = username;
		this.isEmail = true;
		this.expirationTime = System.currentTimeMillis() + (numMinutes * oneMinute);
	}
}