package me.oldboy.input.repository;

import me.oldboy.input.entity.ReserveUnit;
import me.oldboy.input.entity.User;
import me.oldboy.input.entity.Workplace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WorkplaceBase.
 */
class WorkplaceBaseTest {

    private static WorkplaceBase workplaceBase;
    private static UserBase userBase;
    private static ReserveBase reserveBase;
    private static User simpleUser;
    private static User adminUser;
    private static LocalDate reserveDate;
    private static Integer numberOfExistingAndReservePlaceForCrudOperation;
    private static Integer numberOfExistingAndNotReservePlaceForCrudOperation;
    private static Integer numberOfNotExistingPlaceForCrudOperation;

    @BeforeAll
    public static void setUp() {
        userBase = new UserBase();
        reserveBase = new ReserveBase(userBase);
        workplaceBase = new WorkplaceBase();
        workplaceBase.initPlaceBase();
        workplaceBase.setReserveBase(reserveBase);
        userBase.initBaseAdmin();
        simpleUser = new User("User");
        userBase.getUsersBase().put(simpleUser.getLogin(), simpleUser);
        adminUser = userBase.getUsersBase().get("Admin");
    }

    @BeforeEach
    public void createReserveUnitForExistingWorkplace(){
        numberOfExistingAndReservePlaceForCrudOperation = 2;
        numberOfExistingAndNotReservePlaceForCrudOperation = 1;
        numberOfNotExistingPlaceForCrudOperation = 23;
        reserveDate = LocalDate.of(2024, 06,9);
        Integer reserveSlot = 6;
        Workplace workplace =
                workplaceBase.getWorkplaceBase().get(numberOfExistingAndReservePlaceForCrudOperation);
        ReserveUnit reserveUnit = new ReserveUnit(reserveDate, workplace, reserveSlot);
        Integer reserveUnitKey = reserveUnit.hashCode();
        Map<Integer, ReserveUnit> reserveUnitMapByConcreteDate = new HashMap<>();
        reserveUnitMapByConcreteDate.put(reserveUnitKey, reserveUnit);
        reserveBase.getAllReserveSlots().put(reserveDate, reserveUnitMapByConcreteDate);
    }

    @AfterEach
    public void cleanReserveAndWorkplacesDataBase() {
        reserveBase.getAllReserveSlots().clear();
        workplaceBase.getWorkplaceBase().clear();
        workplaceBase.initPlaceBase();
    }

    // Create tests
    @Test
    public void createNonExistingWorkplaceByAdminTest(){
        Workplace mayBeNewWorkplace =
                workplaceBase.createWorkPlace(adminUser, numberOfNotExistingPlaceForCrudOperation);
        assertEquals(mayBeNewWorkplace,
                workplaceBase.getWorkplaceBase().get(numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void createNonExistingWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.createWorkPlace(simpleUser, numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void createAnExistingWorkplaceByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.createWorkPlace(adminUser, numberOfExistingAndReservePlaceForCrudOperation));
        assertThrows(RuntimeException.class,
                () -> workplaceBase.createWorkPlace(adminUser, numberOfExistingAndNotReservePlaceForCrudOperation));
    }

    // Remove tests
    @Test
    public void removeAnExistingAndNotReserveWorkplaceByAdminTest(){
        Workplace mayBeRemoveWorkplace =
                workplaceBase.getWorkplaceBase().get(numberOfExistingAndNotReservePlaceForCrudOperation);
        assertEquals(mayBeRemoveWorkplace,
                workplaceBase.removeWorkPlace(adminUser, numberOfExistingAndNotReservePlaceForCrudOperation));
    }

    @Test
    public void removeExistingAndReserveWorkplaceByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.removeWorkPlace(adminUser, numberOfExistingAndReservePlaceForCrudOperation));
    }

