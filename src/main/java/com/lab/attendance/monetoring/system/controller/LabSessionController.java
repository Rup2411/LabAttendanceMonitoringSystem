package com.lab.attendance.monetoring.system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab.attendance.monetoring.system.dtos.LabSessionDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.LabSessionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/session")
public class LabSessionController {

	@Autowired
	LabSessionService labSessionService;
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createSessionForLab(@RequestBody LabSessionDto dto, HttpServletRequest request){
		
		Map<String, Object> map = new HashMap<>();
		
		try {
			LabSessionDto sessionDto = labSessionService.createSession(dto, request);
			
			map.put("session", sessionDto);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
