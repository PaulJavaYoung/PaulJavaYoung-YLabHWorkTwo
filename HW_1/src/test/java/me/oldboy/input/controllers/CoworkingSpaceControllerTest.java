package me.oldboy.input.controllers;

import me.oldboy.input.entity.User;
import me.oldboy.input.repository.HallBase;
import me.oldboy.input.repository.ReserveBase;
import me.oldboy.input.repository.UserBase;
import me.oldboy.input.repository.WorkplaceBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CoworkingSpaceController.
 */
class CoworkingSpaceControllerTest {

    private static CoworkingSpaceController coworkingSpaceController;
    private static UserBase userBase;
    private static ReserveBase reserveBase;
    private static HallBase hallBase;
    private static WorkplaceBase workplaceBase;

    private static User userOne;
    private static User adminUser;
    private static String userOneLogin = "User";
    private static LocalDate reserveDateOne;
    private static LocalDate reserveDateTwo;

    @BeforeAll
    public static void setUp() {
        userBase = new UserBase();
        reserveBase = new ReserveBase(userBase);
        hallBase = new HallBase();
        hallBase.initHallBase();
        hallBase.setReserveBase(reserveBase);
        workplaceBase = new WorkplaceBase();
        workplaceBase.initPlaceBase();
        workplaceBase.setReserveBase(reserveBase);
        coworkingSpaceController = new CoworkingSpaceController(reserveBase, userBase);
        userBase.initBaseAdmin();
        userOne = new User("User");
        userBase.getUsersBase().put(userOne.getLogin(), userOne);
        adminUser = userBase.getUsersBase().get("Admin");
        reserveDateOne = LocalDate.of(2024, 06,06);
        reserveDateTwo = LocalDate.of(2025, 03,03);
    }

    @AfterEach
    public void finalCleanReserveAndUserDataBase() {
        for (Map.Entry<String, User> erasure : userBase.getUsersBase().entrySet()) {
            erasure.getValue().getUserReservedUnitList().clear();
        }
        reserveBase.getAllReserveSlots().clear();
    }

    @Test
    public void goodReserveTest(){
        assertTrue(coworkingSpaceController.reserveSlot(userOne,
                                                        hallBase.getHallBase().get(1),
                                                        reserveDateOne,
                                                        3));
        assertTrue(coworkingSpaceController.reserveSlot(userOne,
                                                        workplaceBase.getWorkplaceBase().get(4),
                                                        reserveDateTwo,
                                                        5));
        assertTrue(coworkingSpaceController.reserveSlot(adminUser,
                                                        workplaceBase.getWorkplaceBase().get(3),
                                                        reserveDateOne,
                                                        2));
        assertTrue(coworkingSpaceController.reserveSlot(adminUser,
                                                        hallBase.getHallBase().get(1),
                                                        reserveDateTwo,
                                                        2));
    }

    @Test
    public void duplicateExistingReserveExceptionTest(){
        coworkingSpaceController.reserveSlot(userOne,
                                             hallBase.getHallBase().get(1),
                                             reserveDateOne, 1);
        coworkingSpaceController.reserveSlot(adminUser,
                                             workplaceBase.getWorkplaceBase().get(2),
                                             reserveDateTwo, 8);
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.reserveSlot(userOne,
                                                           hallBase.getHallBase().get(1),
                                                           reserveDateOne,
                                                 1));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.reserveSlot(adminUser,
                                                           hallBase.getHallBase().get(1),
                                                           reserveDateOne,
                                                 1));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.reserveSlot(adminUser,
                                                           workplaceBase.getWorkplaceBase().get(2),
                                                           reserveDateTwo, 8));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.reserveSlot(userOne,
                                                           workplaceBase.getWorkplaceBase().get(2),
                                                           reserveDateTwo,
                                                 8));

    }

    @Test
    public void nonExistentUserReserveExceptionTest(){
        assertThrows(IllegalArgumentException.class,
                () -> coworkingSpaceController.reserveSlot(new User("BadHacker"),
                        hallBase.getHallBase().get(1),
                        reserveDateOne,
                        1));
    }

    @Test
    public void goodRemoveReservePlacesTest(){
        coworkingSpaceController.reserveSlot(userOne, hallBase.getHallBase().get(1),
                                             reserveDateOne,3);
        coworkingSpaceController.reserveSlot(userOne, workplaceBase.getWorkplaceBase().get(4),
                                             reserveDateTwo,5);
        coworkingSpaceController.reserveSlot(adminUser,
                                             workplaceBase.getWorkplaceBase().get(3),
                                             reserveDateOne,
                                   2);
        coworkingSpaceController.reserveSlot(adminUser,
                                             hallBase.getHallBase().get(1),
                                             reserveDateTwo,
                                   2);
        assertTrue(coworkingSpaceController.removeReserveSlot(userOne, hallBase.getHallBase().get(1),
                                                              reserveDateOne,3));
        assertTrue(coworkingSpaceController.removeReserveSlot(userOne, workplaceBase.getWorkplaceBase().get(4),
                                                              reserveDateTwo,5));
        assertTrue(coworkingSpaceController.removeReserveSlot(adminUser,
                                                              workplaceBase.getWorkplaceBase().get(3),
                                                              reserveDateOne,
                                                    2));
        assertTrue(coworkingSpaceController.removeReserveSlot(adminUser,
                                                              hallBase.getHallBase().get(1),
                                                              reserveDateTwo,
                                                    2));
    }

    @Test
    public void nonExistentUserRemoveReserveExceptionTest(){
        assertThrows(IllegalArgumentException.class,
                () -> coworkingSpaceController.reserveSlot(new User("BadHacker"),
                        hallBase.getHallBase().get(1),
                        reserveDateOne,
                        1));
    }

    @Test
    public void removeNonExistentReservePlacesExceptionByOwnerTest(){
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(userOne,
                                                                 workplaceBase.getWorkplaceBase().get(2),
                                                                 reserveDateTwo,
                                                       8));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(userOne, hallBase.getHallBase().get(1),
                                                                 reserveDateOne,3));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(adminUser,
                                                                 workplaceBase.getWorkplaceBase().get(3),
                                                                 reserveDateOne,
                                                       2));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(adminUser,
                                                                 hallBase.getHallBase().get(1),
                                                                 reserveDateTwo,
                                                       2));
    }

    @Test
    public void removeOtherUserExistentReservePlacesExceptionTest(){
        coworkingSpaceController.reserveSlot(userOne, hallBase.getHallBase().get(1),
                                             reserveDateOne,3);
        coworkingSpaceController.reserveSlot(userOne, workplaceBase.getWorkplaceBase().get(4),
                                             reserveDateTwo,5);
        coworkingSpaceController.reserveSlot(adminUser,
                                             workplaceBase.getWorkplaceBase().get(3),
                                             reserveDateOne,
                                   2);
        coworkingSpaceController.reserveSlot(adminUser,
                                             hallBase.getHallBase().get(1),
                                             reserveDateTwo,
                                   2);
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(adminUser,
                        workplaceBase.getWorkplaceBase().get(2),
                        reserveDateTwo,
                        8));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(adminUser, hallBase.getHallBase().get(1),
                        reserveDateOne,3));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(userOne,
                        workplaceBase.getWorkplaceBase().get(3),
                        reserveDateOne,
                        2));
        assertThrows(RuntimeException.class,
                () -> coworkingSpaceController.removeReserveSlot(userOne,
                        hallBase.getHallBase().get(1),
                        reserveDateTwo,
                        2));
    }
}