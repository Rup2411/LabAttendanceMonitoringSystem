package com.lab.attendance.monetoring.system.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LabSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String labCode;
	
	private LocalDate labSessionDate;
	
	private String labSessionId;
	
	private String startTime;

	private String endTime;

	private long labDuration;
	
	private String instructorName;

	private String instructorMobileNo;

	private String sessionLocation;
	
	private String sessionType;
	
	private boolean sessionStatus;
}
