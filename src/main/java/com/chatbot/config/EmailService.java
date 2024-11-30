package com.chatbot.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	JavaMailSender mailSender;

//	Generate 6 Digit OTP
	private int generateOtp() {
		Random random = new Random();
		return 100000 + random.nextInt(900000);
	}

//	Verify OTP provided by User
	public void sentOtpToEmail(String email, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Your Login OTP");
		message.setText("Your OTP for Login is:" + otp);
		mailSender.send(message);
	}

	// Temporary in-memory storage for OTPs, map email to OTP
	private final Map<String, Integer> otpStorage = new HashMap<>();

//	Verify OTP Provided by User
	public boolean verifyOtp(String email, int otp) {
		Integer storedOtp = otpStorage.get(email);
		if (storedOtp != null && storedOtp == otp) {
			otpStorage.remove(email); // OTP verified, remove from storage
			return true;
		}
		return false;
	}

}
