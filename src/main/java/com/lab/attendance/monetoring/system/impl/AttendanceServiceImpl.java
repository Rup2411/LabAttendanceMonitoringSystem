package com.lab.attendance.monetoring.system.impl;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.lab.attendance.monetoring.system.entities.UserEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.repos.AttendanceRepo;
import com.lab.attendance.monetoring.system.repos.FineRepo;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.repos.LabSessionRepo;
import com.lab.attendance.monetoring.system.repos.StudentAbscenceCountRepo;
import com.lab.attendance.monetoring.system.repos.UserRepo;
import com.lab.attendance.monetoring.system.service.AttendanceService;
import com.lab.attendance.monetoring.system.service.EmailService;
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
	UserRepo userRepo;
	
	@Autowired
	FineService fineService;
	
	@Autowired
	FineRepo fineRepo;
	
	@Autowired
	EmailService emailService;

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

	    List<Map<String, String>> presentStudents = new ArrayList<>();
	    List<Map<String, String>> absentStudents = new ArrayList<>();

	    for (Long rollNo : studentRollNos) {
	        UserEntity user = userRepo.findByRollNo(String.valueOf(rollNo))
	                .orElseThrow(() -> new CustomException("Student Not Found For Roll No " + rollNo));

	        FineEntity fine = fineRepo.getByStudentRollNo(String.valueOf(rollNo))
	                .orElse(new FineEntity()); // If no fine is found, create a new FineEntity

	        // Check if fine is active and if not, continue processing attendance
	        if (fine.getFineAmount() != null && fine.isFineActive()) {
	            AttendanceEntity entity = new AttendanceEntity();
	            entity.setLabSessionId(session.getLabSessionId());
	            entity.setStudentRollNo(rollNo);

	            if (dto.getStudentRollNos().contains(rollNo) && session.getLabSessionDate().equals(LocalDate.now())) {
	                entity.setPresent(true);

	                Map<String, String> studentInfo = new HashMap<>();
	                studentInfo.put("name", user.getName());
	                studentInfo.put("email", user.getEmail());
	                presentStudents.add(studentInfo);

	            } else {
	                entity.setPresent(false);

	                // Increment absence count
	                StudentAbsenceCount absent = new StudentAbsenceCount();
	                absent.setStudentRollNo(String.valueOf(entity.getStudentRollNo()));
	                absent.setAbsentCount(abscenceCountRepo.absentCount(String.valueOf(entity.getStudentRollNo())) + 1);

	                StudentAbsenceCount count = abscenceCountRepo.save(absent);

	                if (count.getAbsentCount() >= 2) {
	                    fineService.applyFine(String.valueOf(entity.getStudentRollNo()));

	                    String fineRollNo = count.getStudentRollNo();
	                    UserEntity finedStudent = userRepo.findByRollNo(fineRollNo)
	                            .orElseThrow(() -> new CustomException("Fined student not found"));

	                    String fineEmail = finedStudent.getEmail();

	                    String fineSubject = "Fine Applied Due to Absences";
	                    String fineBody = "Dear " + finedStudent.getName() +",\n\n" +
	                            "You have been absent for two or more sessions in the lab, and a fine has been applied to your account.\n" +
	                            "Please ensure to pay the fine to continue attending future lab sessions.\n\n" +
	                            "Best regards,\nBBIT Lab Team";

	                    emailService.sendEmail(List.of(fineEmail), fineSubject, fineBody);
	                }

	                Map<String, String> studentInfo = new HashMap<>();
	                studentInfo.put("name", user.getName());
	                studentInfo.put("email", user.getEmail());
	                absentStudents.add(studentInfo);
	            }
	            attendanceRepo.save(entity);
	        }
	    }

	    // Send attendance emails for present students
	    for (Map<String, String> presentStudent : presentStudents) {
	        String subject = "Attendance Confirmation";
	        String body = "Dear " + presentStudent.get("name") + ",\n\n" +
	                "You have been marked as present for the lab session on " + session.getLabSessionDate() + ".\n\n" +
	                "Best regards,\nBBIT Lab Team";
	        emailService.sendEmail(List.of(presentStudent.get("email")), subject, body);
	    }

	    // Send attendance alert emails for absent students
	    for (Map<String, String> absentStudent : absentStudents) {
	        String subject = "Attendance Alert";
	        String body = "Dear " + absentStudent.get("name") + ",\n\n" +
	                "You have been marked as absent for the lab session on " + session.getLabSessionDate() + ".\n\n" +
	                "Please ensure to attend future sessions.\n\n" +
	                "Best regards,\nBBIT Lab Team";
	        emailService.sendEmail(List.of(absentStudent.get("email")), subject, body);

	        String parentEmail = userRepo.getParentEmailByStudentEmail(absentStudent.get("email"));

	        if (parentEmail != null) {
	            String parentSubject = "Attendance Alert for Your Child";
	            String parentBody = "Dear Parent,\n\n" +
	                    absentStudent.get("name") + " has been marked as absent for the lab session on " + session.getLabSessionDate() + ".\n" +
	                    "Please encourage them to attend future sessions.\n\n" +
	                    "Best regards,\nBBIT Lab Team";
	            emailService.sendEmail(List.of(parentEmail), parentSubject, parentBody);
	        }

	        String localGuardianEmail = userRepo.getLocalGuardianEmailByStudentEmail(absentStudent.get("email"));
	        if (localGuardianEmail != null) {
	            String guardianSubject = "Attendance Alert for Your Ward";
	            String guardianBody = "Dear Guardian,\n\n" +
	                    absentStudent.get("name") + " has been marked as absent for the lab session on " + session.getLabSessionDate() + ".\n" +
	                    "Please encourage them to attend future sessions.\n\n" +
	                    "Best regards,\nBBIT Lab Team";
	            emailService.sendEmail(List.of(localGuardianEmail), guardianSubject, guardianBody);
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
