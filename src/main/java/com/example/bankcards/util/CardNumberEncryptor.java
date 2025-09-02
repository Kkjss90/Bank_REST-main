package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class CardNumberEncryptor {

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    public String generateCardNumber() {
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder();
        sb.append("4");

        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}