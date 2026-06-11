package com.group6.mvc.fpt_cinema.security;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class EncryptPassword {
    public static String encryptPassword(String password){
        String encryptedPassword = "";
        encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return encryptedPassword;
    }
    public static boolean checkPassword(String password, String encryptedPassword){
        return BCrypt.checkpw(password, encryptedPassword);
    }
}
