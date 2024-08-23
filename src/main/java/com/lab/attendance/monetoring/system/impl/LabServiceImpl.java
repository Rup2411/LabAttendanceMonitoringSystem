package com.lab.attendance.monetoring.system.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.LabRepo;
import com.lab.attendance.monetoring.system.service.LabService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LabServiceImpl implements LabService {

	@Autowired
	LabRepo labRepo;

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

	private void validateLab(LabDto dto) {
		if (labRepo.labCodeCount(dto.getLabCode()) > 0) {
			throw new CustomException("Lab already exists with code: " + dto.getLabCode());
		}

		if (labRepo.labNameCount(dto.getLabName()) > 0) {
			throw new CustomException("Lab already exists with name: " + dto.getLabName());
		}
	}

	private LabEntity toEntity(LabDto dto) {
		LabEntity entity = new LabEntity();
		
		entity.setLabCode(dto.getLabCode());
		entity.setLabName(dto.getLabName());
		entity.setLabDescription(dto.getLabDescription());
		entity.setMaxCapacity(dto.getMaxCapacity());
		entity.setRegisteredStudents(0);
		entity.setActive(dto.isActive());

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
	public LabDto getLabByLabCode(String labCode) {

		LabEntity entity = labRepo.findByLabCode(labCode)
				.orElseThrow(() -> new CustomException("Lab Not Found With This LabCode"));

		return toDto(entity);
	}
}