package com.chatbot.util;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

	private String SECRET_KEY = "r+SksfAD7+8Hkfrk49wlf9l7zRkgqC0+hB6pfHEeXtM=";

	private int jwtExpirationMs = 300000; // 10 min

	public String generateToken(String userName) {
		Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS512.getJcaName());

		return Jwts.builder().setSubject(userName).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key).compact();
	}

	public String getUsernameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()) // Ensure you use the same secret to validate
				.build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()) // Ensure you use the same secret to validate
					.build().parseClaimsJws(authToken); // Validate the token
			return true;
		} catch (JwtException e) {
			System.out.println("Invalid JWT token or expired");
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty");
		}
		return false;
	}
}