package uga.csx370.criticalhit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samskivert.mustache.Mustache;

/**
 * Makes Mustache forgiving: missing/null variables render as empty text
 * instead of crashing the page, and empty strings / zero count as false
 * in section blocks.
 */
@Configuration
public class MustacheConfig {

    @Bean
    public Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader templateLoader) {
        return Mustache.compiler()
                .withLoader(templateLoader)
                .defaultValue("")
                .emptyStringIsFalse(true)
                .zeroIsFalse(true);
    }
}
