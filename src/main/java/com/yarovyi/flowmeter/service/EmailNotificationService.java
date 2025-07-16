package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.exception.EmailNotificationException;
import com.yarovyi.flowmeter.exception.HtmlMailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.IllegalFormatException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {
    private static final Path BASE_LETTERS_PATH = Path.of("src/main/resources/templates/util-html/email-letters/");

    private final FileReaderService fileReaderService;
    private final JavaMailSender sender;


    @Override
    public void sendMessageWithPassword(String to, String password) {
        if (to == null || password == null) {
            throw new IllegalArgumentException("Parameters 'to' or 'password' are null");
        }

        var htmlFileName = "SendPasswordToEmailForm.html";
        var htmlPath = BASE_LETTERS_PATH.resolve(htmlFileName);

        try {
            String subject = "Welcome!";
            String html = fileReaderService
                    .readFile(htmlPath)
                    .replace("{{password}}", password);

            sendHtml(to, subject, html);
        } catch (HtmlMailException e) {
            log.error("Failed to send letter, cause: {}", e.getMessage(), e);
            throw new EmailNotificationException("Failed to send letter to: " + to, e);
        } catch (IOException e) {
            log.error("Failed to read letter-html-template from: {}", htmlPath, e);
            throw new EmailNotificationException("Failed to send letter to: " + to, e);
        }
    }


    private void sendHtml(String to, String subject, String html) throws HtmlMailException {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            sender.send(message);
        } catch (MessagingException e) {
            throw new HtmlMailException("Failed to configure letter");
        } catch (MailException e) {
            throw new HtmlMailException("Failed to send letter");
        }
    }

}
