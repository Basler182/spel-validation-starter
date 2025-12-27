package com.basler182.spelvalidationstarter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Validator that evaluates a SpEL expression to validate an object.
 */
@SupportedValidationTarget({ ValidationTarget.ANNOTATED_ELEMENT, ValidationTarget.PARAMETERS })
public class SpelAssertValidator implements ConstraintValidator<SpelAssert, Object> {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private String spelExpression;
    private String applyToField;

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Override
    public void initialize(SpelAssert constraintAnnotation) {
        this.spelExpression = constraintAnnotation.value();
        this.applyToField = constraintAnnotation.applyTo();
    }

    /**
     * Validates the given value against the SpEL expression.
     * 
     * @param value   the object to validate
     * @param context the constraint validator context
     * @return true if the value is valid, false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(value);

        if (value instanceof Object[] args) {
            evaluationContext.setVariable("args", value);
            String[] paramNames = SpelContextHolder.peekParameterNames();
            if (paramNames != null && paramNames.length == args.length) {
                for (int i = 0; i < paramNames.length; i++) {
                    evaluationContext.setVariable(paramNames[i], args[i]);
                }
            }
        }

        if (applicationContext != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
        }

        try {
            Expression expression = PARSER.parseExpression(spelExpression);
            Boolean result = expression.getValue(evaluationContext, Boolean.class);

            if (Boolean.TRUE.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to evaluate SpEL expression: '" + spelExpression + "'", e);
        }

        if (applyToField != null && !applyToField.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(applyToField)
                    .addConstraintViolation();
        }

        return false;
    }
}