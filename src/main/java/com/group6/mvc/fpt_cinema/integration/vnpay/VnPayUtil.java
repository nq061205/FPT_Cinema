package com.group6.mvc.fpt_cinema.integration.vnpay;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class VnPayUtil {

    private VnPayUtil() {
    }

    public static String hmacSHA512(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(key);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute HMAC-SHA512", e);
        }
    }

    public static String buildQueryWithHash(Map<String, String> params, String hashSecret) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = params.get(name);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String encodedName = encode(name);
            String encodedValue = encode(value);

            hashData.append(encodedName).append('=').append(encodedValue);
            query.append(encodedName).append('=').append(encodedValue);

            if (i < fieldNames.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        return query.toString();
    }

    public static boolean verifySignature(Map<String, String> allParams, String hashSecret) {
        Map<String, String> fields = new TreeMap<>(allParams);
        String receivedHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        if (receivedHash == null || receivedHash.isEmpty()) {
            return false;
        }

        StringBuilder hashData = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = fields.get(name);
            if (value == null || value.isEmpty()) {
                continue;
            }
            hashData.append(encode(name)).append('=').append(encode(value));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
            }
        }

        String computed = hmacSHA512(hashSecret, hashData.toString());
        return computed.equalsIgnoreCase(receivedHash);
    }

    // Encode giống hệt sample VNPay: khoảng trắng -> '+' (US_ASCII), KHÔNG đổi sang %20,
    // nếu không hash sẽ lệch với cách VNPay tính lại -> "Sai chữ ký".
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}
