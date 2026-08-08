package com.sam.service;

import com.sam.entity.User;

import java.io.IOException;

public interface EmailService {

    void sendEmail(User user,String subject,String body);

    void sendEmailSendGrid(User user,String subject,String body) throws IOException;
}
