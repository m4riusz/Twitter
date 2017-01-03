package com.twitter.service;

import com.twitter.model.EmailType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mariusz on 03.01.17.
 */

@Service
@Transactional
class EmailServiceImpl implements EmailService {

    public static final String TEMPLATE_EXTENSION = ".ftl";
    private JavaMailSender javaMailSender;
    private Configuration configuration;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration configuration) {
        this.javaMailSender = javaMailSender;
        this.configuration = configuration;
    }

    @Override
    public void sendEmail(String to, String from, String subject, String content, EmailType emailType) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setContent(content, emailType.getType());
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setSubject(subject);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendEmail(String to, String from, String subject, String templateName, Map<String, Object> model, EmailType emailType) throws IOException, TemplateException, MessagingException {
        sendEmail(to, from, subject, renderTemplate(templateName, model), emailType);
    }

    private String renderTemplate(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = configuration.getTemplate(getFixedTemplateName(templateName));
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private String getFixedTemplateName(String templateName) {
        return templateName.endsWith(TEMPLATE_EXTENSION) ? templateName : templateName + TEMPLATE_EXTENSION;
    }
}
