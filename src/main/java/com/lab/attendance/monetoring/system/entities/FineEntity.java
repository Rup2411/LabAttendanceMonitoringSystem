package com.lab.attendance.monetoring.system.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class FineEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long id;
	
	private String studentRollNo;
	
	private BigDecimal fineAmount;
	
	private LocalDateTime fineDate;
	
	@JsonIgnore
	private LocalDateTime finePaymentDate;
	
	private String finePaymentStatus;
	
	private boolean isFineActive;
}
