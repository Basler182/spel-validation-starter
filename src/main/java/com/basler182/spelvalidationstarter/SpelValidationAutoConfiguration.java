package com.basler182.spelvalidationstarter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SpelValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpelParameterNameAspect spelParameterNameAspect() {
        return new SpelParameterNameAspect();
    }
}
