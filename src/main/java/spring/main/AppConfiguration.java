package spring.main;

import hexagon.primary.port.CinemaSystem;
import hexagon.primary.port.DateTimeProvider;
import infra.secondary.jpa.TxJpaCinema;
import infra.secondary.mail.TheBestProviderForSendingEmailNotifications;
import infra.secondary.payment.PleasePayProviderForManagingPayment;
import infra.secondary.token.PasetoForGeneratingTokens;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    // this secret should not be here
    private static final String SECRET = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

    @Bean
    @Profile("default")
    public CinemaSystem create() {
        new SetUpDb(entityManagerFactory)
                .createSchemaAndPopulateSampleData();
        return new TxJpaCinema(entityManagerFactory, new PleasePayProviderForManagingPayment(),
                new TheBestProviderForSendingEmailNotifications(),
                new PasetoForGeneratingTokens(SECRET), DateTimeProvider.create(),
                2 /*
         * page size
         */);
    }
}
