package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.time.*;

public class Mail implements Runnable{
    private static final Properties properties = System.getProperties();
    private final Config config = new Config();
    private final String title;
    private final String body;
    
    public Mail(String body){
        title = "DTT App Suggestions";
        this.body = body;
    }

    @Override
    public void run() {
        sendEmail(title, body);
    }

    private void sendEmail(String title, String body){
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

            LocalDateTime dt = LocalDateTime.now();

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getSender()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(config.getRecipient()));
            message.setSubject(title + " - " + String.valueOf(dt));
            message.setText(body);
            System.out.println("Sending email...");
            Transport.send(message);
            System.out.println("Mail successfully sent");
        }catch(Exception e){
            System.out.println("Error for " + title + ": " + e.getMessage());
        }
    }
}
