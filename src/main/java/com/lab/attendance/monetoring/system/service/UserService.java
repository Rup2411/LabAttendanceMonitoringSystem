package com.lab.attendance.monetoring.system.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lab.attendance.monetoring.system.dtos.UserDto;
import com.lab.attendance.monetoring.system.entities.UserEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	UserDto getUserByEmail(String email);

	byte[] getImageByEmail(String email);

	List<UserDto> getAllStudentUsers(HttpServletRequest request);

	UserDto getUserByRollNo(String rollNo);

	UserDto toDto(UserEntity entity);

	UserDto createUser(UserDto dto, MultipartFile image, HttpServletRequest request) throws UnsupportedEncodingException;
}
