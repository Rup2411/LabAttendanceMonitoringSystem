package com.lab.attendance.monetoring.system.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.entities.FineEntity;
import com.lab.attendance.monetoring.system.entities.StudentAbsenceCount;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.FineRepo;
import com.lab.attendance.monetoring.system.repos.StudentAbscenceCountRepo;
import com.lab.attendance.monetoring.system.service.FineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class FineServiceImpl implements FineService {

	@Autowired
	FineRepo fineRepo;
	
	@Autowired
	StudentAbscenceCountRepo abscenceCountRepo;
	
	@Autowired
	JwtTokenHelper jwtTokenHelper;
	
	
	@Override
	public void applyFine(String rollNo) {
		
		FineEntity entity = new FineEntity();
		
		entity.setStudentRollNo(rollNo);
		entity.setFineAmount(BigDecimal.valueOf(50));
		entity.setFineDate(LocalDateTime.now());
		entity.setFinePaymentStatus("Pending");
		entity.setFineActive(true);
		
		fineRepo.save(entity);
		
	}
	
	@Override
	public FineEntity getByStudentRollNo(String rollNo) {
		
		FineEntity entity = fineRepo.getByStudentRollNo(rollNo)
				.orElseThrow(() -> new CustomException("Student Not Found For Roll No " + rollNo));
		
		return entity;
	}

	@Transactional
	@Override
	public String payFine(String rollNo, BigDecimal amount) {
		
		try {
			FineEntity entity = fineRepo.getByStudentRollNo(rollNo)
					.orElseThrow(() -> new CustomException("Student Not Found For Roll No " + rollNo));
			
			StudentAbsenceCount absenceCount = abscenceCountRepo.absentCountByRollNo(rollNo)
					.orElseThrow(() -> new CustomException("Absent Student Not Found For Roll No " + rollNo));
			
			if(entity.getFineAmount() == amount) {
				
				entity.setFinePaymentDate(LocalDateTime.now());
				entity.setFinePaymentStatus("Success");
				entity.setFineActive(false);
				absenceCount.setAbsentCount(0);
			}
			else if (amount.compareTo(entity.getFineAmount()) < 0) {
				throw new CustomException("Payment Amount Cannot be less than Fine Amount");
			}
			
			else if (amount.compareTo(entity.getFineAmount()) > 0) {
				throw new CustomException("Payment Amount Cannot be more than Fine Amount");
			}
			
			fineRepo.save(entity);
			abscenceCountRepo.save(absenceCount);
			
			return "Fine Paid, Student Eligible to Attend Next Sessions";
		} catch (CustomException e) {
	        throw new CustomException("An unexpected error occurred while processing the payment : " + e.getMessage());
	    }
		
	}
	
	@Override
	public Map<String, String> allStudentsWithFine(HttpServletRequest request){
		
		String role = jwtTokenHelper.getRoleFromToken(request);
		
		if (role.equals("ADMIN") || role.equals("LAB_ADMIN")) {
			
			Map<String, String> students = abscenceCountRepo.finaAllStudentsEligibleForFine();
			
			return students;
		}
		else {
			throw new CustomException("Students are not allowed to perform this action.");
		}
	}

	
}
