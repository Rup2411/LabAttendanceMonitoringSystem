package com.lab.attendance.monetoring.system.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabSessionDto {

	private String labCode;
	
	private LocalDate labSessionDate;
	
	private String labSessionId;
	
	private String startTime;

	private String endTime;

	private String labDuration;
	
	private String instructorName;

	private String instructorMobileNo;

	private String sessionLocation;
	
	private String sessionType;
	
	private boolean sessionStatus;
}
