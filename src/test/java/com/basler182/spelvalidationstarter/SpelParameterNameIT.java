package com.basler182.spelvalidationstarter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SpelParameterNameIT {

    @Autowired
    private CalendarService calendarService;

    @Configuration
    @EnableAutoConfiguration
    static class Config {
        @org.springframework.context.annotation.Bean
        public CalendarService calendarService() {
            return new CalendarService();
        }
    }

    @Service
    @Validated
    static class CalendarService {

        @SpelAssert(value = "#start.isBefore(#end)", message = "Start must be before End")
        public void schedule(LocalDate start, LocalDate end) {
        }

        @SpelAssert(value = "#args[0].isBefore(#args[1])", message = "Start must be before End using args")
        public void scheduleLegacy(LocalDate start, LocalDate end) {
        }
    }

    @Test
    void testParameterNames_Success() {
        calendarService.schedule(LocalDate.now(), LocalDate.now().plusDays(1));
    }

    @Test
    void testParameterNames_Failure() {
        try {
            calendarService.schedule(LocalDate.now().plusDays(1), LocalDate.now());
        } catch (jakarta.validation.ConstraintViolationException e) {
            assertEquals(1, e.getConstraintViolations().size());
            assertEquals("Start must be before End", e.getConstraintViolations().iterator().next().getMessage());
            return;
        }
        throw new AssertionError("Should have thrown ConstraintViolationException");
    }

    @Test
    void testLegacyArgs_Success() {
        calendarService.scheduleLegacy(LocalDate.now(), LocalDate.now().plusDays(1));
    }
}
