package com.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public interface DataBaseBackUpService {

	public void backUpDataBase() throws java.io.IOException;
}
