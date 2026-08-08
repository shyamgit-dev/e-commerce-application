package com.sam.service.Impl;

import com.sam.entity.User;
import com.sam.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("emailService")
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final SendGrid sendGrid;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    @Async
    @Override
    public void sendEmail(User user,String subject,String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmailId);
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);

        log.info("Email sent from {}",fromEmailId);
        log.info("Subject : {}",subject);
        log.info("to : {}",user.getEmail());

    }

    @Async
    @Override
    public void sendEmailSendGrid(User user, String subject, String body) throws IOException {

        Email from = new Email(fromEmailId);
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain",body);
        Mail mail = new Mail(from,subject,to,content);

        Request request = new Request();

        try{
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            request.setEndpoint("mail/send");

            Response response = sendGrid.api(request);
            log.info("Send email with code "+response.getStatusCode());
        }catch (IOException e)
        {
            log.error("Failed to send emaill",e);
        }
    }
}
