package com.lab.attendance.monetoring.system.service;

import com.lab.attendance.monetoring.system.dtos.LabSessionDto;

import jakarta.servlet.http.HttpServletRequest;

public interface LabSessionService {

	LabSessionDto createSession(LabSessionDto dto, HttpServletRequest request);

	LabSessionDto getSessionBySessionId(String sessionId);
}
