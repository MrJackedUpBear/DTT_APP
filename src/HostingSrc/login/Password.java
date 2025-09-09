package login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import database.RefreshToken;
import database.User;
import database.UserDB;
import token.TokenGenerator;

public class Password {
	private static Password p = new Password();
	
	public static Password getInstance() {
		return p;
	}
	
	public Boolean changePassword(String token, String password) {
		int userId = RefreshToken.getInstance().getUser(token);
		
		if (userId == -1) {
			return false;
		}
		
		Optional<User> user = UserDB.getInstance().getUserById(userId);
		
		if (!user.isPresent()) {
			return false;
		}
		
		ArrayList<byte[]> byteList = Hash.getInstance().hashNewPass(password);
		
		UserDB.getInstance().updateCredentials(user.get().getUserId(), byteList.get(1), byteList.get(0));
		
		return true;
	}
	
	public Boolean checkPassword(String email, String pass) {
		Optional<User> user = UserDB.getInstance().getUserByEmail(email);
		
		if (!user.isPresent()) {
			return false;
		}
		
		byte[] hashedPass = Hash.getInstance().hashPass(user.get().getSalt(), pass);
		
		if (hashedPass == null) {
			return false;
		}
		
		if (Arrays.equals(user.get().getPassword(), hashedPass)) {
			return true;
		}else {
			return false;
		}
	}
}
