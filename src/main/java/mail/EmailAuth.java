package mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailAuth implements Runnable {
	private static final Properties properties = System.getProperties();
    private final Config config = new Config();
    private String title;
    private String body;
    private String recipient;
    
    public EmailAuth(String body, String recipient){
        title = "DTT App - Email Verification Notification";
        this.body = "You need to verify your identity. Below is your code to do so.\n" + body;
        this.recipient = recipient;
    }
    
    public void run() {
        sendEmail();
    }
	
	public void sendEmail(){
        try{
            //sets up the mail properties
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", config.getHost());
            properties.put("mail.smtp.port", config.getPort());
            properties.put("mail.smtp.ssl.trust", config.getHost());

            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(config.getSender(), config.getPassword());
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getSender()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(title);
            message.setText(body);
            System.out.println("Sending email...");
            Transport.send(message);
            System.out.println("Mail successfully sent");
        }catch(Exception e){
            System.out.println("Error for " + title + ": " + e.getMessage());
        }
    }
}
