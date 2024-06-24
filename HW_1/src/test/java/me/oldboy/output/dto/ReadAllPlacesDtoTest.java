package me.oldboy.output.dto;

import me.oldboy.input.repository.HallBase;
import me.oldboy.input.repository.WorkplaceBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReadAllPlacesDto.
 */
class ReadAllPlacesDtoTest {

    private static HallBase hallBase;
    private static WorkplaceBase workplaceBase;
    private static ReadAllPlacesDto readAllPlacesDto;

    @BeforeAll
    public static void setUp() {
        hallBase = new HallBase();
        hallBase.initHallBase();
        workplaceBase = new WorkplaceBase();
        workplaceBase.initPlaceBase();
        readAllPlacesDto = new ReadAllPlacesDto(hallBase, workplaceBase);
    }

    @Test
    void getAllPlaces() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));
        readAllPlacesDto.getAllPlaces();
        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        assertTrue(allWrittenLines.contains("Доступные рабочие места:"));
        assertTrue(allWrittenLines.contains("Доступные конференц-залы:"));
        assertTrue(allWrittenLines.contains(String.valueOf(hallBase.getHallBase()
                                                                   .get(1)
                                                                   .getNumber())));
        assertTrue(allWrittenLines.contains(String.valueOf(workplaceBase.getWorkplaceBase()
                                                                        .get(1)
                                                                        .getNumber())));
    }
}