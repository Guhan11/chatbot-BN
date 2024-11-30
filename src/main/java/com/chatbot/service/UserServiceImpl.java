
package com.chatbot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chatbot.common.CommonConstant;
import com.chatbot.common.UserConstants;
import com.chatbot.dto.UserDetailsDTO;
import com.chatbot.entity.UserVO;
import com.chatbot.repo.UserActionRepo;
import com.chatbot.repo.UserRepo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Service
public class UserServiceImpl implements UserService {
	public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepo userRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserActionRepo userActionRepo;

	@Autowired
	GoogleIdTokenVerifier googleIdTokenVerifier;

//	@Override
//	public UserVO getUserById(Long userId) {
//		String methodName = "getUserById()";
//		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
//		if (ObjectUtils.isEmpty(userId)) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_ID);
//		}
//		UserVO userVO = userRepo.getUserById(userId);
//		if (ObjectUtils.isEmpty(userVO)) {
//			throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
//		}
//		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
//		return userVO;
//	}

	@Override
	public UserVO getUserByUserName(String userName) {
		String methodName = "getUserByUserName()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isNotEmpty(userName)) {
			UserVO userVO = userRepo.findByUserName(userName);
			if (ObjectUtils.isEmpty(userVO)) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
			}
			LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
			return userVO;
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_NAME);
		}
	}

	@Override
	public void removeUser(String userName) {
		String methodName = "removeUser()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		if (StringUtils.isNotEmpty(userName)) {
			UserVO userVO = userRepo.findByUserName(userName);
			if (ObjectUtils.isEmpty(userVO)) {
				throw new ApplicationContextException(UserConstants.ERRROR_MSG_USER_INFORMATION_NOT_FOUND);
			}
			userVO.setActive(false);
			userVO.setAccountRemovedDate(new Date());
			userRepo.save(userVO);
			createUserAction(userVO.getUserName(), userVO.getId(), UserConstants.USER_ACTION_REMOVE_ACCOUNT);
		} else {
			throw new ApplicationContextException(UserConstants.ERRROR_MSG_INVALID_USER_NAME);
		}
	}

	@Override
	public void createUserAction(String userName, long userId, String actionType) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserVO editProfile(UserDetailsDTO userDEtailsDTO, Long id) {
		UserVO userVO = userRepo.findById(id).orElse(null);
		userVO.setGender(userDEtailsDTO.getGender());
		userVO.setAge(userDEtailsDTO.getAge());
		userVO.setPhone(userDEtailsDTO.getPhone());
		userRepo.save(userVO);
		return userVO;
	}

	@Override
	public List<Map<String, Object>> getUserById(Long orgId) {
		Set<Object[]> user = userRepo.findUserById(orgId);
		return getUser(user);
	}

	private List<Map<String, Object>> getUser(Set<Object[]> user) {
		List<Map<String, Object>> List1 = new ArrayList<>();
		for (Object[] ch : user) {
			Map<String, Object> map = new HashMap<>();
			map.put("age", ch[0] != null ? ch[0].toString() : ""); // Empty string if null
			map.put("gender", ch[1] != null ? ch[1].toString() : "");
			map.put("phone", ch[2] != null ? ch[2].toString() : "");

			List1.add(map);
		}
		return List1;
	}

	@Override
	public UserVO uploadProfileImage(MultipartFile file, Long id) throws IOException {
		String fileName = file.getOriginalFilename();
		byte[] fileData = file.getBytes();

		UserVO userVO = userRepo.findById(id).orElse(null);
		userVO.setImageName(fileName);
		userVO.setImageData(fileData);

		return userRepo.save(userVO);
	}

	@Override
	public byte[] getProfileImage(Long id) {
		UserVO user = userRepo.findById(id).orElse(null);
		return user.getImageData();
	}
}

