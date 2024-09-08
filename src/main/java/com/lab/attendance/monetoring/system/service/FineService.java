package com.lab.attendance.monetoring.system.service;

import java.math.BigDecimal;
import java.util.Map;

import com.lab.attendance.monetoring.system.entities.FineEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface FineService {

	void applyFine(String rollNo);

	FineEntity getByStudentRollNo(String rollNo);
	
	String payFine(String rollNo, BigDecimal amount);

	Map<String, String> allStudentsWithFine(HttpServletRequest request);
}
