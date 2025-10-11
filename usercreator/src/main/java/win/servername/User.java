package win.servername;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] password;
    private byte[] salt;
    private int settingId;
    private int userType;

    // Getters and setters
    public int getUserType(){return userType;}
    public void setUserType(int userType){this.userType = userType;}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public byte[] getPassword() { return password; }
    public void setPassword(byte[] password) { this.password = password; }

    public byte[] getSalt() { return salt; }
    public void setSalt(byte[] salt) { this.salt = salt; }

    public int getSettingId() { return settingId; }
    public void setSettingId(int settingId) { this.settingId = settingId; }
}

