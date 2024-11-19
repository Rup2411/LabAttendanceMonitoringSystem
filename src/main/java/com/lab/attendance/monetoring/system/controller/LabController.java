package com.lab.attendance.monetoring.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.LabService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/lab")
public class LabController {

	@Autowired
	LabService labService;
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createLab(@RequestBody LabDto dto, HttpServletRequest request){
		
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
	
	@GetMapping("/get/{labCode}")
	public ResponseEntity<?> editLab(@PathVariable String labCode, HttpServletRequest request){
		
		try {
			Map<String, LabDto> map = new HashMap<>();
			
			LabDto labDto = labService.getLabByLabCode(labCode, request);
			map.put("activeLabs", labDto);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/edit/{labCode}")
	public ResponseEntity<?> updateLab(@PathVariable String labCode, @RequestBody LabDto dto, HttpServletRequest request){
		
		try {
			Map<String, LabDto> map = new HashMap<>();
			
			LabDto labDto = labService.updateLab(labCode, dto, request);
			map.put("labSession", labDto);
			
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
			Map<String, List<LabDto>> map = new HashMap<>();
			
			List<LabDto> labs = labService.getAllLabs(status,request);
			
			map.put("allLabs", labs);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@GetMapping("/active")
	public ResponseEntity<?> getAllActiveLabs(HttpServletRequest request){
		
		try {
			Map<String, List<LabDto>> map = new HashMap<>();
			
			List<LabDto> labs = labService.getAllActiveLabs(request);
			
			map.put("activeLabs", labs);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/inactive")
	public ResponseEntity<?> getAllInActiveLabs(HttpServletRequest request){
		
		try {
			Map<String, List<LabDto>> map = new HashMap<>();
			
			List<LabDto> labs = labService.getAllInActiveLabs(request);
			
			map.put("inactiveLabs", labs);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
