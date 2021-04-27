package org.iproduct.spring.restmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.iproduct.spring.restmvc.service")
@PropertySource("classpath:application.properties")
@Import({ SpringSecurityConfig.class, MongoConfig.class })
public class TestRootConfig {
}
