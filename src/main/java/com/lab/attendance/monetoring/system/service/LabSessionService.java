package com.lab.attendance.monetoring.system.service;

import java.util.List;

import com.lab.attendance.monetoring.system.dtos.LabSessionDto;

import jakarta.servlet.http.HttpServletRequest;

public interface LabSessionService {

	LabSessionDto createSession(LabSessionDto dto, HttpServletRequest request);

	LabSessionDto getSessionBySessionId(String sessionId, HttpServletRequest request);

	List<LabSessionDto> getAllLabSessions(HttpServletRequest request);

	LabSessionDto updateSession(String labSessionId, LabSessionDto dto, HttpServletRequest request);
}
