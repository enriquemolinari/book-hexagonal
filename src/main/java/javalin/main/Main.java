package javalin.main;

import hexagon.primary.port.DateTimeProvider;
import infra.primary.javalin.web.CinemaSystemController;
import infra.secondary.jpa.TxJpaCinema;
import infra.secondary.mail.TheBestProviderForSendingEmailNotifications;
import infra.secondary.payment.PleasePayProviderForManagingPayment;
import infra.secondary.token.PasetoForGeneratingTokens;
import jakarta.persistence.Persistence;
import spring.main.SetUpDb;

public class Main {

    public static void main(String[] args) {
        // this secret should not be here
        String SECRET = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";
        var emf = Persistence
                .createEntityManagerFactory("derby-cinema");
        new SetUpDb(emf).createSchemaAndPopulateSampleData();
        var cinema = new TxJpaCinema(emf, new PleasePayProviderForManagingPayment(),
                new TheBestProviderForSendingEmailNotifications(),
                new PasetoForGeneratingTokens(SECRET), DateTimeProvider.create(), 10 /* page size */);
        new CinemaSystemController(8080, cinema).start();
    }
}
