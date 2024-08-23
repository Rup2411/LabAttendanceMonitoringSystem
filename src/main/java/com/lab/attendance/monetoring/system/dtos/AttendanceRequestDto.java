package com.lab.attendance.monetoring.system.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDto {

	private String labSessionId;

	private List<Long> studentRollNos;

}
