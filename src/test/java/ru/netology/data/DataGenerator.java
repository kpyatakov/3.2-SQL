package ru.netology.data;

import com.github.javafaker.Faker;
import java.util.Locale;

public class DataGenerator {

    private static String validLogin = "vasya";
    private static String validPassword = "qwerty123";

    public static UserInfo getValidLoginValidPassword(){
        return new UserInfo(validLogin, validPassword);
    }

    public static UserInfo getInvalidLoginValidPassword(){
        String invalidLogin = getInvalidLogin();
        return new UserInfo(invalidLogin, validPassword);
    }

    public static UserInfo getValidLoginInvalidPassword(){
        String invalidPwd = getInvalidPassword();
        return new UserInfo(validLogin, invalidPwd);
    }

    public static UserInfo getInvalidLoginInvalidPassword(){
        String invalidLogin = getInvalidLogin();
        String invalidPwd = getInvalidPassword();
        return new UserInfo(invalidLogin, invalidPwd);
    }

    private static String getInvalidLogin(){
        String invalidLogin = "";
        Faker fkr = new Faker(new Locale("ru"));
        do {
            invalidLogin = fkr.name().username().replace('ё', 'е').replace('Ё', 'Е');;
        }while (invalidLogin == validLogin); // а вдруг???

        return invalidLogin;
    }

    private static String getInvalidPassword(){
        String invalidPassword = "";
        Faker fkr = new Faker(new Locale("ru"));
        do {
            invalidPassword = fkr.funnyName().name();
        }while (invalidPassword == validPassword); // а вдруг???

        return invalidPassword;
    }
}
