package com.chatbot.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = getJwtFromRequest(request);

		if (token != null && jwtUtils.validateJwtToken(token)) {
			String userName = jwtUtils.getUsernameFromJwtToken(token);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, null,
					null);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

		}
		filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

//	private String getUsernameFromToken(String token) {
//		try {
//			Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//			return claims.getSubject();
//		} catch (Exception e) {
//			return null; // Return null or handle the error as needed
//		}
//	}
//
//	private boolean isTokenExpired(String token) {
//		try {
//			Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
//			Date expiration = claims.getExpiration();
//			boolean expired = expiration.before(new Date());
//			return expired;
//		} catch (Exception e) {
//			logger.error("Error parsing token: " + e.getMessage());
//			return true; // Token is invalid
//		}
//	}
//
//	private String extractToken(HttpServletRequest request) {
//		String header = request.getHeader("Authorization");
//		if (header != null && header.startsWith("Bearer ")) {
//			return header.substring(7); // Remove "Bearer " part
//		}
//		return null;
//	}
}
