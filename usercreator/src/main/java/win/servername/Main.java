package win.servername;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Scanner;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Main {
    final static int NUM_QUESTIONS = 10;
    final static int TIME_LIMIT = 15; //seconds
    final static String pass = "9WMn;v81r<!.^STTq~L&XWY,L7QTrF~kF\"9*\\7rs-W\\-QDR£Ev";

    public static void main(String[] args){
        User user = getInfo();

        HashMap<String, byte[]> hashAndSalt = generatePassword();

        if (hashAndSalt == null){
            return;
        }

        user.setPassword(hashAndSalt.get("Password"));
        user.setSalt(hashAndSalt.get("Salt"));

        Setting setting = new Setting();
        setting.setNumQuestions(NUM_QUESTIONS);
        setting.setTimeLimit(TIME_LIMIT);

        SettingDB.getInstance().createSetting(setting);

        user.setSettingId(setting.getSettingId());

        UserDB.getInstance().createUser(user);

        UserRolesDB.getInstance().addUserRole(user.getUserId(), user.getUserType());
    }

    private static User getInfo(){
        User user = new User();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter new user's first name");
        user.setFirstName(scanner.nextLine());

        System.out.println("Enter new user's last name");
        user.setLastName(scanner.nextLine());

        System.out.println("Enter new user's email address");
        user.setEmail(scanner.nextLine());

        boolean isValid = false;

        int role = 0;

        while (!isValid){
            System.out.println("Here are the available user roles: ");
            System.out.println("\t1. Admin");
            System.out.println("\t2. General User");
            System.out.println("\t3. Elevated User");
            System.out.println("Please enter the number corresponding to the user type you would like this user to have.");
            String tempVal = scanner.nextLine();

            try{
                role = Integer.parseInt(tempVal);

                if (role > 0 && role < 4){
                    isValid = true;
                }
            }catch (Exception e){
                System.out.println("Not an integer. Try again.");
                isValid = false;
            }
        }
        user.setUserType(role);

        scanner.close();

        return user;
    }

    private static HashMap<String, byte[]> generatePassword(){
        HashMap<String, byte[]> passAndSalt = new HashMap<>();

        SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
				
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
            passAndSalt.put("Password", hash);
            passAndSalt.put("Salt", salt);
            return passAndSalt;
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }
}
