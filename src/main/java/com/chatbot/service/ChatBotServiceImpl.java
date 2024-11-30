package com.chatbot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.chatbot.config.OpenAiConfig;
import com.chatbot.dto.CountryDTO;
import com.chatbot.dto.MessageResponse;
import com.chatbot.entity.CountryVO;
import com.chatbot.repo.CountryRepo;

@Service
public class ChatBotServiceImpl implements ChatBotService {

	@Autowired
	CountryRepo countryRepo;

	@Autowired
	private OpenAiConfig openAiConfig; 

	private final String LLAMA_API_URL = "https://api.your-llama-provider.com/v1/chat";

	@Override
	public MessageResponse getChatBotResponse(String userMessage) {
		RestTemplate restTemplate = new RestTemplate();

		// Set headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + openAiConfig.getApiKey()); // Set your API key here
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Define payload
		String payload = String.format("{\"prompt\": \"%s\", \"max_tokens\": 150}", userMessage);

		HttpEntity<String> entity = new HttpEntity<>(payload, headers);

		try {
			// Send request to the Llama API
			ResponseEntity<String> response = restTemplate.exchange(LLAMA_API_URL, HttpMethod.POST, entity,
					String.class);

			// Return the response body wrapped in MessageResponse
			return new MessageResponse(response.getBody());
		} catch (HttpClientErrorException e) {
			// Log error and return a user-friendly message
			System.err.println("Error calling Llama API: " + e.getResponseBodyAsString());
			return new MessageResponse("Sorry, something went wrong. Please try again later.");
		}
	}

//	@Override
//	public String getChatGptResponse(String userMessage) {
//		try {
//
//			// Prepare the request payload for the GPT-3
//			JsonNode requestPayload = createRequestPayload(userMessage);
//
//			// Make the post req to OpenAI
//			JsonNode response = webClient.post().uri("/completions").header("Authorization", "Bearer" + openaiApiKey)
//					.header("Content-Type", "application/json").bodyValue(requestPayload.toString()).retrieve()
//					.bodyToMono(JsonNode.class).block();
//
//			// Extract the return response from GPT-3
//			return extractResponseText(response);
//		} catch (Exception e) {
//			// Handle exceptions and return a fallback message
//			e.printStackTrace();
//			return "Sorry, something went wrong. Please try again later.";
//		}
//	}
//
//	private JsonNode createRequestPayload(String userMessage) {
//		return new ObjectMapper().createObjectNode().put("model", "text-davinci-003").put("prompt", userMessage)
//				.put("max_tokens", 150) // Adjust the number of tokens for the response length
//				.put("temperature", 0.7); // Controls randomness; 0 is deterministic, 1 is random
//	}
//
//	private String extractResponseText(JsonNode response) {
//	if(response!=null&&response.has("choices")&&response.get("choices").size()>0)
//
//	{
//		return response.get("choices").get(0).get("text").asText().trim();
//	}return"No response from the model. Please try again.";
//	}

	// Country
	@Override
	public List<CountryVO> getAllCountry() {
		return countryRepo.findAll();
	}

	@Override
	public CountryVO getCountryById(Long id) {
		return countryRepo.findById(id).orElse(null);
	}

	@Override
	public Map<String, Object> updateCreateCountry(CountryDTO countryDTO) {

		CountryVO countryVO;
		String message = null;
		if (ObjectUtils.isEmpty(countryDTO.getId())) {
			countryVO = new CountryVO();
			message = "Country created Successfully";
		} else {
			countryVO = countryRepo.findById(countryDTO.getId()).orElseThrow();
			message = "Country Updated Successfully";
		}

		setCountryDTOToCountryVO(countryDTO, countryVO);
		countryRepo.save(countryVO);

		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("countryVO", countryVO);
		return response;
	}

	private void setCountryDTOToCountryVO(CountryDTO countryDTO, CountryVO countryVO) {
		countryVO.setCountry(countryDTO.getCountry());
		countryVO.setCountryCode(countryDTO.getCountryCode());
		countryVO.setOrgId(countryDTO.getOrgId());
	}

}
