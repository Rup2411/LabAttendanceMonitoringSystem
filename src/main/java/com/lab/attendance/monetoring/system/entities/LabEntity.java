package com.lab.attendance.monetoring.system.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LabEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String labCode;

	private String labName;

	private long maxCapacity;

	private long registeredStudents;

	private String labDescription;

	private boolean isActive;
	
	@ElementCollection
	@Column(name = "session_id")
	private Set<String> sessions = new HashSet<>();

	@ElementCollection
	@Column(name = "enrolled_students")
	Set<String> enrolledStudentsRollNo = new HashSet<>();

	@Version
	private int version;
}
