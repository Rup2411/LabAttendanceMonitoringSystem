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

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.LabService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/lab")
public class LabController {

	@Autowired
	LabService labService;
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createLabSession(@RequestBody LabDto dto, HttpServletRequest request){
		
		try {
			Map<String, LabDto> map = new HashMap<>();
			
			LabDto labDto = labService.createLab(dto, request);
			map.put("labSession", labDto);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
