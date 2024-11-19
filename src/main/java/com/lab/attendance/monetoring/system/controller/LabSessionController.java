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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllLabs(@RequestParam(required = false, defaultValue = "all") String status, HttpServletRequest request){
		
		try {
			Map<String, List<LabSessionDto>> map = new HashMap<>();
			
			List<LabSessionDto> labs = labSessionService.getAllLabSessions(request);
			
			map.put("allLabSessions", labs);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/update/{labSessionId}")
	public ResponseEntity<?> updateSessionForLab(@PathVariable String labSessionId, @RequestBody LabSessionDto dto, HttpServletRequest request){
		
		Map<String, Object> map = new HashMap<>();
		
		try {
			LabSessionDto sessionDto = labSessionService.updateSession(labSessionId, dto, request);
			
			map.put("session", sessionDto);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/get/{labSessionId}")
	public ResponseEntity<?> getSessionBySessionId(@PathVariable String labSessionId, HttpServletRequest request){
		
		Map<String, Object> map = new HashMap<>();
		
		try {
			LabSessionDto dto = labSessionService.getSessionBySessionId(labSessionId, request);
			
			map.put("session", dto);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
