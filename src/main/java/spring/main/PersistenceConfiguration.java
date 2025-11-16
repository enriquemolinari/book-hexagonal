package spring.main;

import infra.secondary.jpa.EmfBuilder;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
public class PersistenceConfiguration {

    @Bean
    @Profile("default")
    public EntityManagerFactory createEmf() {
        return new EmfBuilder()
                .memory()
                .withDropAndCreateDDL()
                .build();
    }
}
