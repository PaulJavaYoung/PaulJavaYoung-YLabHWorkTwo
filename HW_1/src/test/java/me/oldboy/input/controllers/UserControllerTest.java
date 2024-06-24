package me.oldboy.input.controllers;

import me.oldboy.input.entity.User;
import me.oldboy.input.repository.UserBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserController.
 */
class UserControllerTest {
    private static UserBase userBase = new UserBase();;
    private UserController userController = new UserController(userBase);
    private static String godUserLogin;
    private static String emptyUserLogin;
    private static String alreadyExistUserLogin;

    @BeforeAll
    public static void setUp() {
        userBase.initBaseAdmin();
        godUserLogin = "User";
        emptyUserLogin = "";
        alreadyExistUserLogin = "Admin";
    }

    @AfterAll
    public static void finalCleanReserveAndUserDataBase() {
        for (Map.Entry<String, User> erasure : userBase.getUsersBase().entrySet()) {
            erasure.getValue().getUserReservedUnitList().clear();
        }
    }

    @Test
    public void goodCreateUserTest(){
        assertTrue(userController.createUser(godUserLogin));
    }

    @Test
    public void emptyCreateLoginTest(){
        assertThrows(IllegalArgumentException.class, () -> userController.createUser(emptyUserLogin));
    }

    @Test
    public void nullCreateLoginTest(){
        assertThrows(IllegalArgumentException.class, () -> userController.createUser(null));
    }

    @Test
    public void alreadyExistCreateLoginTest(){
        userController.createUser(godUserLogin);
        assertFalse(userController.createUser(godUserLogin));
        assertFalse(userController.createUser(alreadyExistUserLogin));
    }

    @Test
    public void alreadyExistingUserLoginTest(){
        User resUser = userController.login(alreadyExistUserLogin);
        assertEquals(resUser, userBase.getUsersBase().get(resUser.getLogin()));
    }

    @Test
    public void notExistingLoginUserTest(){
        assertThrows(RuntimeException.class, () -> userController.login(godUserLogin));
    }

    @Test
    public void emptyEnterLoginTest(){
        assertThrows(IllegalArgumentException.class, () -> userController.login(emptyUserLogin));
    }

    @Test
    public void nullEnterLoginTest(){
        assertThrows(IllegalArgumentException.class, () -> userController.login(null));
    }
}