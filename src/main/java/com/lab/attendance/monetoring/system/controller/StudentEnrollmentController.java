package com.lab.attendance.monetoring.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.attendance.monetoring.system.dtos.LabEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.dtos.RequestEnrollmentDto;
import com.lab.attendance.monetoring.system.dtos.UserEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.StudentEnrollmentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class StudentEnrollmentController {

	@Autowired
	StudentEnrollmentService enrollmentService;

	@PostMapping("/lab/enroll")
	public ResponseEntity<?> enrollStudentInSubjects(@RequestBody RequestEnrollmentDto dto,
			HttpServletRequest request) {

		try {
			List<Map<String, Map<String, String>>> result = enrollmentService.enrollStudentsinLab(dto.getRollNo(),
					dto.getLabCodes(), request);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/lab/user/{labCode}")
	public ResponseEntity<?> getAllUsersEnrolledInLab(@PathVariable String labCode, HttpServletRequest request) {

		try {
			List<LabEnrollmentResponseDto> dtos = enrollmentService.getStudentsEnrolledInLabs(labCode, request);

			return new ResponseEntity<>(dtos, HttpStatus.OK);
		} catch (CustomException e) {

			Map<String, String> errorResponse = new HashMap<>();

			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/lab/{rollNo}")
	public ResponseEntity<?> getAllLabsEnrolledByUser(@PathVariable String rollNo) {

		try {
			UserEnrollmentResponseDto dto = enrollmentService.getAllLabsEnrolledByStudent(rollNo);

			return new ResponseEntity<>(dto, HttpStatus.OK);

		} catch (CustomException e) {

			Map<String, String> errorResponse = new HashMap<>();

			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
