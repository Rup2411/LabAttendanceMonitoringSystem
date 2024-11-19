package com.lab.attendance.monetoring.system.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEnrollmentDto {

	private Set<String> rollNos;

	private Set<String> labCodes;
}
