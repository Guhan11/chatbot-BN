package com.chatbot.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chatbot.dto.UserDetailsDTO;
import com.chatbot.entity.UserVO;

@Service
public interface UserService {

//	public UserVO getUserById(Long userId);

	public UserVO getUserByUserName(String userName);

	public void createUserAction(String userName, long userId, String actionType);

	public void removeUser(String userName);

	public UserVO editProfile(UserDetailsDTO userDetailsDTO, Long id);

	public List<Map<String, Object>> getUserById(Long id);

	public UserVO uploadProfileImage(MultipartFile file, Long id) throws IOException;

	public byte[] getProfileImage(Long id);

}
