package com.chatbot.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatbot.common.CommonConstant;
import com.chatbot.common.UserConstants;
import com.chatbot.config.EmailService;
import com.chatbot.dto.ChangePasswordFormDTO;
import com.chatbot.dto.OtpData;
import com.chatbot.dto.ResetPasswordFormDTO;
import com.chatbot.dto.SignUpFormDTO;
import com.chatbot.dto.SignUpResponseDTO;
import com.chatbot.entity.UserActionVO;
import com.chatbot.entity.UserVO;
import com.chatbot.repo.UserActionRepo;
import com.chatbot.repo.UserRepo;
import com.chatbot.util.CryptoUtils;
import com.chatbot.util.JwtUtils;
import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Service
public class AuthServiceImpl implements AuthService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	PasswordEncoder passwordEncoder = null;

	@Autowired
	UserRepo userRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	BCryptPasswordEncoder cryptEncoder;

	@Autowired
	UserActionRepo userActionRepo;

	@Autowired
	GoogleIdTokenVerifier googleIdTokenVerifier;

//	@Autowired
//	MailSender mailSender;

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	EmailService emailService;

	@Autowired
	JwtUtils jwtUtils;

	@Override
	public SignUpResponseDTO signup(SignUpFormDTO signUpRequest) {
		if (userRepo.existsByUserNameOrEmail(signUpRequest.getUserName(), signUpRequest.getEmail())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_ALREADY_REGISTERED);
		}
		UserVO user = getSignUpFormToUserVO(signUpRequest);
		UserVO savedUser = userRepo.save(user);

		String token = jwtUtils.generateToken(savedUser.getUserName());

		return new SignUpResponseDTO(user.getId(), "Success", "User registered successfully.", token, user.getEmail(),user.getUserName());
	}

	private UserVO getSignUpFormToUserVO(SignUpFormDTO signUpRequest) {
//		String encodedPassword =  CryptoUtils.encrypt(signUpRequest.getPassword());

		UserVO user = new UserVO();
		user.setUserName(signUpRequest.getUserName());
		user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		user.setEmail(signUpRequest.getEmail());
		user.setRole("USER");
		user.setProvider("MANUAL");

		return user;
	}

	@Override
	public UserVO googleLogin(String token) {
		try {
			// Verify the token
			GoogleIdToken idToken = googleIdTokenVerifier.verify(token);

			if (idToken != null) {
				Payload payload = idToken.getPayload();
				String email = (String) payload.get("email"); // Get method for email

				// Check if user already exists
				Optional<UserVO> existingUser = userRepo.findByEmail(email);
				UserVO user;

				if (existingUser.isPresent()) {
					user = existingUser.get();
				} else {
					user = new UserVO();
					user.setUserName(email.split("@")[0]);
					user.setEmail(email);
					user.setRole("USER");
					user.setProvider("GOOGLE");
					userRepo.save(user); // Save new User
				}

				// Return user object (you might want to omit sensitive info here)
				return user;

			} else {
				throw new Exception("Invalid Google Token");
			}
		} catch (Exception e) {
			// Log the error message for debugging
			System.err.println("MethodName : googleLogin() Error : " + e.getMessage());
			throw new RuntimeException("Google Login Failed!", e);
		}
	}

//	LOGIN
	// Temporary in-memory storage for OTPs, map email to OTP
	private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
	private static final long OTP_EXPIRY_DURATION_MS = 5 * 60 * 1000; // 5 minutes

//	// Inner class to store OTP data
//	@Data
//	private static class OtpData {
//		private final int otp;
//		private final long timestamp;
//	}

	@Override
	public Map<String, String> login(String email, String password) throws Exception {
		Optional<UserVO> user = userRepo.findByEmail(email);

		if (user.isPresent()) {
			if (passwordEncoder.matches(password, user.get().getPassword())) {
				String token = jwtUtils.generateToken(user.get().getUserName());

				Map<String, String> response = new HashMap<>();
				response.put("message", "Login Successfull");
				response.put("token", token);
				response.put("name", user.get().getUserName());
				response.put("email", user.get().getEmail());
				response.put("id", user.get().getId().toString());

				return response;
			}
		}
		throw new BadCredentialsException("Invalid email or password");
	}

