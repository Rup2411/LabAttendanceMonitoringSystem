package com.lab.attendance.monetoring.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lab.attendance.monetoring.system.dtos.LabEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.dtos.UserEnrollmentResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface StudentEnrollmentService {

	List<Map<String, Map<String, String>>> enrollStudentsinLab(String rollNo, Set<String> labCodes,
			HttpServletRequest request);

	UserEnrollmentResponseDto getAllLabsEnrolledByStudent(String rollNo);

	List<LabEnrollmentResponseDto> getStudentsEnrolledInLabs(String labCode, HttpServletRequest request);

}
