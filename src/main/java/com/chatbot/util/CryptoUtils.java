package com.chatbot.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.iharder.Base64;

@Component
public class CryptoUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtils.class);
	private static final String ENCRYPTION_KEY = "ABCDEFGHIJKLMNOP";
	private static final String CHARACTER_ENCODING = "UTF-8";
	private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
	private static final String AES_ENCRYPTION_ALGORITHEM = "AES";
//	private static final String SECRET_KEY = "6LfSsH4oAAAAAK1f"; // Base64 encoded key
	private static final String IV = "5D9r9ZVzEYYgha93/aUK2w=="; // Base64 encoded IV

	@Autowired
	PasswordEncoder encoder;

	/**
	 * @param password
	 * @return
	 * @throws Exception
	 */
	
	private static final String SECRET_KEY = "6LfSsH4oAAAAAK1fGMgUqd1moEOgYs680vyZAIAc";// Correcting to 16 bytes (AES-128)

    public static String getDecrypt(String encryptedPassword) throws Exception {
        try {
            // Decode the encrypted password from Base64
            byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedPassword);

            // Create AES key from the specified secret key
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            // Initialize the AES cipher with ECB mode and PKCS5 padding
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decrypt the password
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Return the decrypted password as a string
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting password: " + e.getMessage(), e);
        }
    }

	/**
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ENCRYPTION_ALGORITHEM);
        Cipher cipher = Cipher.getInstance(AES_ENCRYPTION_ALGORITHEM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decodedValue = java.util.Base64.getDecoder().decode(encryptedText);
        byte[] decryptedValue = cipher.doFinal(decodedValue);
        return new String(decryptedValue);
    }
	

	public static String encrypt(String plainText) {
		String encryptedText = StringUtils.EMPTY;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			byte[] key = Base64.decode("u/Gu5posvwDsXUnV5Zaq4g==");
			byte[] ivparam = Base64.decode("5D9r9ZVzEYYgha93/aUK2w==");
			SecretKeySpec secretKey = new SecretKeySpec(key, AES_ENCRYPTION_ALGORITHEM);
			IvParameterSpec ivparameterspec = new IvParameterSpec(ivparam);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
			plainText = Hex.encodeHexString(plainText.getBytes(StandardCharsets.UTF_8));
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
			encryptedText = encoder.encodeToString(cipherText);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return encryptedText;
	}

	public static String encryptWithJavaBase64(String plainText) {
		String encryptedText = StringUtils.EMPTY;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			byte[] key = ENCRYPTION_KEY.getBytes(CHARACTER_ENCODING);
			SecretKeySpec secretKey = new SecretKeySpec(key, AES_ENCRYPTION_ALGORITHEM);
			IvParameterSpec ivparameterspec = new IvParameterSpec(key);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
			encryptedText = encoder.encodeToString(cipherText);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return encryptedText;
	}

	public static String decryptWithJavaBase64(String encryptedText) {
		String decryptedText = StringUtils.EMPTY;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			byte[] key = ENCRYPTION_KEY.getBytes(CHARACTER_ENCODING);
			SecretKeySpec secretKey = new SecretKeySpec(key, AES_ENCRYPTION_ALGORITHEM);
			IvParameterSpec ivparameterspec = new IvParameterSpec(key);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
			java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
			byte[] cipherText = decoder.decode(encryptedText.getBytes(StandardCharsets.UTF_8));
			decryptedText = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return decryptedText;
	}

}
