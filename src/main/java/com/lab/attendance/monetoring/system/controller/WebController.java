package com.lab.attendance.monetoring.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/web")
public class WebController {

	@GetMapping("/labs")
	public String getLabsData() {
		return "allLabs";
	}
	
	@GetMapping("editLab")
	public String updateLab() {
		return "editLab";
	}
	
	@GetMapping("createLab")
	public String createLab() {
		return "createLab";
	}
	
	@GetMapping("createLabSession")
	public String createLabSession() {
		return "createLabSession";
	}
	
	@GetMapping("/sessions")
	public String getAllSessions() {
		return "allSessions";
	}
	
	@GetMapping("editSession")
	public String updateSession() {
		return "editSession";
	}
	
	@GetMapping("enrollStudents")
	public String enrollStudents() {
		return "enrollStudents";
	}
	
	@GetMapping("attendance")
	public String markAttendance() {
		return "attendance";
	}
}
