package com.lab.attendance.monetoring.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.EmailService;


@Service
public class EmailServiceImpl implements EmailService {

	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmailId;
	
	public void sendEmail(List<String> toEmailIds, String subject, String body) {
		
       try {
    	   SimpleMailMessage message = new SimpleMailMessage();
           
           message.setTo(toEmailIds.toArray(new String[0]));
           message.setSubject(subject);
           message.setText(body);
           message.setFrom(fromEmailId);
           
           javaMailSender.send(message);
	} catch (CustomException e) {
		e.getMessage();
	}
    }

}
