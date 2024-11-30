package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpData {
	private final int otp;
	private final long timeStamp;
}
