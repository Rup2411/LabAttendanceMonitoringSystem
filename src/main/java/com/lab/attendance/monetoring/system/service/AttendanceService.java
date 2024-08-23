package com.lab.attendance.monetoring.system.service;

import java.util.List;
import java.util.Map;

import com.lab.attendance.monetoring.system.dtos.AttendanceRequestDto;

public interface AttendanceService {

	void markAttendance(AttendanceRequestDto dto);

	Map<String, List<Map<String, String>>> getAllAttendanceDetails(String labSessionId);

}
