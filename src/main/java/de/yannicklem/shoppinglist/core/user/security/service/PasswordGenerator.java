package de.yannicklem.shoppinglist.core.user.security.service;

import java.math.BigInteger;

import java.security.SecureRandom;


public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {

        return new BigInteger(130, random).toString(32);
    }
}
