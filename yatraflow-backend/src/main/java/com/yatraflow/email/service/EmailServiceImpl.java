package com.yatraflow.email.service;

import com.yatraflow.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");


            helper.setFrom(
                    "yatraflow.notifications@gmail.com",
                    "YatraFlow"
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info(
                    "HTML email sent successfully to: {}",
                    to
            );

        } catch (MessagingException | UnsupportedEncodingException | MailException e) {

            log.error(
                    "Failed to send HTML email to: {}",
                    to,
                    e
            );

            throw new BusinessException(
                    "Unable to send email. Please try again later."
            );
        }
    }
}
