package com.yatraflow.auth.notification;

public interface EmailService {

    void sendEmail(String to, String subject, String htmlContent);
}
