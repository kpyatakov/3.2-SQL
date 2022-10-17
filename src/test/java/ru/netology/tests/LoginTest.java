package ru.netology.tests;

import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.DatabaseHelper;
import ru.netology.data.UserInfo;
import ru.netology.pages.LoginPage;
import ru.netology.pages.VerificationPage;

import javax.management.InvalidApplicationException;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.*;

public class LoginTest {

    @AfterAll
    public static void cleanUp(){
        DatabaseHelper.clearAll();
    }

    @Test
    public void shouldValidLoginAndGetVerificationPage() {
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getValidLoginValidPassword();
        VerificationPage vp = loginPage.validLogin(testUserData);
    }

    @Test
    public void shouldValidLoginAndEnterGoodCode() throws SQLException, InvalidApplicationException {
        DatabaseHelper.resetCodes(); //That seems to reset invalid login count in SUT
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getValidLoginValidPassword();
        VerificationPage verificationPage = loginPage.validLogin(testUserData);

        int validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        verificationPage.validVerify(validCode);
    }

    @Test
    public void shouldValidLoginAndEnterBadCode() throws SQLException, InvalidApplicationException {
        DatabaseHelper.resetCodes(); //That seems to reset invalid login count in SUT
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getValidLoginValidPassword();
        VerificationPage verificationPage = loginPage.validLogin(testUserData);

        int validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        int invalidCode = validCode + 42;  //why 42? Only Douglas Adams would know. But the code is sure invalid ))

        verificationPage.invalidVerify(invalidCode);
    }

    @Test
    public void shouldInvalidLoginBadNameBadPassword() {
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getInvalidLoginInvalidPassword();
        loginPage.invalidLogin(testUserData);
    }

    @Test
    public void shouldInvalidLoginGoodNameBadPassword() {
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getValidLoginInvalidPassword();
        loginPage.invalidLogin(testUserData);
    }

    @Test
    public void shouldInvalidLoginBadNameGoodPassword() {
        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();

        UserInfo testUserData = DataGenerator.getInvalidLoginValidPassword();
        loginPage.invalidLogin(testUserData);
    }

    @Test
    public void shouldValidLoginAndEnterBadCode3times() throws SQLException, InvalidApplicationException {
        DatabaseHelper.resetCodes(); //That seems to reset invalid login count in SUT

        UserInfo testUserData = DataGenerator.getValidLoginValidPassword();

        open("http://localhost:9999");

        LoginPage loginPage = new LoginPage();
        VerificationPage verificationPage = loginPage.validLogin(testUserData);
        int validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        int invalidCode1 = validCode + 10;
        verificationPage.invalidVerify(invalidCode1);//first bad attempt, expect "error - bad code"

        open("http://localhost:9999");
        loginPage = new LoginPage();
        verificationPage = loginPage.validLogin(testUserData);
        validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        int invalidCode2 = validCode + 20;
        verificationPage.invalidVerify(invalidCode2);//second bad attempt, expect "error - bad code"

        open("http://localhost:9999");
        loginPage = new LoginPage();
        verificationPage = loginPage.validLogin(testUserData);
        validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        int invalidCode3 = validCode + 30;
        verificationPage.invalidVerify(invalidCode3);//third bad attempt, expect "error - bad code"

        open("http://localhost:9999");
        loginPage = new LoginPage();
        verificationPage = loginPage.validLogin(testUserData);
        validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        int invalidCode4 = validCode + 40;
        loginPage = verificationPage.invalidVerifyTooManyBadAttempts(invalidCode4);//last bad attempt, expect "too many bad logins, then jump to login page"

        //attempt to login again with the valid code:
        open("http://localhost:9999");
        loginPage = new LoginPage();
        verificationPage = loginPage.validLogin(testUserData);
        validCode = DatabaseHelper.getVerificationCode(testUserData.getLogin());
        loginPage = verificationPage.invalidVerifyTooManyBadAttempts(validCode);//valid code works no more, expect "too many bad logins, then jump to login page"
    }
}