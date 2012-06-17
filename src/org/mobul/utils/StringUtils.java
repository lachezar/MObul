package org.mobul.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import android.widget.EditText;

public class StringUtils {

	public static String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next().toString());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
	public static String formatFileSize(long size) {
		if (size < 900*1024) {
			return (1 + size / 1024) + " KB";
		} else {
			Formatter f = new Formatter();
			return f.format("%.1f MB", ((size) / (1024d*1024d))).toString();
		}
	}
	
	public static boolean isEmpty(String str) {
		if (str == null || str.equals("") || str.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(EditText editText) {
		return isEmpty(editText.getText().toString());		
	}

	private static final String HEXES = "0123456789abcdef";

	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	public static String generateQueryString(List<String[]> tokens) {
		StringBuilder sb = new StringBuilder();
		
		for (String[] s: tokens) {
			if (s.length == 2 && !StringUtils.isEmpty(s[0])) {
				sb.append(URLEncoder.encode(s[0])).append("=").append(URLEncoder.encode(s[1])).append("&");
			}
		}
		
		return sb.toString();
	}
	
	public static String toSHA1(String s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		s = getHex(md.digest(s.getBytes()));
		
		return s;
	}

}
