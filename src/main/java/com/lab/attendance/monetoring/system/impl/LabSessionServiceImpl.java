package com.lab.attendance.monetoring.system.impl;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.dtos.LabSessionDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;
import com.lab.attendance.monetoring.system.entities.LabSessionEntity;
import com.lab.attendance.monetoring.system.entities.UserEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.repos.LabSessionRepo;
import com.lab.attendance.monetoring.system.repos.UserRepo;
import com.lab.attendance.monetoring.system.service.EmailService;
import com.lab.attendance.monetoring.system.service.LabSessionService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LabSessionServiceImpl implements LabSessionService {

	
	@Autowired
	LabRepo labRepo;
	
	@Autowired
	LabSessionRepo labSessionRepo;
	
	@Autowired
	JwtTokenHelper jwtTokenHelper;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	EmailService emailService;
	
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
	private static final List<String> VALID_SESSION_TYPES = Arrays.asList("PRACTICAL", "TUTORIAL", "WORKSHOP");
	private static final List<String> ADMIN_ROLES = Arrays.asList("LAB_ADMIN", "ADMIN", "FACULTY", "INSTRUCTOR");
	
	@Override
	public LabSessionDto createSession(LabSessionDto dto, HttpServletRequest request) {
		
		String role = jwtTokenHelper.getRoleFromToken(request);
		
		if(ADMIN_ROLES.contains(role)) {
			LabSessionEntity collision = getCollidingSession(dto);

			if (collision != null) {
				throw new CustomException("The lab session collides with an existing session: " + "Lab Code: "
						+ collision.getLabCode() + ", Date: " + collision.getLabSessionDate() + ", Start Time: "
						+ collision.getStartTime() + ", End Time: " + collision.getEndTime() + ", Instructor: "
						+ collision.getInstructorName() + ", Instructor Contact Number: "
						+ collision.getInstructorMobileNo());
			}

			if (!isInstructorAvailable(dto)) {
				throw new CustomException(
						"The instructor is already assigned to another session at this time on the same date.");
			}
			LabEntity labEntity = labRepo.findByLabCode(dto.getLabCode())
					.orElseThrow(() -> new CustomException("Lab Not Found With This LabCode"));
			
			LabSessionEntity entity = toEntity(dto);
			entity.setLabCode(labEntity.getLabCode());
			labEntity.getSessions().add(entity.getLabSessionId());
			
			labRepo.save(labEntity);
			
			LabSessionEntity savedSession = labSessionRepo.save(entity);
			
	        Set<String> rollNos = labEntity.getEnrolledStudentsRollNo();
	        
	        for (String rollNo : rollNos) {
	        	
	        	UserEntity student = userRepo.findByRollNo(rollNo).orElseThrow(null);
	        	
	        	String subject = "New Lab Session Created";
		        String body = "Dear " + student.getName() + ",\n\nA new lab session has been created for the lab " + labEntity.getLabName() + ".\n" +
		                              "Session Date: " + dto.getLabSessionDate() +"\nStart Time: " + dto.getStartTime() +"\nEnd Time: " + dto.getEndTime() +"\n\nPlease make sure to attend.\n\n" +
		                              "Best regards,\nBBIT Lab Team";
	            
	            emailService.sendEmail(List.of(student.getEmail()), subject, body);
	        }
	        
			
			return toDto(savedSession);
		} else {
			throw new CustomException("Students are not allowed to perform this action");
		}
	}
	
	
	@Override
	public LabSessionDto updateSession(String labSessionId, LabSessionDto dto, HttpServletRequest request) {
		
		String role = jwtTokenHelper.getRoleFromToken(request);
		
		if(ADMIN_ROLES.contains(role)) {
			LabSessionEntity collision = getCollidingSession(dto);

			if (collision != null) {
				throw new CustomException("The lab session collides with an existing session: " + "Lab Code: "
						+ collision.getLabCode() + ", Date: " + collision.getLabSessionDate() + ", Start Time: "
						+ collision.getStartTime() + ", End Time: " + collision.getEndTime() + ", Instructor: "
						+ collision.getInstructorName() + ", Instructor Contact Number: "
						+ collision.getInstructorMobileNo());
			}

			if (!isInstructorAvailable(dto)) {
				throw new CustomException(
						"The instructor is already assigned to another session at this time on the same date.");
			}
			LabEntity labEntity = labRepo.findByLabCode(dto.getLabCode())
					.orElseThrow(() -> new CustomException("Lab Not Found With This LabCode"));
			
			LabSessionEntity entity = updateEntity(labSessionId, dto);
			
			if (!(entity.getLabSessionId().equals(dto.getLabSessionId()))) {
				throw new CustomException("Session Id Cannot be Changed");
			}
			
			if (!(entity.getLabCode().equals(dto.getLabCode()))) {
				throw new CustomException("Lab Cannot be Changed");
			}
			
			entity.setLabCode(labEntity.getLabCode());
			labEntity.getSessions().add(entity.getLabSessionId());
			
			labRepo.save(labEntity);
			
			LabSessionEntity savedSession = labSessionRepo.save(entity);
			
	        Set<String> rollNos = labEntity.getEnrolledStudentsRollNo();
	        
	        for (String rollNo : rollNos) {
	        	
	        	UserEntity student = userRepo.findByRollNo(rollNo).orElseThrow(null);
	        	
	        	String subject = "Lab Session Update";
		        String body = "Dear " + student.getName() + ",\n\n lab session "+ labSessionId +" has been created for the lab " + labEntity.getLabName() + ".\n" +
		                              "Session Date: " + dto.getLabSessionDate() +"\nStart Time: " + dto.getStartTime() +"\nEnd Time: " + dto.getEndTime() +"\n\nPlease make sure to attend.\n\n" +
		                              "Best regards,\nBBIT Lab Team";
	            
	            emailService.sendEmail(List.of(student.getEmail()), subject, body);
	        }
	        
			
			return toDto(savedSession);
		} else {
			throw new CustomException("Students are not allowed to perform this action");
		}
	}
	
	private LabSessionEntity updateEntity(String labSessionId, LabSessionDto dto) {
		
		LabSessionEntity entity = labSessionRepo.findByLabSessionId(labSessionId).orElseThrow(() -> new CustomException("Session Does Not Exist"));
		
		entity.setSessionLocation(dto.getSessionLocation());
		entity.setLabSessionDate(dto.getLabSessionDate());

		LocalTime startTime = parseTime(dto.getStartTime());
		LocalTime endTime = parseTime(dto.getEndTime());

		if (startTime != null && endTime != null) {
			if (endTime.isBefore(startTime)) {
				throw new CustomException("End time cannot be before start time.");
			}
			entity.setLabDuration(Duration.between(startTime, endTime).toNanos());
		} else {
			entity.setLabDuration(0);
		}

		if (!VALID_SESSION_TYPES.contains(dto.getSessionType())) {
			throw new CustomException("Invalid session type: " + dto.getSessionType());
		}
		entity.setSessionType(dto.getSessionType());

		entity.setInstructorName(dto.getInstructorName());
		entity.setInstructorMobileNo(dto.getInstructorMobileNo());
		entity.setStartTime(formatTime(startTime));
		entity.setEndTime(formatTime(endTime));
		entity.setSessionStatus(dto.isSessionStatus());
		
		return entity;
	}
	
	private LabSessionEntity toEntity(LabSessionDto dto) {
		
		LabSessionEntity entity = new LabSessionEntity();
		
		entity.setSessionLocation(dto.getSessionLocation());
		entity.setLabSessionDate(dto.getLabSessionDate());

		String sessionId = generateSessionId(dto);
		entity.setLabSessionId(sessionId);
		;

		LocalTime startTime = parseTime(dto.getStartTime());
		LocalTime endTime = parseTime(dto.getEndTime());

		if (startTime != null && endTime != null) {
			if (endTime.isBefore(startTime)) {
				throw new CustomException("End time cannot be before start time.");
			}
			entity.setLabDuration(Duration.between(startTime, endTime).toNanos());
		} else {
			entity.setLabDuration(0);
		}

		if (!VALID_SESSION_TYPES.contains(dto.getSessionType())) {
			throw new CustomException("Invalid session type: " + dto.getSessionType());
		}
		entity.setSessionType(dto.getSessionType());

		entity.setInstructorName(dto.getInstructorName());
		entity.setInstructorMobileNo(dto.getInstructorMobileNo());
		entity.setStartTime(formatTime(startTime));
		entity.setEndTime(formatTime(endTime));
		entity.setSessionStatus(true);
		
		return entity;
		
	}
	
	private LabSessionDto toDto(LabSessionEntity entity) {
		
		LabSessionDto dto = new LabSessionDto();
		
		dto.setLabCode(entity.getLabCode());
		dto.setSessionLocation(entity.getSessionLocation());
		dto.setLabSessionDate(entity.getLabSessionDate());
		dto.setLabSessionId(entity.getLabSessionId());
		dto.setSessionType(entity.getSessionType());
		dto.setStartTime(entity.getStartTime());
		dto.setEndTime(entity.getEndTime());
		dto.setLabDuration(formatDuration(Duration.ofNanos(entity.getLabDuration())));
		dto.setInstructorName(entity.getInstructorName());
		dto.setInstructorMobileNo(entity.getInstructorMobileNo());
		dto.setSessionStatus(entity.isSessionStatus());
		
		return dto;
	}

	private String generateSessionId(LabSessionDto dto) {

		String labCode = dto.getLabCode();
		String sessionDate = dto.getLabSessionDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		long currentTimeMillis = System.currentTimeMillis() % 1000000;

		return labCode + sessionDate + currentTimeMillis;
	}

	private LocalTime parseTime(String timeStr) {
		if (timeStr != null && !timeStr.isEmpty()) {
			try {
				return LocalTime.parse(timeStr, TIME_FORMATTER);
			} catch (DateTimeParseException e) {
				throw new CustomException("Invalid time format: " + timeStr + e);
			}
		}
		return null;
	}

	private String formatTime(LocalTime time) {
		return time != null ? time.format(TIME_FORMATTER) : null;
	}

	private String formatDuration(Duration duration) {
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		StringBuilder sb = new StringBuilder();
		if (hours > 0) {
			sb.append(hours).append(" hour");
			if (hours > 1)
				sb.append("s");
		}
		if (minutes > 0) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(minutes).append(" minute");
			if (minutes > 1)
				sb.append("s");
		}
		return sb.toString();
	}
	
	private LabSessionEntity getCollidingSession(LabSessionDto dto) {
		List<LabSessionEntity> sessions = labSessionRepo.findActiveSessionsByLabLocationAndDate(dto.getSessionLocation(),
				dto.getLabSessionDate());

		LocalTime dtoStartTime = parseTime(dto.getStartTime());
		LocalTime dtoEndTime = parseTime(dto.getEndTime());

		for (LabSessionEntity entity : sessions) {
			LocalTime entityStartTime = parseTime(entity.getStartTime());
			LocalTime entityEndTime = parseTime(entity.getEndTime());

			boolean isOverlapping = dtoStartTime != null && dtoEndTime != null
					&& (dtoStartTime.isBefore(entityEndTime) && dtoEndTime.isAfter(entityStartTime)
							|| dtoStartTime.equals(entityStartTime) && dtoEndTime.equals(entityEndTime)
							|| dtoStartTime.isBefore(entityStartTime) && dtoEndTime.isAfter(entityEndTime)
							|| dtoStartTime.isAfter(entityStartTime) && dtoEndTime.isBefore(entityEndTime));

			if (isOverlapping) {
				return entity;
			}
		}
		return null;
	}

	private boolean isInstructorAvailable(LabSessionDto dto) {
		List<LabSessionEntity> sessions = labSessionRepo.findActiveSessionsByInstructorAndDate(dto.getInstructorName(),
				dto.getLabSessionDate());

		LocalTime startTime = parseTime(dto.getStartTime());
		LocalTime endTime = parseTime(dto.getEndTime());

		for (LabSessionEntity session : sessions) {
			LocalTime sessionStartTime = parseTime(session.getStartTime());
			LocalTime sessionEndTime = parseTime(session.getEndTime());

			boolean isOverlapping = startTime != null && endTime != null
					&& (startTime.isBefore(sessionEndTime) && endTime.isAfter(sessionStartTime)
							|| startTime.equals(sessionStartTime) && endTime.equals(sessionEndTime)
							|| startTime.isBefore(sessionStartTime) && endTime.isAfter(sessionEndTime)
							|| startTime.isAfter(sessionStartTime) && endTime.isBefore(sessionEndTime));

			if (isOverlapping) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public LabSessionDto getSessionBySessionId(String sessionId, HttpServletRequest request) {
		
		LabSessionEntity entity = labSessionRepo.findByLabSessionId(sessionId)
				.orElseThrow(() -> new CustomException("Session Not Found For Session ID : " + sessionId));
		
		return toDto(entity);
	}
	
	@Override
	public List<LabSessionDto> findAllSessionByLabCode(String labCode, HttpServletRequest request){
		
		List<LabSessionEntity> allSessions = labSessionRepo.findAllByLabCode(labCode);
		
		return allSessions.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	@Override
	public List<LabSessionDto> getAllLabSessions(HttpServletRequest request){
		
		List<LabSessionEntity> entity = labSessionRepo.findAllOrderByStatus();
		
		return entity.stream().map(this::toDto).collect(Collectors.toList());
	}


}
