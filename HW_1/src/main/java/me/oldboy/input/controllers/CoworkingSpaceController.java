package me.oldboy.input.controllers;

import me.oldboy.input.entity.ReserveUnit;
import me.oldboy.input.entity.Place;
import me.oldboy.input.entity.User;
import me.oldboy.input.repository.ReserveBase;
import me.oldboy.input.repository.UserBase;

import java.time.LocalDate;

/**
 * Controller for reserve and remove reserve slots.
 */
public class CoworkingSpaceController {

    private ReserveBase reserveBase;
    private UserBase userBase;

    public CoworkingSpaceController(ReserveBase reserveBase, UserBase userBase) {
        this.reserveBase = reserveBase;
        this.userBase = userBase;
    }

    /**
     * New slot reservation.
     *
     * @param user          the user making the reserve one slot
     * @param place         the hall or workplace for reservation
     * @param reserveDate   the date for reservation
     * @param slotNumber    the number of reservation slot
     * @throws RuntimeException if the slot is already reserved
     * @throws IllegalArgumentException if user have no registration in coworking system
     * @return true if slot was reserved, false if reservation failed
     */
    public boolean reserveSlot(User user, Place place, LocalDate reserveDate, Integer slotNumber){
        /* Полной валидации не проводится, допускаем, что пользователь дисциплинирован */
        boolean mayBeReserve = false;
        ReserveUnit newReserveUnit = new ReserveUnit(reserveDate, place, slotNumber);
        Integer mayBeReserveKey = newReserveUnit.hashCode();
        if(userBase.getUsersBase().containsKey(user.getLogin())){
            if(!user.getUserReservedUnitList().containsKey(mayBeReserveKey)) {
                mayBeReserve = reserveBase.reserveSlot(user, newReserveUnit);
            } else {
                throw new RuntimeException("Такая бронь уже есть!");
            }
        } else {
            throw new IllegalArgumentException("Пользователь не зарегистрирован!");
        }
        return mayBeReserve;
    }

    /**
     * Existing slot remove reservation.
     *
     * @param user          the user making the reserve this slot
     * @param place         the hall or workplace witch reservation need remove
     * @param reserveDate   the reservation date
     * @param slotNumber    the number of reservation slot for remove
     * @throws RuntimeException if the slot is not exist
     * @throws IllegalArgumentException if user have no registration in coworking system
     * @return true if reservation of slot was remove, false if removing of reservation failed
     */
    public boolean removeReserveSlot(User user, Place place, LocalDate reserveDate, Integer slotNumber){
        /* Полной валидации не проводится, допускаем, что пользователь дисциплинирован */
        boolean mayBeRemove = false;
        ReserveUnit removeReserveUnit = new ReserveUnit(reserveDate, place, slotNumber);
        Integer mayBeRemoveKey = removeReserveUnit.hashCode();
        if(userBase.getUsersBase().containsKey(user.getLogin())){
            if(user.getUserReservedUnitList().containsKey(mayBeRemoveKey)){
                  mayBeRemove = reserveBase.removeReserveSlot(user, removeReserveUnit);
            } else {
                throw new RuntimeException("Такой брони не существует!");
            }
        } else {
            throw new IllegalArgumentException("Пользователь не зарегистрирован!");
        }
        return mayBeRemove;
    }
}
