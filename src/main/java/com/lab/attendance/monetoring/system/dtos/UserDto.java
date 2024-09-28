package com.lab.attendance.monetoring.system.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private String name;

	private long rollNo;

	private String department;

	private MultipartFile image;

	private String mobileNumber;

	private String email;
	
	private String parentEmail;
	
	private String localGuardianEmail;

	private String password;

	private String role;
}
