/*
 * Copyright (c) 2018, Deepak Goyal under Apache License.
 *     All rights reserved.
 *     Redistribution and use in source and binary forms, with or without
 *     modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 */

package com.productcode.identifier.library;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class ProductCodeValidator {
    private static final Long LARGEST_POSSIBLE_UPC = 999999999999L; // 12 digits
    private static final Long LARGEST_POSSIBLE_ISBN_10 = 9999999999L; // 10 digits
    private static final Long LARGEST_POSSIBLE_EAN = 9999999999999L; // 13 digits

    public static ProductCodeType getCodeType(@NonNull String code) {
        if (TextUtils.isEmpty(code)) return ProductCodeType.NONE;

        // remove all the special characters, just retain alphanumerics
        code = code.replaceAll("[^A-Za-z0-9]", "");

        // check if code contains only numerics
        if (code.matches("[0-9]+")) {
            // numerics only
            switch (code.length()) {
                case 8: // can be EAN-8
                    return isValidEAN(code) ? ProductCodeType.EAN_8 : ProductCodeType.NONE;
                case 10: // can be ISBN-10
                    return isValidISBN_10(code) ? ProductCodeType.ISBN_10 : ProductCodeType.NONE;
                case 12: // can be UPC
                    return isValidUPC(code) ? ProductCodeType.UPC : ProductCodeType.NONE;
                case 13: // can be EAN-13 or ISBN-13 (both are same)
                    return isValidEAN(code) ? ProductCodeType.EAN_13 : ProductCodeType.NONE;
            }
        } else {
            // alphanumeric
            switch (code.length()) {
                case 8:
                    return ProductCodeType.SKU;
                case 10:
                    return ProductCodeType.ASIN;
            }
        }
        return ProductCodeType.NONE;
    }

    /**
     * check if @UPC is valid or not
     * UPC is 12 digits length
     *
     * @param code upc code
     * @return true or false
     */
    public static boolean isValidUPC(@NonNull String code) {
        if (!TextUtils.isEmpty(code)) {
            code = getDigits(code);
            if (!TextUtils.isEmpty(code)) { // ensure code has some value with digits only
                int nDigits = code.length();

                int sumEven = 0;
                int sumOdd = 0;

                int checkDigit = Integer.parseInt(code.substring(nDigits - 1));

                // traverse the digits from left to right
                for (int i = 1; i <= nDigits - 1; i++) {
                    int digit = Integer.parseInt(code.substring(i - 1, i));
                    if (i % 2 == 0) // even
                        sumEven += digit;
                    else // odd
                        sumOdd += digit;
                }
                int sum = sumEven + (sumOdd * 3);
                int calcCheckDigit = (10 - (sum % 10)) % 10;
                return checkDigit == calcCheckDigit;
            }
        }
        return false;
    }

    /**
     * check if @ISBN-10 is valid  or not
     * ISBN_10 is 10 digits in length
     *
     * @param code ISBN code
     * @return true or false
     */
    public static boolean isValidISBN_10(@NonNull String code) {
        if (!TextUtils.isEmpty(code)) {
            code = getDigits(code);
            if (!TextUtils.isEmpty(code)) { // ensure code has some value with digits only
                int nDigits = code.length();
                int sum = 0;

                // traverse the digits from left to right
                for (int i = 1; i <= nDigits; i++) {
                    int digit = Integer.parseInt(code.substring(i - 1, i));
                    sum += (digit * i);
                }
                return sum % 11 == 0;
            }
        }
        return false;
    }

    /**
     * check if @EAN is valid or not
     * EAN is 13 digits in length
     *
     * @param code EAN code
     * @return true or false
     */
    public static boolean isValidEAN(String code) {
        if (!TextUtils.isEmpty(code)) {
            code = getDigits(code);
            if (!TextUtils.isEmpty(code)) { // ensure code has some value with digits only
                int nDigits = code.length();

                int sumEven = 0;
                int sumOdd = 0;

                int checkDigit = Integer.parseInt(code.substring(nDigits - 1));

                // traverse the digits from left to right
                for (int i = 1; i <= nDigits - 1; i++) {
                    int digit = Integer.parseInt(code.substring(i - 1, i));
                    if (i % 2 == 0) // even
                        sumEven += digit;
                    else // odd
                        sumOdd += digit;
                }
                int sum = (sumEven * 3) + sumOdd;
                int calcCheckDigit = (10 - (sum % 10)) % 10;
                return checkDigit == calcCheckDigit;

            }
        }
        return false;
    }

    /**
     * check if @ISBN-13 is valid or not
     * ISBN-13 is 13 digits in length
     *
     * @param code ISBN-13 code
     * @return true or false
     */
    public static boolean isValidISBN_13(String code) {
        return isValidEAN(code);
    }

    /**
     * check if @UPC is valid or not
     *
     * @param codeValue upc code
     * @return true or false
     * @deprecated use {@link #isValidUPC(String)} instead. This doesn't validates the code starting with 0.
     * So use the new function with string parameter
     */
    @Deprecated
    public static boolean isValidUPC(long codeValue) {
        if (codeValue > 0 && codeValue < LARGEST_POSSIBLE_UPC) {

            int nDigits = getTotalDigits(codeValue);

            int sumOdd = 0;
            int sumEven = 0;

            // get the first right digit
            int checkDigit = (int) (codeValue % 10);
            codeValue = codeValue / 10;

            // traverse the digits from right to left but numbering will be from left to right
            for (int i = nDigits - 1; i >= 1; i--) {
                // get the digit
                int digit = (int) (codeValue % 10);
                codeValue = codeValue / 10;
                if (i % 2 == 0) {
                    // even position
                    sumEven += digit;
                } else {
                    // odd position
                    sumOdd += digit;
                }
            }
            int sum = sumEven + (sumOdd * 3);
            int calcCheckDigit = (10 - (sum % 10)) % 10;
            return checkDigit == calcCheckDigit;
        }
        return false;
    }

    /**
     * check if @ISBN-10 is valid  or not
     *
     * @deprecated use {@link #isValidISBN_10(String)} instead. This doesn't validates the code starting with 0.
     * So use the new function with string parameter
     */
    @Deprecated
    public static boolean isValidISBN_10(long codeValue) {
        if (codeValue > 0 && codeValue < LARGEST_POSSIBLE_ISBN_10) {

            int nDigits = getTotalDigits(codeValue);

            int sum = 0;
            for (int i = 1; i <= nDigits; i++) {
                // get the digit
                int digit = (int) (codeValue % 10);
                codeValue = codeValue / 10;

                sum += (digit * i);
            }
            return sum % 11 == 0;
        }
        return false;
    }

    /**
     * check if @EAN is valid or not
     *
     * @param codeValue EAN code
     * @return true or false
     * @deprecated use {@link #isValidEAN(String)} instead. This doesn't validates the code starting with 0.
     * So use the new function with string parameter
     */
    @Deprecated
    public static boolean isValidEAN(long codeValue) {
        if (codeValue > 0 && codeValue < LARGEST_POSSIBLE_EAN) {

            int nDigits = getTotalDigits(codeValue);

            int sumOdd = 0;
            int sumEven = 0;

            // get the first right digit
            int checkDigit = (int) (codeValue % 10);
            codeValue = codeValue / 10;

            // traverse the digits from right to left but numbering will be from left to right
            for (int i = nDigits - 1; i >= 1; i--) {
                // get the digit
                int digit = (int) (codeValue % 10);
                codeValue = codeValue / 10;
                if (i % 2 == 0) {
                    // even position
                    sumEven += digit;
                } else {
                    // odd position
                    sumOdd += digit;
                }
            }
            int sum = (sumEven * 3) + sumOdd;
            int calcCheckDigit = (10 - (sum % 10)) % 10;
            return checkDigit == calcCheckDigit;
        }
        return false;
    }

    /**
     * check if @ISBN-13 is valid or not
     *
     * @param codeValue ISBN-13 code
     * @return true or false
     * @deprecated use {@link #isValidISBN_13(String)} instead. This doesn't validates the code starting with 0.
     * So use the new function with string parameter
     */
    @Deprecated
    public static boolean isValidISBN_13(long codeValue) {
        return isValidEAN(codeValue);
    }

    /**
     * get the total digits of number
     */
    private static int getTotalDigits(long codeValue) {
        return (int) (Math.floor(Math.log10(Math.abs(codeValue))) + 1);
    }

    /**
     * get the digits only from code
     */
    private static String getDigits(@NonNull String code) {
        return code.replaceAll("[^0-9]", "");
    }
}
