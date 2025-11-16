package spring.web;

import hexagon.primary.port.CinemaSystem;
import hexagon.primary.port.DateTimeProvider;
import infra.secondary.jpa.TxJpaCinema;
import infra.secondary.token.PasetoForGeneratingTokens;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.YearMonth;

@Configuration
@Profile("test")
public class AppTestConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public CinemaSystem create() {
        String ANY_SECRET = "Kdj5zuBIBBgcWpv9zjKOINl2yUKUXVKO+SkOVE3VuZ4=";
        return new TxJpaCinema(entityManagerFactory,
                (String creditCardNumber, YearMonth expire, String securityCode,
                 float totalAmount) -> {
                },
                (String to, String subject, String body) -> {
                },
                new PasetoForGeneratingTokens(ANY_SECRET), DateTimeProvider.create(),
                2 /*
         * page size
         */);
    }
}