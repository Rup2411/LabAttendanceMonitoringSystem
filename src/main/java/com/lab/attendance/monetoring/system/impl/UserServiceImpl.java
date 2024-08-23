package com.lab.attendance.monetoring.system.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lab.attendance.monetoring.system.dtos.UserDto;
import com.lab.attendance.monetoring.system.entities.UserEntity;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.repos.UserRepo;
import com.lab.attendance.monetoring.system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepo userRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtTokenHelper jwtTokenHelper;

	private static final List<String> VALID_ROLES = Arrays.asList("LAB_ADMIN", "FACULTY", "INSTRUCTOR", "STUDENT");

	@Override
	public UserDto createUser(UserDto dto, MultipartFile image) {

		int email = userRepo.countByEmail(dto.getEmail());

		if (email > 0) {
			throw new CustomException("Account Already Exists With Email : " + dto.getEmail());
		}

		int number = userRepo.countByContactNumber(dto.getMobileNumber());

		if (number > 0) {
			throw new CustomException("Account Already Exists With Contact Number : " + dto.getMobileNumber());
		}

		int rollNo = userRepo.countByRollNo(dto.getRollNo());

		if (rollNo > 0) {
			throw new CustomException("Account Already Exists With Roll Number : " + dto.getRollNo());
		}

		UserEntity entity = toEntity(dto, image);

		UserEntity savedEntity = userRepo.save(entity);

		return toDto(savedEntity);

	}

	private UserEntity toEntity(UserDto dto, MultipartFile image) {

		UserEntity entity = new UserEntity();

		entity.setName(dto.getName());
		entity.setRollNo(dto.getRollNo());
		entity.setDepartment(dto.getDepartment());
		entity.setEmail(dto.getEmail());
		entity.setMobileNumber(dto.getMobileNumber());
		entity.setPassword(encoder.encode(dto.getPassword()));

		if (VALID_ROLES.contains(dto.getRole())) {
			entity.setRole(dto.getRole());
		} else {
			throw new CustomException("Provide a valid Role");
		}

		if (image != null && !image.isEmpty()) {
			try {
				entity.setImage(image.getBytes());
			} catch (IOException e) {
				throw new CustomException("Failed to save image: " + e.getMessage());
			}
		}

		return entity;
	}

	@Override
	public UserDto toDto(UserEntity entity) {

		UserDto dto = new UserDto();

		dto.setName(entity.getName());
		dto.setRollNo(entity.getRollNo());
		dto.setDepartment(entity.getDepartment());
		dto.setEmail(entity.getEmail());
		dto.setMobileNumber(entity.getMobileNumber());
		dto.setPassword(entity.getPassword());
		dto.setRole(entity.getRole());

		return dto;
	}

	@Override
	public UserDto getUserByEmail(String email) {

		UserEntity entity = userRepo.findByEmail(email)
				.orElseThrow(() -> new CustomException("User Not Found With This email"));

		return toDto(entity);
	}

	@Override
	public UserDto getUserByRollNo(String rollNo) {

		UserEntity entity = userRepo.findByRollNo(rollNo)
				.orElseThrow(() -> new CustomException("User Not Found With This Roll No"));

		return toDto(entity);
	}

	@Override
	public byte[] getImageByEmail(String rollNo) {
		return userRepo.findImageByRollNo(rollNo)
				.orElseThrow(() -> new CustomException("No image found for Roll No: " + rollNo));
	}

	@Override
	public List<UserDto> getAllStudentUsers(HttpServletRequest request) {

		String role = jwtTokenHelper.getRoleFromToken(request);

		List<String> ADMIN_ROLES = Arrays.asList("LAB_ADMIN", "FACULTY", "INSTRUCTOR");

		if (ADMIN_ROLES.contains(role)) {
			List<UserEntity> entity = userRepo.getAllStudentUsers();

			List<UserDto> dtos = entity.stream().map(this::toDto).collect(Collectors.toList());

			return dtos;
		}

		else if (role.equals("STUDENT")) {
			throw new CustomException("Students Are Not Allowed to Perform This Action");
		} else {
			return Collections.emptyList();
		}
	}

}
