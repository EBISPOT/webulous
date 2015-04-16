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

    public void sendEmailNotification(String to, String cc, DataConversionRunDocument document) {


        // Set up some of the values used in mail body

        // Format mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setCc(cc);
        mailMessage.setBcc(getTo());
        mailMessage.setFrom(getFrom());
        mailMessage.setSubject("Webulous run for " + document.getTemplateName() + ": " + document.getStatus());



        mailMessage.setText(
                "You data submitted to webulous template " + document.getTemplateName() + " has completed with status: " + document.getStatus() + "\n\n" +
                document.getMessage()  + "\n"

        );
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