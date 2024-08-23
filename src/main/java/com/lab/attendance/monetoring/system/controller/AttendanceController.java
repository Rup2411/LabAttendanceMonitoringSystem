package com.lab.attendance.monetoring.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.attendance.monetoring.system.dtos.AttendanceRequestDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.AttendanceService;

@RestController
@RequestMapping("api/attendance")
public class AttendanceController {

	@Autowired
	AttendanceService attendanceService;

	@PostMapping("/mark")
	public ResponseEntity<?> markAttendance(@RequestBody AttendanceRequestDto dto) {

		try {
			Map<String, String> map = new HashMap<>();

			attendanceService.markAttendance(dto);

			map.put("message", "Attendance Marked Successfully");

			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/details")
	public ResponseEntity<?> getAllAttendanceStudents(@RequestParam String labSessionId) {
		try {
			Map<String, Object> map = new HashMap<>();

			Map<String, List<Map<String, String>>> attendanceDetails = attendanceService
					.getAllAttendanceDetails(labSessionId);

			map.put("attendance", attendanceDetails);

			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
