package spring.main;

import hexagon.primary.port.CinemaSystem;
import hexagon.primary.port.DateTimeProvider;
import infra.secondary.jpa.TxJpaCinema;
import infra.secondary.mail.TheBestEmailProvider;
import infra.secondary.payment.PleasePayPaymentProvider;
import infra.secondary.token.PasetoToken;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.YearMonth;

@Configuration
public class AppConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    // this secret should not be here
    private static String SECRET = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

    @Bean
    @Profile("default")
    public CinemaSystem create() {
        addSampleData();
        return new TxJpaCinema(entityManagerFactory, new PleasePayPaymentProvider(),
                new TheBestEmailProvider(),
                new PasetoToken(SECRET), DateTimeProvider.create(), 10 /*
         * page size
         */);
    }

    @Bean
    @Profile("test")
    public CinemaSystem createForTest() {
        String ANY_SECRET = "Kdj5zuBIBBgcWpv9zjKOINl2yUKUXVKO+SkOVE3VuZ4=";
        addSampleData();
        return new TxJpaCinema(entityManagerFactory,
                (String creditCardNumber, YearMonth expire, String securityCode,
                 float totalAmount) -> {
                },
                (String to, String subject, String body) -> {
                },
                new PasetoToken(ANY_SECRET), DateTimeProvider.create(),
                2 /*
         * page size
         */);
    }

    private void addSampleData() {
        new SetUpDb(entityManagerFactory)
                .createSchemaAndPopulateSampleData();
    }
}
