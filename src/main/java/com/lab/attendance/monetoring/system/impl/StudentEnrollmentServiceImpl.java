package com.lab.attendance.monetoring.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.dtos.LabEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.dtos.UserDto;
import com.lab.attendance.monetoring.system.dtos.UserEnrollmentResponseDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;
import com.lab.attendance.monetoring.system.entities.UserEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.repos.LabSessionRepo;
import com.lab.attendance.monetoring.system.repos.UserRepo;
import com.lab.attendance.monetoring.system.service.LabService;
import com.lab.attendance.monetoring.system.service.StudentEnrollmentService;
import com.lab.attendance.monetoring.system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

	@Autowired
	LabService labService;

	@Autowired
	LabRepo labRepo;

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	LabSessionRepo labSessionRepo;

	@Autowired
	UserService userService;

	@Autowired
	JwtTokenHelper jwtTokenHelper;

	@Override
	public List<Map<String, Map<String, String>>> enrollStudentsinLab(String rollNo, Set<String> labCodes,
			HttpServletRequest request) {

		Map<String, String> enrolledLabs = new HashMap<>();

		Map<String, String> nonEnrolledLabs = new HashMap<>();

		String role = jwtTokenHelper.getRoleFromToken(request);
		String requestRollNo = jwtTokenHelper.getRollNoFromToken(request);

		if (role.equals("STUDENT") && !requestRollNo.equals(rollNo)) {
			throw new CustomException("Students can only enroll themselves.");
		}

		UserEntity entity = userRepo.findByRollNo(rollNo)
				.orElseThrow(() -> new CustomException("Student Not Found For Roll No: " + rollNo));

		for (String labCode : labCodes) {

			try {
				LabEntity labEntity = labRepo.findByLabCode(labCode)
						.orElseThrow(() -> new CustomException("Lab Not Found For Lab Code: " + labCode));

				if (!entity.getEnrolledLabCodes().contains(labCode)) {

					entity.getEnrolledLabCodes().add(labCode);

					enrolledLabs.put(labCode, "Enrolled");
				} else {

					nonEnrolledLabs.put(labCode, "Already Enrolled");
				}
				
				if(labEntity.getMaxCapacity() == labEntity.getRegisteredStudents()) {
					throw new CustomException("Lab Is Full Kindly Contact Lab Admin");
				}

				if (!labEntity.getEnrolledStudentsRollNo().contains(rollNo)) {

					labEntity.getEnrolledStudentsRollNo().add(rollNo);

					labEntity.setRegisteredStudents(labEntity.getRegisteredStudents() + 1);
				}

				userRepo.save(entity);

				labRepo.save(labEntity);
			} catch (CustomException e) {
				nonEnrolledLabs.put(labCode, e.getMessage());
			}
		}

		List<Map<String, Map<String, String>>> result = new ArrayList<>();
		Map<String, Map<String, String>> enrolledMap = new HashMap<>();
		enrolledMap.put("enrolled", enrolledLabs);

		Map<String, Map<String, String>> nonEnrolledMap = new HashMap<>();
		nonEnrolledMap.put("notEnrolled", nonEnrolledLabs);

		result.add(enrolledMap);
		result.add(nonEnrolledMap);

		return result;
	}

	@Override
	public UserEnrollmentResponseDto getAllLabsEnrolledByStudent(String rollNo) {

		UserEntity entity = userRepo.findByRollNo(rollNo)
				.orElseThrow(() -> new CustomException("Student Not Found For Email : " + rollNo));

		Set<String> enrolledSubjectCodes = entity.getEnrolledLabCodes();

		List<LabEntity> enrolledSubjects = labRepo.findByLabIn(enrolledSubjectCodes);

		UserDto userDto = userService.toDto(entity);

		List<LabDto> labDto = enrolledSubjects.stream().map(labService::toDto).collect(Collectors.toList());

		return new UserEnrollmentResponseDto(userDto, labDto);
	}

	@Override
	public List<LabEnrollmentResponseDto> getStudentsEnrolledInLabs(String labCode,
			HttpServletRequest request) {

		String role = jwtTokenHelper.getRoleFromToken(request);

		if (role.equals("ADMIN") || role.equals("LAB_ADMIN")) {

			List<LabEntity> labs = labRepo.findAllByLabCode(labCode);

			if (labs.isEmpty()) {
				throw new CustomException("No labs found with Lab Code: " + labCode);
			}

			List<LabEnrollmentResponseDto> responseDtos = labs.stream().map(lab -> {
				Set<String> enrolledStudentEmails = lab.getEnrolledStudentsRollNo();
				List<UserEntity> enrolledUsers = userRepo.findByRollNoIn(enrolledStudentEmails);
				List<UserDto> enrolledUsersDto = enrolledUsers.stream().map(userService::toDto)
						.collect(Collectors.toList());
				LabDto labDto = labService.toDto(lab);
				return new LabEnrollmentResponseDto(labDto, enrolledUsersDto);
			}).collect(Collectors.toList());

			return responseDtos;
		} 
//			else if (role.equals("FACULTY") || role.equals("INSTRUCTOR")) {
//
//			LabSessionEntity lab = labSessionRepo.findByLabSessionId(labSessionId)
//					.orElseThrow(() -> new CustomException("Session Not Found With Session ID: " + labSessionId));
//			
//			LabEntity labEntity = labRepo.findByLabSessionId(labSessionId)
//					.orElseThrow(() -> new CustomException("Lab Not Found With Session ID: " + labSessionId));
//
//			Set<String> enrolledStudentEmails = labEntity.getEnrolledStudentsRollNo();
//			List<UserEntity> enrolledUsers = userRepo.findByRollNoIn(enrolledStudentEmails);
//			List<UserDto> enrolledUsersDto = enrolledUsers.stream().map(userService::toDto)
//					.collect(Collectors.toList());
//
//			LabDto labDto = labService.toDto(labEntity);
//
//			return Collections.singletonList(new LabEnrollmentResponseDto(labDto, enrolledUsersDto));
//		} 
			else {
			throw new CustomException("Students Are Not Allowed to Perform This Action");
		}
	}

}
