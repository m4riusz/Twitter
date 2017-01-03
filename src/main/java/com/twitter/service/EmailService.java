package com.twitter.service;

import com.twitter.model.EmailType;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mariusz on 03.01.17.
 */

@Service
public interface EmailService {

    void sendEmail(String to, String from, String subject, String content, EmailType emailType) throws MessagingException;

    void sendEmail(String to, String from, String subject, String templateName, Map<String, Object> model, EmailType emailType) throws IOException, TemplateException, MessagingException;
}
