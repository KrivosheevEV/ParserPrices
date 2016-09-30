package ru.parserprices.myparser;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by KrivosheevEV on 30.09.2016.
 */
public class Email {

    public Email(String args[], String givenSubject, String givenText){

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", args[2]);

        Session session = Session.getDefaultInstance(properties); // default session

        try {
            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(args[1])); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(args[0]));

            message.setSubject(givenSubject); // subject line

            // actual mail body
            message.setText(givenText);

            // Send message
            Transport.send(message); System.out.println("Email Sent successfully....");
        } catch (MessagingException mex){ mex.printStackTrace(); }

    }

}
