package me.oldboy.input.repository;

import me.oldboy.input.entity.Hall;
import me.oldboy.input.entity.ReserveUnit;
import me.oldboy.input.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for HallBase.
 */
class HallBaseTest {

    private static HallBase hallBase;
    private static UserBase userBase;
    private static ReserveBase reserveBase;
    private static User simpleUser;
    private static User adminUser;
    private static LocalDate reserveDate;
    private static Integer numberOfExistingAndReserveHallForCrudOperation;
    private static Integer numberOfExistingAndNotReserveHallForCrudOperation;
    private static Integer numberOfNotExistingHallForCrudOperation;

    @BeforeAll
    public static void setUp() {
        userBase = new UserBase();
        reserveBase = new ReserveBase(userBase);
        hallBase = new HallBase();
        hallBase.initHallBase();
        hallBase.setReserveBase(reserveBase);
        userBase.initBaseAdmin();
        simpleUser = new User("User");
        userBase.getUsersBase().put(simpleUser.getLogin(), simpleUser);
        adminUser = userBase.getUsersBase().get("Admin");
    }

    @BeforeEach
    public void createReserveUnitForExistingHall(){
        numberOfExistingAndReserveHallForCrudOperation = 2;
        numberOfExistingAndNotReserveHallForCrudOperation = 1;
        numberOfNotExistingHallForCrudOperation = 5;
        reserveDate = LocalDate.of(2024, 06,9);
        Integer reserveSlot = 2;
        Hall hall = hallBase.getHallBase().get(numberOfExistingAndReserveHallForCrudOperation);
        ReserveUnit reserveUnit = new ReserveUnit(reserveDate, hall, reserveSlot);
        Integer reserveUnitKey = reserveUnit.hashCode();
        Map<Integer, ReserveUnit> reserveUnitMapByConcreteDate = new HashMap<>();
        reserveUnitMapByConcreteDate.put(reserveUnitKey, reserveUnit);
        reserveBase.getAllReserveSlots().put(reserveDate, reserveUnitMapByConcreteDate);
    }

    @AfterEach
    public void cleanReserveAndHallDataBase() {
        reserveBase.getAllReserveSlots().clear();
        hallBase.getHallBase().clear();
        hallBase.initHallBase();
    }

    // Create tests
    @Test
    public void createNonExistingHallByAdminTest(){
        Hall mayBeNewHall = hallBase.createHall(adminUser, numberOfNotExistingHallForCrudOperation);
        assertEquals(mayBeNewHall, hallBase.getHallBase().get(numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void createNonExistingHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.createHall(simpleUser, numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void createAnExistingHallByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.createHall(adminUser, numberOfExistingAndReserveHallForCrudOperation));
        assertThrows(RuntimeException.class,
                () -> hallBase.createHall(adminUser, numberOfExistingAndNotReserveHallForCrudOperation));
    }

    // Remove tests
    @Test
    public void removeAnExistingAndNotReserveHallByAdminTest(){
        Hall mayBeRemoveHall =
                hallBase.getHallBase().get(numberOfExistingAndNotReserveHallForCrudOperation);
        assertEquals(mayBeRemoveHall,
                hallBase.removeHall(adminUser, numberOfExistingAndNotReserveHallForCrudOperation));
    }

    @Test
    public void removeExistingAndReserveHallByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.removeHall(adminUser, numberOfExistingAndReserveHallForCrudOperation));
    }

    @Test
    public void removeNonExistingHallByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.removeHall(adminUser, numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void removeAnExistingAndNotReserveHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.removeHall(simpleUser, numberOfExistingAndNotReserveHallForCrudOperation));
    }

    @Test
    public void removeExistingAndReserveHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.removeHall(simpleUser, numberOfExistingAndReserveHallForCrudOperation));
    }

    @Test
    public void removeNonExistingHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.removeHall(simpleUser, numberOfNotExistingHallForCrudOperation));
    }

    // Update tests
    @Test
    public void updateAnExistingAndNotReserveHallByAdminTest(){
        Hall mayBeUpdateHall =
                hallBase.updateHall(adminUser,
                                    numberOfExistingAndNotReserveHallForCrudOperation,
                                    numberOfNotExistingHallForCrudOperation);
        assertEquals(mayBeUpdateHall,
                hallBase.getHallBase().get(numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void updateAnExistingAndReserveHallByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.updateHall(adminUser,
                                          numberOfExistingAndReserveHallForCrudOperation,
                                          numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void updateNonExistingHallByAdminExceptionTest(){
        Integer newNumberForUpdate = numberOfNotExistingHallForCrudOperation + 1;
        assertThrows(RuntimeException.class,
                () -> hallBase.updateHall(adminUser,
                                          numberOfNotExistingHallForCrudOperation,
                                          newNumberForUpdate));
    }

    @Test
    public void updateAnExistingAndNotReserveHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.updateHall(simpleUser,
                        numberOfExistingAndNotReserveHallForCrudOperation,
                        numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void updateExistingAndReserveHallBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.updateHall(simpleUser,
                                          numberOfExistingAndReserveHallForCrudOperation,
                                          numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void updateNonExistingHallBySimpleUserExceptionTest(){
        Integer newNumberForUpdate = numberOfNotExistingHallForCrudOperation + 1;
        assertThrows(RuntimeException.class,
                () -> hallBase.updateHall(simpleUser,
                                          numberOfNotExistingHallForCrudOperation,
                                          newNumberForUpdate));
    }

    // Read tests
    @Test
    public void readExistingHallTest(){
        Hall mayBeReadHall =
                hallBase.readHall(numberOfExistingAndNotReserveHallForCrudOperation);
        assertEquals(mayBeReadHall,
                hallBase.getHallBase().get(numberOfExistingAndNotReserveHallForCrudOperation));
        Hall mayBeReadHallTwo =
                hallBase.readHall(numberOfExistingAndReserveHallForCrudOperation);
        assertEquals(mayBeReadHallTwo,
                hallBase.getHallBase().get(numberOfExistingAndReserveHallForCrudOperation));
    }

    @Test
    public void readNonExistingHallExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> hallBase.readHall(numberOfNotExistingHallForCrudOperation));
    }

    @Test
    public void stringViewHallBaseWithAddNewHallTest(){
        Integer lengthBeforeAddition = hallBase.stringViewHallBase().length();
        Integer newHall = numberOfNotExistingHallForCrudOperation;
        hallBase.getHallBase().put(newHall, new Hall(newHall));
        Integer newLengthAfterAddNewHall = hallBase.stringViewHallBase().length();
        assertTrue(newLengthAfterAddNewHall > lengthBeforeAddition);
    }

    @Test
    public void stringViewHallBaseWithRemoveHallTest(){
        Integer lengthBeforeDelete = hallBase.stringViewHallBase().length();
        hallBase.getHallBase().remove(numberOfExistingAndNotReserveHallForCrudOperation);
        Integer newLengthAfterDeleteHall = hallBase.stringViewHallBase().length();
        assertTrue(newLengthAfterDeleteHall < lengthBeforeDelete);
    }
}
