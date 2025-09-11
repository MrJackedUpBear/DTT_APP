package login;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import logging.Log;
import logging.LogInfo;

public class Hash {
	private static Hash hash = new Hash();
	
	public static Hash getInstance() {
		return hash;
	}
	
	ArrayList<byte[]> hashNewPass(String pass) {
		//Generate salt.
		ArrayList<byte[]> byteList = new ArrayList<>();
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
				
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			byteList.add(salt);
			byteList.add(hash);
			return byteList;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<byte[]> hashNewPassAndSalt(String pass) {
		ArrayList<byte[]> temp = new ArrayList<>();
		
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			temp.add(hash);
			temp.add(salt);
			return temp;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	byte[] hashPass(byte[] salt, String pass, LogInfo logInfo) {
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully hashed password.");
			Log.getInstance().log(logInfo);
			return hash;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error hashing password: " + e.getStackTrace());
			Log.getInstance().log(logInfo);
		}
		
		return null;
	}
}
