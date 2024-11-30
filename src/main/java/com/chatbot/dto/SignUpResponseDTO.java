package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDTO {

	private Long id;
	private String status;
	private String message;
	private String token;
	private String email;
	private String name;
}
