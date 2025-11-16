package spring.web;

import infra.secondary.jpa.EmfBuilder;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class PersistenceTestConfiguration {
    @Bean
    public EntityManagerFactory createForTests() {
        return new EmfBuilder()
                .memory()
                .withDropAndCreateDDL()
                .build();
    }
}
