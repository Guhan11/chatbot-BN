package com.chatbot.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.chatbot.dto.CountryDTO;
import com.chatbot.dto.MessageResponse;
import com.chatbot.entity.CountryVO;

@Service
public interface ChatBotService {

//	Country
	List<CountryVO> getAllCountry();

	CountryVO getCountryById(Long id);

	Map<String, Object> updateCreateCountry(CountryDTO countryDTO);

// 	AI Response
//	public MessageResponsePJ sendMessage(MessageRequestPJ messageRequestPJ);

	MessageResponse getChatBotResponse(String userMessage);
}
