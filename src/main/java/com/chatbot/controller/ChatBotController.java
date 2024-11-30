package com.chatbot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatbot.dto.MessageRequest;
import com.chatbot.dto.MessageResponse;
import com.chatbot.entity.CountryVO;
import com.chatbot.service.ChatBotService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/chatBot")
public class ChatBotController extends BaseController {

	@Autowired
	ChatBotService chatbotService;

//	AI Response
	@PostMapping("/sendMessage")
	public MessageResponse sendMessage(@RequestBody MessageRequest request) {
	    try {
	        // Call the chat service to get a response
	        return chatbotService.getChatBotResponse(request.getMessage());
	    } catch (Exception e) {
	        // Log the error for debugging purposes
	        System.err.println("Error processing the chat message: " + e.getMessage());

	        // Return a user-friendly error response
	        return new MessageResponse("Sorry, something went wrong while processing your message. Please try again later.");
	    }
	}

	@GetMapping("/getAllCountry")
	public List<CountryVO> getAllCountry() {
		List<CountryVO> country = chatbotService.getAllCountry();
		return country;
	}

}
