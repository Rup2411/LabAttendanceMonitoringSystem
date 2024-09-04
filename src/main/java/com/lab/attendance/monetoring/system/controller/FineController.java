package com.lab.attendance.monetoring.system.controller;

import java.math.BigDecimal;
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

import com.lab.attendance.monetoring.system.dtos.UserDto;
import com.lab.attendance.monetoring.system.entities.FineEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.service.FineService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/fine")
public class FineController {

	@Autowired
	FineService fineService;
	
	@PostMapping("/pay")
	public ResponseEntity<?> payFine(@RequestParam String rollNo, @RequestParam BigDecimal amount, HttpServletRequest request){
		
		try {
			Map<String, String> map = new HashMap<>();
			
			String payment = fineService.payFine(rollNo, amount);
			map.put("payment", payment);
			
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/get/{rollNo}")
	public ResponseEntity<?> getStudentFineDetails(@PathVariable String rollNo, HttpServletRequest request) {

		try {
			Map<String, FineEntity> map = new HashMap<>();

			FineEntity fineDetails = fineService.getByStudentRollNo(rollNo);

			map.put("students", fineDetails);

			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
