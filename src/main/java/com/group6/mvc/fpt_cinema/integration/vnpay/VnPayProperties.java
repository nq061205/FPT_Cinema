package com.group6.mvc.fpt_cinema.integration.vnpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VnPayProperties {

    private String tmnCode;

    private String hashSecret;

    private String payUrl;

    private String returnUrl;

    private String version = "2.1.0";

    private String command = "pay";

    private String currencyCode = "VND";

    private String locale = "vn";

    private int paymentTimeoutMinutes = 15;
}
