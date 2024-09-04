package com.lab.attendance.monetoring.system.service;

import java.math.BigDecimal;

import com.lab.attendance.monetoring.system.entities.FineEntity;

public interface FineService {

	void applyFine(String rollNo);

	FineEntity getByStudentRollNo(String rollNo);
	
	String payFine(String rollNo, BigDecimal amount);
}
