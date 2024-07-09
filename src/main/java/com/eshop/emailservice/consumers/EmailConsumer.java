package com.eshop.emailservice.consumers;

import com.eshop.emailservice.dto.EmailFormatDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class EmailConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "sendUserSignUpEmail", id = "signUpEmailConsumerGroup")
    public void sendEmailForUserSignUp(String message){
        EmailFormatDto emailMessage = null;
        try {
            emailMessage = objectMapper.readValue(message, EmailFormatDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello");
        sendActualEmail(emailMessage);
    }

    private void sendActualEmail(EmailFormatDto emailMessage) {

        /*
          SMTP -> Simple Mail Transfer Protocal
         */

        final String fromEmail = "notify.eshop@gmail.com"; //requires valid gmail id
        final String password = "nzvoqcjykoknnrun"; // correct password for gmail id
        final String toEmail = emailMessage.getToEmail(); // can be any email id

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail,emailMessage.getSubject(), emailMessage.getContent());

    }


}
