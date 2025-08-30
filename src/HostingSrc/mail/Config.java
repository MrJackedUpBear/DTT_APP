package mail;

public class Config {
	final String host = System.getenv("EMAIL_HOST");
	final String port = System.getenv("EMAIL_PORT");
	final String recipient = System.getenv("EMAIL_RECIPIENT");
	final String sender = System.getenv("EMAIL_SENDER");
	final String password = System.getenv("EMAIL_PASSWORD");
	
	public String getHost() {
		return host;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public String getPassword() {
		return password;
	}
}
