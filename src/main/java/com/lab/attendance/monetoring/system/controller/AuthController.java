package com.lab.attendance.monetoring.system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lab.attendance.monetoring.system.dtos.UserDto;
import com.lab.attendance.monetoring.system.exceptions.CustomException;
import com.lab.attendance.monetoring.system.jwtUtils.JwtRequestDto;
import com.lab.attendance.monetoring.system.jwtUtils.JwtResponseDto;
import com.lab.attendance.monetoring.system.jwtUtils.JwtTokenHelper;
import com.lab.attendance.monetoring.system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class AuthController {

	@Autowired
	UserService userService;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	JwtTokenHelper jwtTokenHelper;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@GetMapping("/")
	public String redirectToLogin() {
	    return "redirect:/api/auth/login";
	}

	
	@GetMapping("/api/auth/login")
    public String loginPage() {
        return "login"; // This will resolve to src/main/resources/templates/login.html
    }
	
	@GetMapping("/api/auth/register")
    public String registerPage() {
        return "register"; // This will resolve to src/main/resources/templates/login.html
    }

    // Serve the dashboard page
    @GetMapping("/api/auth/dashboard")
    public String dashboard() {
        return "dashboard"; // This will resolve to src/main/resources/templates/dashboard.html
    }
    

	@PostMapping("/api/auth/register")
	public ResponseEntity<?> registerUser(@ModelAttribute UserDto dto,
			@RequestParam(required = false) MultipartFile image, HttpServletRequest request) {

		try {
			UserDto userDto = userService.createUser(dto, image, request);

			Map<String, UserDto> response = new HashMap<>();

			if ("STUDENT".equals(dto.getRole())) {
				response.put("Student", userDto);
			} else if ("LAB_ADMIN".equals(dto.getRole())) {
				response.put("Lab Admin", userDto);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (CustomException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "An error occurred while processing the image.");
			System.out.println(e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/api/auth/login")
	public ResponseEntity<?> login(@RequestBody JwtRequestDto dto) {
		this.doAuthenticate(dto.getEmail(), dto.getPassword());

		UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

		UserDto entity = userService.getUserByEmail(dto.getEmail());

		String token = jwtTokenHelper.generateToken(userDetails, entity.getRole(), userDetails.getUsername(),
				entity.getMobileNumber(), String.valueOf(entity.getRollNo()));

		JwtResponseDto response = JwtResponseDto.builder().jwtToken(token).username(userDetails.getUsername()).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private void doAuthenticate(String email, String password) throws BadCredentialsException {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			authenticationManager.authenticate(authentication);
		} catch (BadCredentialsException | UsernameNotFoundException e) {
			throw new BadCredentialsException("Invalid Username or Password");
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Credentials");
		}
	}
}
