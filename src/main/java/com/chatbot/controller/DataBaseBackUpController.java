package com.chatbot.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatbot.service.DataBaseBackUpService;

@RestController
@RequestMapping("/api/chatBot")
public class DataBaseBackUpController extends BaseController{

	@Autowired
	DataBaseBackUpService backUpService;

	@GetMapping("/backup")
	public String triggerBackup() {
		try {
			backUpService.backUpDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Succesfully Backed Up";
	}
}
