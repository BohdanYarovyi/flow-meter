package com.yarovyi.flowmeter.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {
    private static final Path BASE_LETTERS_PATH = Path.of("src/main/resources/templates/util-html/email-letters/");

    private final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);
    private final FileReaderService fileReaderService;
    private final JavaMailSender sender;


    @Override
    public void sendMessageWithPassword(String to, String password) {
        var htmlFileName = "SendPasswordToEmailForm.html";
        var htmlPath = BASE_LETTERS_PATH.resolve(htmlFileName);

        try {
            String subject = "Welcome!";
            String html = fileReaderService
                    .readFile(htmlPath)
                    .formatted(password);

            sendHtml(to, subject, html);
        } catch (IOException | RuntimeException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException("Failed to send letter to: " + to, e);
        }
    }


    public void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            sender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Failed to configure letter");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send letter");
        }
    }

}
