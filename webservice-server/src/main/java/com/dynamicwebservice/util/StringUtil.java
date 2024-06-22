package com.dynamicwebservice.util;

public class StringUtil {

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String fixLength(String input, int length) {
        if (input.length() >= length) {
            return input.substring(0, length);
        } else {
            StringBuilder sb = new StringBuilder(input);
            while (sb.length() < length) {
                sb.append('0');
            }
            return sb.toString();
        }
    }

}
