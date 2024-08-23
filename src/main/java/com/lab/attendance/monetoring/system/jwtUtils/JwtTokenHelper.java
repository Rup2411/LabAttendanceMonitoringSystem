package com.lab.attendance.monetoring.system.jwtUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.lab.attendance.monetoring.system.exceptions.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenHelper {

	public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

	private String SECRET = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	public String getUserNameFromToken(String token) {

		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromtoken(String token) {

		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

		final Claims claims = getAllClaimsFromToken(token);

		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET).build();
		return parser.parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpired(String token) {

		final Date expiration = getExpirationDateFromtoken(token);

		return expiration.before(new Date());
	}

	public String generateToken(UserDetails userDetails, String role, String email, String contactNumber,
			String rollNo) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("contactNumber", contactNumber);
		claims.put("rollNo", rollNo);
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUserNameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String getRoleFromToken(HttpServletRequest request) throws CustomException {

		String jwtToken = request.getHeader("Authorization");

		jwtToken = jwtToken.substring(7);

		Claims claims = getAllClaimsFromToken(jwtToken);

		String role = claims.get("role", String.class);
		if (role != null && !role.isEmpty()) {
			return role;
		} else {
			throw new CustomException("User Doesn't Have any Role");
		}
	}

	public String getRollNoFromToken(HttpServletRequest request) throws CustomException {

		String jwtToken = request.getHeader("Authorization");

		jwtToken = jwtToken.substring(7);

		Claims claims = getAllClaimsFromToken(jwtToken);

		String rollNo = claims.get("rollNo", String.class);
		if (rollNo != null && !rollNo.isEmpty()) {
			return rollNo;
		} else {
			throw new CustomException("User Doesn't Have any Roll No");
		}
	}

	public String getEmailFromToken(HttpServletRequest request) {

		String jwtToken = request.getHeader("Authorization");

		jwtToken = jwtToken.substring(7);

		Claims claims = getAllClaimsFromToken(jwtToken);

		String email = claims.get("sub", String.class);

		if (email != null && !email.isEmpty()) {
			return email;
		} else {
			throw new CustomException("Email Id Not Found");
		}
	}

	public String getContactNumberFromToken(HttpServletRequest request) {

		String jwtToken = request.getHeader("Authorization");

		jwtToken = jwtToken.substring(7);

		Claims claims = getAllClaimsFromToken(jwtToken);

		String number = claims.get("contactNumber", String.class);

		if (number != null && !number.isEmpty()) {
			return number;
		} else {
			throw new CustomException("Contact Number Not Found");
		}
	}
}
