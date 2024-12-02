package com.chatbot.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.chatbot.dto.ChangePasswordFormDTO;
import com.chatbot.dto.ResetPasswordFormDTO;
import com.chatbot.dto.SignUpFormDTO;
import com.chatbot.dto.SignUpResponseDTO;
import com.chatbot.dto.UserDetailsDTO;
import com.chatbot.entity.UserVO;

@Service
public interface AuthService {

	public SignUpResponseDTO signup(SignUpFormDTO signUpRequest);

	public UserVO googleLogin(String token);

	public Map<String, String> login(String email, String password) throws Exception;

	public boolean verifyOtp(String email, int otp);

	public String changePassword(ChangePasswordFormDTO changePasswordDTO);


	public void logout(String userName);

//	public void logout(String userName);

//	public void changePassword(ChangePasswordFormDTO changePasswordRequest);

	public void resetPassword(ResetPasswordFormDTO resetPasswordRequest);



//	public UserVO login(LoginFormDTO loginRequest);

	// public GoogleLoginVO googleLogin(String userName) throws
	// UsernameNotFoundException;
}
