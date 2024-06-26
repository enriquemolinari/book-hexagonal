package infra.primary.javalin.web;

import hexagon.primary.port.AuthException;
import hexagon.primary.port.CinemaSystem;
import infra.primary.spring.web.LoginRequest;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CinemaSystemController {

    private static final String TOKEN_COOKIE_NAME = "token";
    public static final String AUTHENTICATION_REQUIRED = "You must be logged in to perform this action...";
    private final int webPort;
    private final CinemaSystem cinema;
    private Javalin javalinApp;

    public CinemaSystemController(int webPort, CinemaSystem cinema) {
        this.webPort = webPort;
        this.cinema = cinema;
    }

    public void start() {
        javalinApp = Javalin.create();
        javalinApp.post("/login", login());
        javalinApp.post("/logout", logout());
        javalinApp.post("/movies/{id}/rate", rateMovie());
        javalinApp.get("/shows/{id}", showDetail());
        // finish with the other endpoints

        javalinApp.exception(AuthException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
            // log error in a stream...
        });

        javalinApp.exception(Exception.class, (e, ctx) -> {
            ctx.json(
                    Map.of("message",
                            e.getMessage()));
            // log error in a stream...
        }).start(this.webPort);
    }

    public void stop() {
        this.javalinApp.stop();
    }

    private Handler rateMovie() {
        return ctx -> {
            var token = ctx.cookie(TOKEN_COOKIE_NAME);
            ifAuthenticatedDo(token, userId -> {
                var r = ctx.bodyAsClass(RateRequest.class);
                var rated = this.cinema.rateMovieBy(userId,
                        ctx.pathParamAsClass("id", String.class).get(),
                        r.rateValue(), r.comment());
                return ctx.json(rated);
            });
        };
    }

    private Handler login() {
        return ctx -> {
            var r = ctx.bodyAsClass(LoginRequest.class);

            var token = this.cinema.login(r.username(), r.password());
            var profile = cinema.profileFrom(cinema.userIdFrom(token));

            ctx.res().setHeader("Set-Cookie",
                    TOKEN_COOKIE_NAME + "=" + token + ";path=/; HttpOnly; ");

            ctx.json(profile);
        };
    }

    private Handler logout() {
        return ctx -> {
            // want register login/logout time?
            // just remove the token cookie
            ctx.removeCookie(TOKEN_COOKIE_NAME);
            ctx.json(Map.of("result", "success"));
        };
    }

    private <S> S ifAuthenticatedDo(String token, Function<String, S> method) {
        var userId = Optional.ofNullable(token).map(this.cinema::userIdFrom)
                .orElseThrow(() -> new AuthException(
                        AUTHENTICATION_REQUIRED));
        return method.apply(userId);
    }

    private Handler showDetail() {
        return ctx -> {
            ctx.json(
                    this.cinema.show(
                            ctx.pathParamAsClass("id", String.class).get()));
        };
    }
}
