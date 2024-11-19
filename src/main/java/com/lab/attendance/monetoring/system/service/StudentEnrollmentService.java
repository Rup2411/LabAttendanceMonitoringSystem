package com.lab.attendance.monetoring.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lab.attendance.monetoring.system.dtos.LabEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.dtos.UserEnrollmentResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface StudentEnrollmentService {

	UserEnrollmentResponseDto getAllLabsEnrolledByStudent(String rollNo);

	List<LabEnrollmentResponseDto> getStudentsEnrolledInLabs(String labCode, HttpServletRequest request);

	List<Map<String, Map<String, String>>> enrollStudentsinLab(Set<String> rollNo, Set<String> labCodes,
			HttpServletRequest request);

}
