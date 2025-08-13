package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.exception.EmailNotificationException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    @Mock
    private FileReaderService fileReaderService;
    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private EmailNotificationService service;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    sendMessageWithPassword(String to, String password)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
@Test
    void sendMessageWithPassword_whenIsOk_thenDoNothing() throws IOException {
        // given
        String to = "test@gmail.com";
        String password = "password123";
        String validHtml = getValidHtml();

        // mockito
        MimeMessage mockedMimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mockedMimeMessage);
        Mockito.when(fileReaderService.readFile(any()))
                .thenReturn(validHtml);

        // when
        assertDoesNotThrow(() -> service.sendMessageWithPassword(to, password));
    }

    @Test
    void sendMessageWithPassword_whenProblemWithJavaMailSender_thenThrowException() throws IOException {
        // given
        String to = "test@gmail.com";
        String password = "password123";
        String validHtml = getValidHtml();

        // mockito
        MimeMessage mockedMimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mockedMimeMessage);
        Mockito.when(fileReaderService.readFile(any()))
                .thenReturn(validHtml);
        Mockito.doThrow(new MailSendException(""))
                .when(javaMailSender)
                .send(mockedMimeMessage);

        // when
        assertThrows(EmailNotificationException.class, () -> service.sendMessageWithPassword(to, password));
    }

    private String getValidHtml() {
        return """
                <h1>Password</h1>
                <p>{{password}}</p>
                """;
    }

    @Test
    void sendMessageWithPassword_whenPathToHtmlTemplateIsWrong_thenThrowException() throws IOException {
        // given
        String to = "test@gmail.com";
        String password = "password123";

        // mockito
        Mockito.when(fileReaderService.readFile(any()))
                .thenThrow(new IOException());

        assertThrows(EmailNotificationException.class, () -> service.sendMessageWithPassword(to, password));
    }

    @Test
    void sendMessageWithPassword_whenParameterToIsNull_thenThrowException() {
        // given
        String to = null;
        String password = "password123";

        // when
        assertThrows(IllegalArgumentException.class, () -> service.sendMessageWithPassword(to, password));
    }

    @Test
    void sendMessageWithPassword_whenParameterPasswordIsNull_thenThrowException() {
        // given
        String to = "test@gmail.com";
        String password = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.sendMessageWithPassword(to, password));
    }

}