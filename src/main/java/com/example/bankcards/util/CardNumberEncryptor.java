package com.example.bankcards.util;

import org.springframework.stereotype.Component;

/**
 * The type Card number encryptor.
 */
@Component
public class CardNumberEncryptor {

    /**
     * Mask card number string.
     *
     * @param cardNumber the card number
     * @return the string
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Generate card number string.
     *
     * @return the string
     */
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