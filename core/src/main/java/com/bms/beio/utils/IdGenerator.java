package com.bms.beio.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author marri.shashanka
 *
 */
public class IdGenerator {

	static Logger LOG = LoggerFactory.getLogger(IdGenerator.class);

	/**
	 * @param key
	 * @return Unique ID
	 * 
	 * This method generates a hash code for a given input which is used to 
	 * uniquely identify a content fragment in the repository.
	 */
	public static String generateUniqueID(String key) {
		LOG.debug("::::: Entered generateUniqueID Method of IdGenerator class :::::");
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = digest.digest(key.getBytes("UTF-8"));
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				stringBuffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			LOG.error("Exception generating Unique ID in generateUniqueID method of IdGenerator class " , e);
		}
		LOG.debug("::::: Exit from generateUniqueID Method of IdGenerator class :::::");
		return null;
	}
}
