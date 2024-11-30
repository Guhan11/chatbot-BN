package com.chatbot.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.IOException;

@Service
public class DataBaseBackUpServiceImpl implements DataBaseBackUpService {

	private static final Logger logger = LoggerFactory.getLogger(DataBaseBackUpServiceImpl.class);

	@Value("${backup.directory}")
	private String backUpDirectory;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String dbUsername;

	@Value("${spring.datasource.password}")
	private String dbPassword;

	@Override
	public void backUpDataBase() throws java.io.IOException {

		if (dbUrl == null || dbUrl.isEmpty()) {
			logger.error("Database URL is not configured correctly: {}", dbUrl);
			throw new IllegalArgumentException("Database URL is not configured correctly.");
		}

		String dbName = getDatabaseName();
		String backupFileName = generateBackupFileName(dbName);

		File backupDir = new File(backUpDirectory);
		if (!backupDir.exists()) {
			if (backupDir.mkdir()) {
				System.out.println("Backup directory created: " + backupDir.getAbsolutePath());
			} else {
				System.out.println("Failed to create backup directory: " + backupDir.getAbsolutePath());
			}
		}

		String command = String.format("mysqldump -u%s -p\"%s\" --databases %s -r %s", dbUsername, dbPassword, dbName,
				backupFileName);

		logger.info("Executing command: {}", command);

		try {
			Process process = Runtime.getRuntime().exec(command);
			int processComplete = process.waitFor();

			if (processComplete == 0) {
				logger.info("Backup completed successfully for database: {}", dbName);
			} else {
				logger.error("Backup failed for database: {}", dbName);
			}

		} catch (IOException | InterruptedException e) {
			logger.error("Error during database backup", e);
		}
	}

	private String getDatabaseName() {
		if (dbUrl != null && !dbUrl.isEmpty()) {
			return dbUrl.substring(dbUrl.lastIndexOf("/") + 1); // Extract DB name from URL
		}
		throw new IllegalArgumentException("Database URL is invalid: " + dbUrl);
	}

	private String generateBackupFileName(String dbName) {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		return backUpDirectory + File.separator + dbName + "_backup_" + timestamp + ".sql";
	}
}
