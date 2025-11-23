package eu.schk.spelvalidationstarter;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpelAssertTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @SpelAssert(value = "start.isBefore(end)", message = "Start must be before End")
    @SpelAssert(value = "param != null && param.length() > 5", applyTo = "param")
    static class TimeRange {
        LocalDate start;
        LocalDate end;
        String param;

        TimeRange(LocalDate start, LocalDate end, String param) {
            this.start = start;
            this.end = end;
            this.param = param;
        }

        public LocalDate getStart() {
            return start;
        }

        public LocalDate getEnd() {
            return end;
        }

        public String getParam() {
            return param;
        }
    }

    @Test
    void testValidObject() {
        TimeRange valid = new TimeRange(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "longerString"
        );
        assertTrue(validator.validate(valid).isEmpty());
    }

    @Test
    void testInvalidDateOrder() {
        TimeRange invalid = new TimeRange(
                LocalDate.now().plusDays(1),
                LocalDate.now(),
                "longerString"
        );

        var violations = validator.validate(invalid);
        assertEquals(1, violations.size());
        assertEquals("Start must be before End", violations.iterator().next().getMessage());
    }

    @Test
    void testFieldMapping() {
        TimeRange invalid = new TimeRange(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "tiny"
        );

        var violations = validator.validate(invalid);
        assertEquals(1, violations.size());
        assertEquals("param", violations.iterator().next().getPropertyPath().toString());
    }
}