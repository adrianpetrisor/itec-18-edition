package com.seventailed.engine.email;

import com.seventailed.engine.EngineApplication;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

@Component
public class EngineEmailService {
    private Logger emailLogger = LoggerFactory.getLogger("email");
    private HashMap<String, String> emailTemplates = new HashMap<>();

    @Autowired
    private JavaMailSender javaMailSender;

    public EngineEmailService() {
        emailLogger.info("Initializing email service.");

        try {
            emailTemplates.put("notification", new String(EngineApplication.class.getResourceAsStream("/templates/email/notification_email.html").readAllBytes(), StandardCharsets.UTF_8));
            emailTemplates.put("redirect", new String(EngineApplication.class.getResourceAsStream("/templates/email/redirect_email.html").readAllBytes(), StandardCharsets.UTF_8));
        }catch (Exception exception) {
            exception.printStackTrace();
        }

        emailLogger.info("Registered " + emailTemplates.size() + " email templates.");
    }

    @Async
    public void sendEmail(String template, String to, String subject, String title, String message, String... additionalParams) {
        if(!emailTemplates.containsKey(template)) {
            emailLogger.info("Can't find template " + template + ".");
            return;
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom("no-reply@seventailed.com");

            String htmlBody = emailTemplates.get(template);
            htmlBody = htmlBody.replace("{{title}}", title);
            htmlBody = htmlBody.replace("{{message}}", message);

            if(template.equalsIgnoreCase("redirect")) {
                htmlBody = htmlBody.replace("{{redirectUrl}}", additionalParams[0]);
                htmlBody = htmlBody.replace("{{redirectMessage}}", additionalParams[1]);
            }

            mimeMessageHelper.setText(htmlBody, true);

            javaMailSender.send(mimeMessage);

            emailLogger.info("Sent email with template " + template + " and subject " + subject + " to " + to + ".");
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
