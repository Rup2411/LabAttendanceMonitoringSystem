package com.lab.attendance.monetoring.system.service;

import java.util.List;

public interface EmailService {

	void sendEmail(List<String> toEmailIds, String subject, String body);
}
