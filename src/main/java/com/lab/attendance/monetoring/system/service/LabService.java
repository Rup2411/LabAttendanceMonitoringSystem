package com.lab.attendance.monetoring.system.service;

import java.util.List;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface LabService {

	LabDto createLab(LabDto dto, HttpServletRequest request);
	
	LabDto updateLab(String labCode, LabDto dto, HttpServletRequest request);

	LabDto toDto(LabEntity entity);

	List<LabDto> getAllActiveLabs(HttpServletRequest request);
	
	List<LabDto> getAllInActiveLabs(HttpServletRequest request);
	
	LabDto getLabByLabCode(String labCode, HttpServletRequest request);

	List<LabDto> getAllLabs(String status, HttpServletRequest request);

}
