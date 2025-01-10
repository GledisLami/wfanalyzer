package com.analyzer.wfmarket.util;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomMailSender {

    private final Logger logger = LoggerFactory.getLogger(CustomMailSender.class);
    private final JavaMailSenderImpl mailSender;


    public CustomMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String body, String cc, byte[] attachment) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(to);
            mimeMessageHelper.setTo(to);

            logger.info("Email text before setting it to mimeMessageHelper: {}", body);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
            if (attachment != null) {
                ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(attachment, "application/octet-stream");
                mimeMessageHelper.addAttachment("market_report" + ".csv", byteArrayDataSource);
            }
            if (cc != null) {
                if (!cc.isEmpty()) {
                    mimeMessageHelper.setCc(cc.trim().split(";"));
                }
            }
            logger.info("Sending email... {}", mimeMessage);
            mailSender.send(mimeMessage);
            logger.info("Email sent successfully");
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage(), e);
        }
    }
}
