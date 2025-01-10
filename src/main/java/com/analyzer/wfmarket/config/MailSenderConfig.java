package com.analyzer.wfmarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfig {

    private final String port;
    private final String host;
    private final String username;
    private final String password;

    public MailSenderConfig(
            @Value("${mail.port}") String port,
            @Value("${mail.host}") String host,
            @Value("${mail.username}") String username,
            @Value("${mail.password}") String password) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    @Bean
    public JavaMailSenderImpl getMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        javaMailSender.setPort(Integer.parseInt(port));
        javaMailSender.setHost(host);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        return javaMailSender;
    }

}
