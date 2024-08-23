package com.lab.attendance.monetoring.system.jwtUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDto {

	private String token;

	private String email;

	public static JwtResponseBuilder builder() {
		return new JwtResponseBuilder();
	}

	public static class JwtResponseBuilder {
		private String token;
		private String email;

		public JwtResponseBuilder jwtToken(String token) {
			this.token = token;
			return this;
		}

		public JwtResponseBuilder username(String email) {
			this.email = email;
			return this;
		}

		public JwtResponseDto build() {
			JwtResponseDto response = new JwtResponseDto();
			response.setToken(token);
			response.setEmail(email);
			return response;
		}
	}
}