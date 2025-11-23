package eu.schk.spelvalidationstarter;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpelAssertSpringIntegrationTest {

    @Autowired
    private Validator validator;

    @Configuration
    @EnableAutoConfiguration
    static class Config {
        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    @Service("testService")
    static class TestService {
        public boolean isUnique(String username) {
            return !"admin".equals(username);
        }
    }

    @SpelAssert(
                value = "@testService.isUnique(username)",
                message = "Username already taken",
                applyTo = "username"
        )
    record UserRegistration(String username) {
    }

    @Test
    void testBeanAccessInSpel_Success() {
        UserRegistration user = new UserRegistration("newUser123");

        var violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Should be valid because 'newUser123' is not 'admin'");
    }

    @Test
    void testBeanAccessInSpel_Failure() {
        UserRegistration user = new UserRegistration("admin");

        var violations = validator.validate(user);

        assertEquals(1, violations.size());
        var violation = violations.iterator().next();

        assertEquals("Username already taken", violation.getMessage());
        assertEquals("username", violation.getPropertyPath().toString());
    }
}