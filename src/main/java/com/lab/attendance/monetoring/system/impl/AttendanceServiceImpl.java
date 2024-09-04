package com.lab.attendance.monetoring.system.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.dtos.AttendanceRequestDto;
import com.lab.attendance.monetoring.system.entities.AttendanceEntity;
import com.lab.attendance.monetoring.system.entities.FineEntity;
import com.lab.attendance.monetoring.system.entities.LabSessionEntity;
import com.lab.attendance.monetoring.system.entities.StudentAbsenceCount;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.repos.AttendanceRepo;
import com.lab.attendance.monetoring.system.repos.FineRepo;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.repos.LabSessionRepo;
import com.lab.attendance.monetoring.system.repos.StudentAbscenceCountRepo;
import com.lab.attendance.monetoring.system.service.AttendanceService;
import com.lab.attendance.monetoring.system.service.FineService;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	LabRepo labRepo;
	
	@Autowired
	LabSessionRepo labSessionRepo;

	@Autowired
	AttendanceRepo attendanceRepo;
	
	@Autowired
	StudentAbscenceCountRepo abscenceCountRepo;
	
	@Autowired
	FineService fineService;
	
	@Autowired
	FineRepo fineRepo;

	@Override
	public void markAttendance(AttendanceRequestDto dto) {
		
		LabSessionEntity session = labSessionRepo.findByLabSessionId(dto.getLabSessionId())
	            .orElseThrow(() -> new CustomException("Lab session not found with ID: " + dto.getLabSessionId()));

		List<Long> studentRollNos = labSessionRepo.findStudentRollNosByLabSessionId(dto.getLabSessionId());
		
		 for (Long rollNo : dto.getStudentRollNos()) {
		        if (!studentRollNos.contains(rollNo)) {
		            throw new CustomException("Invalid roll number: " + rollNo + " - Student is not enrolled in this lab session.");
		        }
		    }
		
		for(Long rollNo : studentRollNos) {
			
			FineEntity fine = fineRepo.getByStudentRollNo(String.valueOf(rollNo))
					.orElseThrow(() -> new CustomException("Student Not Found For Roll No " + rollNo));
			
			if(fine.isFineActive() == false) {
				
				AttendanceEntity entity = new AttendanceEntity();
				
				entity.setLabSessionId(session.getLabSessionId());
				entity.setStudentRollNo(rollNo);
				if(dto.getStudentRollNos().contains(rollNo) && (session.getLabSessionDate().equals(LocalDate.now()))) {
					entity.setPresent(true);
				}
				else {
					entity.setPresent(false);
					
					StudentAbsenceCount absent = new StudentAbsenceCount();
					
					absent.setStudentRollNo(String.valueOf(entity.getStudentRollNo()));
					absent.setAbsentCount(abscenceCountRepo.absentCount(String.valueOf(entity.getStudentRollNo())) + 1);
					
					StudentAbsenceCount count = abscenceCountRepo.save(absent);
					
					if(count.getAbsentCount() >= 2) {
						fineService.applyFine(String.valueOf(entity.getStudentRollNo()));
					}
					
				}
				attendanceRepo.save(entity);
			}
			
		}
	}

	@Override
	public Map<String, List<Map<String, String>>> getAllAttendanceDetails(String labSessionId) {

		Map<String, List<Map<String, String>>> attendanceDetails = new HashMap<>();

		List<Map<String, String>> presentStudents = attendanceRepo.findAllPresentStudents(labSessionId);
		attendanceDetails.put("presentStudents", presentStudents);

		List<Map<String, String>> absentStudents = attendanceRepo.findAllAbsentStudents(labSessionId);
		attendanceDetails.put("absentStudents", absentStudents);

		return attendanceDetails;
	}

}
