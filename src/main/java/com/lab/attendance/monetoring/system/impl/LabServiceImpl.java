package com.lab.attendance.monetoring.system.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;
import com.lab.attendance.monetoring.system.entities.LabSessionEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.repos.LabSessionRepo;
import com.lab.attendance.monetoring.system.service.LabService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class LabServiceImpl implements LabService {

	@Autowired
	LabRepo labRepo;
	
	@Autowired
	LabSessionRepo labSessionRepo;

	@Autowired
	JwtTokenHelper jwtTokenHelper;

	private static final List<String> ADMIN_ROLES = Arrays.asList("LAB_ADMIN", "ADMIN", "FACULTY", "INSTRUCTOR");

	@Override
	public LabDto createLab(LabDto dto, HttpServletRequest request) {

		String role = jwtTokenHelper.getRoleFromToken(request);

		if (ADMIN_ROLES.contains(role)) {

			validateLab(dto);

			LabEntity entity = toEntity(dto);
			LabEntity savedEntity = labRepo.save(entity);

			return toDto(savedEntity);
		} else {
			throw new CustomException("Students are not allowed to perform this action");
		}
	}
	
	@Transactional
	@Override
	public LabDto updateLab(String labCode, LabDto labDto, HttpServletRequest request) {
		
		String role = jwtTokenHelper.getRoleFromToken(request);

		if (ADMIN_ROLES.contains(role)) {
			
			LabEntity entity = labRepo.findByLabCode(labCode).get();

			validateLab(labDto);

			entity.setLabDescription(labDto.getLabDescription());
			entity.setMaxCapacity(labDto.getMaxCapacity());
			entity.setActive(labDto.isActive());
			
			List<LabSessionEntity> sessionEntity = labSessionRepo.findAllByLabCode(labCode);
			DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
			
			sessionEntity.forEach(labSessionEntity -> {
			    LocalDate sessionDate = labSessionEntity.getLabSessionDate();
			    LocalTime sessionEndTime = LocalTime.parse(labSessionEntity.getEndTime(), TIME_FORMATTER);

			    if (sessionDate.isAfter(LocalDate.now()) || 
			        (sessionDate.isEqual(LocalDate.now()) && sessionEndTime.isAfter(LocalTime.now()))) {
			        labSessionEntity.setSessionStatus(labDto.isActive());
			    }
			});
			
			labSessionRepo.saveAll(sessionEntity);
			
			LabEntity savedEntity = labRepo.save(entity);

			return toDto(savedEntity);
		} else {
			throw new CustomException("Students are not allowed to perform this action");
		}
	}

	private void validateLab(LabDto dto) {
		if (labRepo.labCodeCount(dto.getLabCode()) > 0) {
			throw new CustomException("Lab already exists with code: " + dto.getLabCode());
		}

		if (labRepo.labNameCount(dto.getLabName()) > 0) {
			throw new CustomException("Lab already exists with name: " + dto.getLabName());
		}
	}
	
	@Override
	public List<LabDto> getAllLabs(String status, HttpServletRequest request) {
		
		List<LabEntity> labs = null;
		if (status.equals("all")) {
			labs = labRepo.findAllOrderByStatus();
		} else if(status.equals("active")) {
			labs = labRepo.findAllActiveLabs();
		} else if (status.equals("inactive")) {
			labs = labRepo.findAllInActiveLabs();
		}
		
		return labs.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	@Override
	public List<LabDto> getAllActiveLabs(HttpServletRequest request) {
		
		List<LabEntity> labs = labRepo.findAllActiveLabs();
		
		return labs.stream().map(this::toDto).collect(Collectors.toList());
	}
	
	@Override
	public List<LabDto> getAllInActiveLabs(HttpServletRequest request) {
		
		List<LabEntity> labs = labRepo.findAllInActiveLabs();
		
		return labs.stream().map(this::toDto).collect(Collectors.toList());
	}

	private LabEntity toEntity(LabDto dto) {
		LabEntity entity = new LabEntity();
		
		entity.setLabCode(dto.getLabCode());
		entity.setLabName(dto.getLabName());
		entity.setLabDescription(dto.getLabDescription());
		entity.setMaxCapacity(dto.getMaxCapacity());
		entity.setRegisteredStudents(0);
		entity.setActive(true);

		return entity;
	}

	@Override
	public LabDto toDto(LabEntity entity) {
		LabDto dto = new LabDto();
		dto.setLabCode(entity.getLabCode());
		dto.setLabName(entity.getLabName());
		dto.setLabDescription(entity.getLabDescription());
		dto.setMaxCapacity(entity.getMaxCapacity());
		dto.setRegisteredStudents(entity.getRegisteredStudents());
		dto.setActive(entity.isActive());
		return dto;
	}

	@Override
	public LabDto getLabByLabCode(String labCode, HttpServletRequest request) {

		LabEntity entity = labRepo.findByLabCode(labCode)
				.orElseThrow(() -> new CustomException("Lab Not Found With This LabCode"));

		return toDto(entity);
	}
}