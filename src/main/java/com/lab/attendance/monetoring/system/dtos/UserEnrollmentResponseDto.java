package com.lab.attendance.monetoring.system.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEnrollmentResponseDto {

	private UserDto user;

	private List<LabDto> labs;
}
