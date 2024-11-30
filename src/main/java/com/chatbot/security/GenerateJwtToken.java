package com.chatbot.security;

import java.util.Date;

import com.chatbot.entity.UserVO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class GenerateJwtToken {

	public String generateJwtToken(UserVO user) {

		long expirationTime = 3600000;// 1Hr in Ms
		String secretKey = "6LfSsH4oAAAAAK1fGMgUqd1moEOgYs680vyZAIAc";
		return Jwts.builder().setSubject(user.getEmail()).claim("Role", user.getRole()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}
}