    @Test
    public void removeNonExistingWorkplaceByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.removeWorkPlace(adminUser, numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void removeAnExistingAndNotReserveWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.removeWorkPlace(simpleUser, numberOfExistingAndNotReservePlaceForCrudOperation));
    }

    @Test
    public void removeExistingAndReserveWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.removeWorkPlace(simpleUser, numberOfExistingAndReservePlaceForCrudOperation));
    }

    @Test
    public void removeNonExistingWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.removeWorkPlace(simpleUser, numberOfNotExistingPlaceForCrudOperation));
    }

    // Update tests
    @Test
    public void updateAnExistingAndNotReserveWorkplaceByAdminTest(){
        Workplace mayBeUpdateHall =
                workplaceBase.updateWorkPlace(adminUser,
                        numberOfExistingAndNotReservePlaceForCrudOperation,
                        numberOfNotExistingPlaceForCrudOperation);
        assertEquals(mayBeUpdateHall,
                workplaceBase.getWorkplaceBase().get(numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void updateAnExistingAndReserveWorkplaceByAdminExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.updateWorkPlace(adminUser,
                        numberOfExistingAndReservePlaceForCrudOperation,
                        numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void updateNonExistingWorkplaceByAdminExceptionTest(){
        Integer newNumberForUpdate = numberOfNotExistingPlaceForCrudOperation + 1;
        assertThrows(RuntimeException.class,
                () -> workplaceBase.updateWorkPlace(adminUser,
                        numberOfNotExistingPlaceForCrudOperation,
                        newNumberForUpdate));
    }

    @Test
    public void updateAnExistingAndNotReserveWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.updateWorkPlace(simpleUser,
                        numberOfExistingAndNotReservePlaceForCrudOperation,
                        numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void updateExistingAndReserveWorkplaceBySimpleUserExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.updateWorkPlace(simpleUser,
                        numberOfExistingAndReservePlaceForCrudOperation,
                        numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void updateNonExistingWorkplaceBySimpleUserExceptionTest(){
        Integer newNumberForUpdate = numberOfNotExistingPlaceForCrudOperation + 1;
        assertThrows(RuntimeException.class,
                () -> workplaceBase.updateWorkPlace(simpleUser,
                        numberOfNotExistingPlaceForCrudOperation,
                                                    newNumberForUpdate));
    }

    // Read tests
    @Test
    public void readExistingWorkplaceTest(){
        Workplace mayBeReadHall =
                workplaceBase.readWorkPlace(numberOfExistingAndNotReservePlaceForCrudOperation);
        assertEquals(mayBeReadHall,
                workplaceBase.getWorkplaceBase().get(numberOfExistingAndNotReservePlaceForCrudOperation));
        Workplace mayBeReadHallTwo =
                workplaceBase.readWorkPlace(numberOfExistingAndReservePlaceForCrudOperation);
        assertEquals(mayBeReadHallTwo,
                workplaceBase.getWorkplaceBase().get(numberOfExistingAndReservePlaceForCrudOperation));
    }

    @Test
    public void readNonExistingWorkplaceExceptionTest(){
        assertThrows(RuntimeException.class,
                () -> workplaceBase.readWorkPlace(numberOfNotExistingPlaceForCrudOperation));
    }

    @Test
    public void stringViewWorkplaceBaseWithAddNewPlaceTest(){
        Integer lengthBeforeAddition = workplaceBase.stringViewWorkplaceBase().length();
        Integer newPlace = numberOfNotExistingPlaceForCrudOperation;
        workplaceBase.getWorkplaceBase().put(newPlace, new Workplace(newPlace));
        Integer newLengthAfterAddNewHall = workplaceBase.stringViewWorkplaceBase().length();
        assertTrue(newLengthAfterAddNewHall > lengthBeforeAddition);
    }

    @Test
    public void stringViewWorkplaceBaseWithRemovePlaceTest(){
        Integer lengthBeforeDelete = workplaceBase.stringViewWorkplaceBase().length();
        workplaceBase.getWorkplaceBase().remove(numberOfExistingAndNotReservePlaceForCrudOperation);
        Integer newLengthAfterDeleteHall = workplaceBase.stringViewWorkplaceBase().length();
        assertTrue(newLengthAfterDeleteHall < lengthBeforeDelete);
    }
}