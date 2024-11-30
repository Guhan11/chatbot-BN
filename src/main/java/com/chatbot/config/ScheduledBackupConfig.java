package com.chatbot.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.chatbot.service.DataBaseBackUpService;

@Configuration
@EnableScheduling
public class ScheduledBackupConfig {

	@Autowired
	DataBaseBackUpService dataBackupService;

	@Scheduled(cron = "0 0 2 * * ?")
	public void performDatabaseBackup() throws IOException {
		dataBackupService.backUpDataBase();
	}
}
