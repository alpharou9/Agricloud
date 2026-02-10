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

    public static void sendOrderConfirmation(String toEmail, String customerName, long orderId, double totalAmount) throws MessagingException {
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
        message.setSubject("AgriCloud - Order Confirmation #" + orderId);

        String emailBody = "Dear " + customerName + ",\n\n" +
                "Thank you for your order on AgriCloud!\n\n" +
                "Order Details:\n" +
                "Order Number: #" + orderId + "\n" +
                "Total Amount: $" + String.format("%.2f", totalAmount) + "\n\n" +
                "Your order has been confirmed and will be delivered to you within 3 business days maximum.\n\n" +
                "We will keep you updated on the status of your order.\n\n" +
                "Thank you for choosing AgriCloud!\n\n" +
                "Best regards,\n" +
                "The AgriCloud Team";

        message.setText(emailBody);
        Transport.send(message);
    }
}
