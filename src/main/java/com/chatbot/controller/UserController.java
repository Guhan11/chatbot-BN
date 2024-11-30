/*
 * ========================================================================
 * This file is the intellectual property of GSM Outdoors.it
 * may not be copied in whole or in part without the express written
 * permission of GSM Outdoors.
 * ========================================================================
 * Copyrights(c) 2023 GSM Outdoors. All rights reserved.
 * ========================================================================
 */
package com.chatbot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatbot.common.CommonConstant;
import com.chatbot.common.UserConstants;
import com.chatbot.dto.ResponseDTO;
import com.chatbot.dto.UserDetailsDTO;
import com.chatbot.entity.UserVO;
import com.chatbot.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
	public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@GetMapping("/removeUser")
	public ResponseEntity<ResponseDTO> removeUser(@RequestParam String userName) {
		String methodName = "removeUser()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			userService.removeUser(userName);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, userName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.USER_REMOVED_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.USER_REMOVE_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping("/editProfile")
	public ResponseEntity<?> editProfile(@Valid @RequestBody UserDetailsDTO userDetailsDTO, @RequestParam Long id) {
		String methodName = "editProfile()";
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		try {
			UserVO newUser = userService.editProfile(userDetailsDTO, id);
			return ResponseEntity.ok(newUser);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, errorMsg);
			ResponseEntity.badRequest().body(e.getMessage());
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, UserConstants.SIGNUP_REGISTERED_SUCCESS_MESSAGE);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, UserConstants.SIGNUP_REGISTERED_FAILED_MESSAGE,
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getUserById")
	public ResponseEntity<ResponseDTO> getUserById(@RequestParam Long id) {
		String methodName = "getUserById()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		List<Map<String, Object>> userVO = new ArrayList<>();
		try {
			userVO = userService.getUserById(id);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "User Information recieved");
			responseObjectsMap.put("userVO", userVO);
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "User information receive failed By id",
					errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);

	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping("/uploadProfileImage")
	public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image, @RequestParam Long id) {
		String methodName = "uploadProfileImage()";
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		try {
			UserVO newUser = userService.uploadProfileImage(image, id);
			return ResponseEntity.ok(newUser);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, errorMsg);
			ResponseEntity.badRequest().body(e.getMessage());
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Profile Picture Uploaded successfully");
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Failed to upload profile picture", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getProfileImage")
	public ResponseEntity<?> getProfileImage(@RequestParam Long id) {
		String methodName = "getProfileImage()";
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		try {
			byte[] imageData = userService.getProfileImage(id);
			if (imageData == null) {
				// Handle the case when there is no image
				return ResponseEntity.noContent().build();
			}
			// Determine the content type (image/jpeg or image/png) based on image data or
			// file extension
			String contentType = getImageContentType(imageData);

			// Return the image with the correct content type
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(imageData);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME_WITH_USER_NAME, methodName, errorMsg);
			ResponseEntity.badRequest().body(e.getMessage());
		}
		if (StringUtils.isBlank(errorMsg)) {
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "This is your Profile Picture");
			responseDTO = createServiceResponse(responseObjectsMap);
		} else {
			responseDTO = createServiceResponseError(responseObjectsMap, "Failed to fetch profile picture", errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	//To check or determine the JPG or JPEG
	private String getImageContentType(byte[] imageData) {
		try {
			// Check the first few bytes of the image to determine the format
			if (imageData != null && imageData.length > 0) {
				// Check for PNG (Starts with the following bytes: 89 50 4E 47)
				if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 && imageData[2] == (byte) 0x4E
						&& imageData[3] == (byte) 0x47) {
					return MediaType.IMAGE_PNG_VALUE;
				}
				// Check for JPEG (Starts with the following bytes: FF D8 FF)
				else if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8 && imageData[2] == (byte) 0xFF) {
					return MediaType.IMAGE_JPEG_VALUE;
				}
				// If unknown, return a default (e.g., JPEG)
				else {
					return MediaType.IMAGE_JPEG_VALUE;
				}
			}
		} catch (Exception e) {
			// Handle any errors
			return MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default if type can't be determined
	}

}
