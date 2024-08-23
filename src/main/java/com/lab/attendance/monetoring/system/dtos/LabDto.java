package com.lab.attendance.monetoring.system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabDto {

	private String labCode;

	private String labName;

	private long maxCapacity;

	private long registeredStudents;

	private String labDescription;

	private boolean isActive;

}
