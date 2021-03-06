package uk.ac.ebi.spot.webulous.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.webulous.model.DataConversionRunDocument;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Simon Jupp
 * @date 30/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Reading these from application.properties
    @Value("${mail.from:}")
    private String from;
    @Value("${mail.to:}")
    private String to;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void sendEmailNotification(String to, String subject, String message) {
        sendEmailNotification( new String[] {to}, null, subject, message );
    }

    public void sendEmailNotification(String to, String cc, String subject, String message) {
        sendEmailNotification( new String[] {to}, new String[] {cc}, subject, message );
    }

    public void sendEmailNotification(String[] to, String subject, String message) {
        sendEmailNotification( to, null, subject, message );
    }

    public void sendEmailNotification(String to, String []cc, String subject, String message) {
        sendEmailNotification( new String[] {to}, cc, subject, message );
    }

    public void sendEmailNotification(String[] to, String[] cc, String subject, String message) {


        // Set up some of the values used in mail body

        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        if (cc != null) {
            mailMessage.setCc(cc);
        }
        mailMessage.setBcc(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);

    }

    // Format text for email
    private String createErrorForEmail(String errorString) {
        String emailString = errorString;
        emailString = emailString.replaceAll("\\[", "");
        emailString = emailString.replaceAll("]", "");
        emailString = emailString.replaceAll(", ", "\n");
        return emailString;
    }

    // Getter and setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}