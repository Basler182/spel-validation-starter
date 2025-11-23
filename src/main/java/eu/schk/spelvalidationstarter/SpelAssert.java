package eu.schk.spelvalidationstarter;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation that uses a SpEL expression to validate a class.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpelAssertValidator.class)
@Repeatable(SpelAssert.List.class)
public @interface SpelAssert {

    /**
     *
     * @return the validation message
     */
    String message() default "SpEL expression validation failed";

    /**
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     *
     * @return the payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The SpEL expression to be evaluated
     * @return the SpEL expression
     */
    String value();

    /**
     * Optional: The field name to which the violation should be applied
     * @return the field name
     */
    String applyTo() default "";

    /**
     * Container annotation for repeatable SpelAssert
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        SpelAssert[] value();
    }
}