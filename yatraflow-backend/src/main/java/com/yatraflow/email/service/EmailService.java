package com.yatraflow.email.service;

public interface EmailService {

    void sendEmail(String to, String subject, String htmlContent);
}
