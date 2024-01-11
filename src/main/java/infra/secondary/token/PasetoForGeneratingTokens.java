package infra.secondary.token;

import dev.paseto.jpaseto.Paseto;
import dev.paseto.jpaseto.Pasetos;
import dev.paseto.jpaseto.lang.Keys;
import hexagon.secondary.port.ForGeneratingTokens;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;

public class PasetoForGeneratingTokens implements ForGeneratingTokens {
    static final String INVALID_TOKEN = "Invalid token. You have to login.";
    private final byte[] base64Secret;
    private static final long defaultMilliSecondsSinceNow = 60 * 60 * 1000; // 1
    // hs
    private final Supplier<LocalDateTime> dateProvider;
    private final Long milliSecondsSinceNow;

    public PasetoForGeneratingTokens(String base64Secret) {
        this(LocalDateTime::now, base64Secret,
                defaultMilliSecondsSinceNow);
    }

    public PasetoForGeneratingTokens(Supplier<LocalDateTime> dateProvider, String base64Secret,
                                     long milliSecondsSinceNow) {
        this.dateProvider = dateProvider;
        this.base64Secret = Base64.getDecoder().decode(base64Secret);
        this.milliSecondsSinceNow = milliSecondsSinceNow;
    }

    private Long expiration() {
        return (dateProvider.get().atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli() + this.milliSecondsSinceNow) / 1000;
    }

    @Override
    public String tokenFrom(Map<String, Object> payload) {
        var pb = Pasetos.V2.LOCAL.builder();
        payload.forEach((key, value) -> pb.claim(key, value));
        pb.setExpiration(Instant.ofEpochSecond(this.expiration()));
        return pb.setSharedSecret(Keys.secretKey(this.base64Secret)).compact();
    }

    @Override
    public String verifyAndGetUserIdFrom(String token) {
        Paseto tk;
        try {
            tk = Pasetos.parserBuilder()
                    .setSharedSecret(Keys.secretKey(this.base64Secret)).build()
                    .parse(token);
            return tk.getClaims().get("id", String.class);
        } catch (Exception ex) {
            throw new RuntimeException(INVALID_TOKEN);
        }
    }
}
