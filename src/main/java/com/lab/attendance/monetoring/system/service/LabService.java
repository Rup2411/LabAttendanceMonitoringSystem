package com.lab.attendance.monetoring.system.service;

import com.lab.attendance.monetoring.system.dtos.LabDto;
import com.lab.attendance.monetoring.system.entities.LabEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface LabService {

	LabDto createLab(LabDto dto, HttpServletRequest request);

	LabDto getLabByLabCode(String labCode);

	LabDto toDto(LabEntity entity);
}