//	Generate 6 Digit OTP
	private int generateOtp() {
		return 100000 + new Random().nextInt(900000);
	}

//	Verify OTP provided by User
	public void sentOtpToEmail(String email, int otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Your Login OTP");
		message.setText("Your OTP for Login is:" + otp);
		mailSender.send(message);

		try {
			mailSender.send(message);
		} catch (Exception e) {
			System.err.println("Error sending OTP email: " + e.getMessage());
			otpStorage.remove(email); // Clear OTP if email fails
		}
	}

//	Verify OTP Provided by User
	public boolean verifyOtp(String email, int otp) {
		OtpData otpData = otpStorage.get(email);

		if (otpData != null) {
			long currentTime = System.currentTimeMillis();
			if (otpData.getOtp() == otp && (currentTime - otpData.getTimeStamp()) <= OTP_EXPIRY_DURATION_MS) {
				otpStorage.remove(email); // OTP verified, remove from storage
				return true;
			} else if ((currentTime - otpData.getTimeStamp()) > OTP_EXPIRY_DURATION_MS) {
				otpStorage.remove(email); // Remove expired OTP
				System.out.println("OTP expired.");
			}
		}
		return false;
	}

//	private String generateOtp() {
//
//		return String.valueOf(new Random().nextInt(9000) + 1000);
//	}

//	private void sentOtpEmail(String email, String otp) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(email);
//		message.setSubject("Your OTP code");
//		message.setText("Your OTP code is: " + otp);
//		mailSender.send(message);
//
//		System.out.println("OTP Sent to" + email + "" + otp);
//	}

//	public String verifyOtp(String email, String otp) {
//		UserVO user = userRepo.findByEmail(email);
//		if (user != null && user.getOtp().equals(otp)) {
//			user.setOtp(null);
//			userRepo.save(user);
//			return "SUCCESS";
//		}
//		return "INVALID OTP";
//	}

	// @Override
//	public void signup(SignUpFormDTO signUpRequest) {
//		String methodName = "signup()";
//		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//		if (ObjectUtils.isEmpty(signUpRequest) || StringUtils.isBlank(signUpRequest.getEmail())
//				|| StringUtils.isBlank(signUpRequest.getUserName())) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_REGISTER_INFORMATION);
//		} else if (userRepo.existsByUserNameOrEmail(signUpRequest.getUserName(), signUpRequest.getEmail())) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_ALREADY_REGISTERED);
//		}
//		UserVO userVO = getUserVOFromSignUpFormDTO(signUpRequest);
//		userRepo.save(userVO);
//		createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_ADD_ACCOUNT);
//		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//	}
//
//	private UserVO getUserVOFromSignUpFormDTO(SignUpFormDTO signUpRequest) {
//		UserVO userVO = new UserVO();
//		userVO.setFirstName(signUpRequest.getFirstName());
//		userVO.setLastName(signUpRequest.getLastName());
//		userVO.setUserName(signUpRequest.getUserName());
//		userVO.setEmail(signUpRequest.getEmail());
//		try {
//			userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(signUpRequest.getPassword())));
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
//		}
////		userVO.setRole(Role.ROLE_USER);
//		userVO.setActive(true);
//		return userVO;
//	}
//__________________________________________
//	@Override
//	public UserVO login(LoginFormDTO loginRequest) {
//		String methodName = "login()";
//		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//		if (ObjectUtils.isEmpty(loginRequest) || StringUtils.isBlank(loginRequest.getEmail())
//				|| StringUtils.isBlank(loginRequest.getPassword())) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_LOGIN_INFORMATION);
//		}
//		UserVO userVO = userRepo.findByUserName(loginRequest.getEmail());
//		if (ObjectUtils.isNotEmpty(userVO)) {
//			if (compareEncodedPasswordWithEncryptedPassword(loginRequest.getPassword(), userVO.getPassword())) {
//				updateUserLoginInformation(userVO);
//			} else {
//				throw new ApplicationContextException(UserConstants.ERRROR_MSG_PASSWORD_MISMATCH);
//			}
//		} else {
//			throw new ApplicationContextException(
//					UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND_AND_ASKING_SIGNUP);
//		}
//		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//		return userVO;
//	}
//
	/**
	 * @param encryptedPassword -> Data from user;
	 * @param encodedPassword   ->Data from DB;
	 * @return
	 */
	private boolean compareEncodedPasswordWithEncryptedPassword(String encryptedPassword, String encodedPassword) {
		boolean userStatus = false;
		try {
			userStatus = encoder.matches(CryptoUtils.getDecrypt(encryptedPassword), encodedPassword);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
		}
		return userStatus;
	}

	/**
	 * @param userVO
	 */
