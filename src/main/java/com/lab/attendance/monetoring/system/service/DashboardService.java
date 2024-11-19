package com.lab.attendance.monetoring.system.service;

import java.util.Map;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.dtos.LabSessionDto;

public interface DashboardService {

	Long totalLabs();
	
	LabDto getAllLabs();
	
	Long totalLabSessions();
	
	LabSessionDto getAllLabSessions();
	
	Long totalStudentsInParticularLab(String labCode);
	
	Map<String, String> getAllStudentsInParticularLab(String labCode);
	
	Map<String, String> getAllAttendanceInParticularLabSession(String labSessionCode);
	
	
	
}
