package esprit.farouk.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private static final String FROM_EMAIL = "farouknakkach@gmail.com";
    private static final String FROM_PASSWORD = "fsqmwfikwjmjeygj";

    public static void sendResetCode(String toEmail, String code) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("AgriCloud - Password Reset Code");
        message.setText("Hello,\n\nYour password reset code is: " + code +
                "\n\nThis code will expire shortly. If you did not request this, please ignore this email." +
                "\n\nAgriCloud Team");

        Transport.send(message);
    }
}
