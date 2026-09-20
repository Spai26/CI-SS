package com.cuidar.api.common.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Enables binding of {@link JwtProperties} from {@code app.security.jwt.*}.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityPropertiesConfig {
}