//	private void updateUserLoginInformation(UserVO userVO) {
//		try {
//			userVO.setLoginStatus(true);
//			userRepo.save(userVO);
//			createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_TYPE_LOGIN);
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());m
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_UPDATE_USER_INFORMATION);
//		}
//	}

	public void createUserAction(String userName, Long id, String actionType) {
		try {
			UserActionVO userActionVO = new UserActionVO();
			userActionVO.setUserName(userName);
			userActionVO.setUserId(id);
			userActionVO.setActionType(actionType);
			userActionRepo.save(userActionVO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	public void logout(String userName) {
		String methodName = "logout()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isBlank(userName)) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_LOGOUT_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(userName);
		if (ObjectUtils.isNotEmpty(userVO)) {
			updateUserLogOutInformation(userVO);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	private void updateUserLogOutInformation(UserVO userVO) {
		try {
			userVO.setLoginStatus(false);
			userRepo.save(userVO);
			createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_TYPE_LOGOUT);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_UPDATE_USER_INFORMATION);
		}
	}

	@Override
	public void changePassword(ChangePasswordFormDTO changePasswordRequest) {
		String methodName = "changePassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(changePasswordRequest) || StringUtils.isBlank(changePasswordRequest.getUserName())
				|| StringUtils.isBlank(changePasswordRequest.getOldPassword())
				|| StringUtils.isBlank(changePasswordRequest.getNewPassword())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_CHANGE_PASSWORD_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(changePasswordRequest.getUserName());
		if (ObjectUtils.isNotEmpty(userVO)) {
			if (compareEncodedPasswordWithEncryptedPassword(changePasswordRequest.getOldPassword(),
					userVO.getPassword())) {
				try {
					userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(changePasswordRequest.getNewPassword())));
				} catch (Exception e) {
					throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
				}
				userRepo.save(userVO);
				createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_TYPE_CHANGE_PASSWORD);
			} else {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_OLD_PASSWORD_MISMATCH);
			}
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	@Override
	public void resetPassword(ResetPasswordFormDTO resetPasswordRequest) {
		String methodName = "resetPassword()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (ObjectUtils.isEmpty(resetPasswordRequest) || StringUtils.isBlank(resetPasswordRequest.getUserName())
				|| StringUtils.isBlank(resetPasswordRequest.getNewPassword())) {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_RESET_PASSWORD_INFORMATION);
		}
		UserVO userVO = userRepo.findByUserName(resetPasswordRequest.getUserName());
		if (ObjectUtils.isNotEmpty(userVO)) {
			try {
				userVO.setPassword(encoder.encode(CryptoUtils.getDecrypt(resetPasswordRequest.getNewPassword())));
			} catch (Exception e) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_UNABLE_TO_ENCODE_USER_PASSWORD);
			}
			userRepo.save(userVO);
			createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_TYPE_RESET_PASSWORD);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
	}

	
//	@Override
//	public GoogleLoginVO googleLogin(String userName) throws UsernameNotFoundException {
//		GoogleLoginVO google = googleLoginRepo.findByUserName(userName);
//
//		if (google == null) {
//			throw new UsernameNotFoundException("User Not Found");
//		}
//	}
}
