/**
 * 
 */
package com.handson.user.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author PattathilS
 *
 */
@Service
public class PasswordUtil {

	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

	private static final Logger log = LoggerFactory.getLogger(PasswordUtil.class);

	public Optional<String> encryptPassword(String password) {
		if (StringUtils.isEmpty(password))
			return Optional.empty();

		PBEKeySpec spec = null;
		byte[] salt = new byte[10];
		char[] chars = password.toCharArray();

		try {
			SecureRandom.getInstanceStrong().nextBytes(salt); // generating a random salt
			byte[] bytes = Base64.getEncoder().encodeToString(salt).getBytes();
			spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
			Arrays.fill(chars, Character.MIN_VALUE);// clearing the password chars

			SecretKeyFactory secretFactory = SecretKeyFactory.getInstance(ALGORITHM);
			return Optional.of(Base64.getEncoder().encodeToString(secretFactory.generateSecret(spec).getEncoded()));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			log.error("Exception while ecrypting password");
			return Optional.empty();
		} finally {
			spec.clearPassword();
		}
	}

}
