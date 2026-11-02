package com.edu.checkcode.util.enums;

public enum PaymentMethod {
    CREDIT,
    DEBIT,
    PIX;

    public static PaymentMethod from(String preferencePaymentMethods) {
        if (preferencePaymentMethods.contains("pix")) {
            return PIX;
        } else if (preferencePaymentMethods.contains("debit")) {
            return DEBIT;
        } else {
            return CREDIT;
        }
    }
}
