package me.oldboy.output.dto;

import me.oldboy.input.repository.HallBase;
import me.oldboy.input.repository.ReserveBase;
import me.oldboy.input.repository.UserBase;
import me.oldboy.input.repository.WorkplaceBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReadFreeSlotsByDateDto.
 */
class ReadFreeSlotsByDateDtoTest {

    private static ReserveBase reserveBase;
    private static HallBase hallBase;
    private static WorkplaceBase workplaceBase;
    private static UserBase userBase;
    private static ReadFreeSlotsByDateDto readFreeSlotsByDateDto;

    @BeforeAll
    public static void setUp() {
        hallBase = new HallBase();
        hallBase.initHallBase();
        workplaceBase = new WorkplaceBase();
        workplaceBase.initPlaceBase();
        userBase = new UserBase();
        userBase.initBaseAdmin();
        reserveBase = new ReserveBase(userBase);
        readFreeSlotsByDateDto =
                new ReadFreeSlotsByDateDto(reserveBase, hallBase, workplaceBase);
    }

    @Test
    void viewFreeSlots() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));
        readFreeSlotsByDateDto.viewFreeSlots(LocalDate.of(1999,06,06));
        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        assertTrue(allWrittenLines.contains("На 1999-06-06 все залы и рабочие места свободы!"));
        assertTrue(allWrittenLines.contains("Милости просим!"));
    }
}