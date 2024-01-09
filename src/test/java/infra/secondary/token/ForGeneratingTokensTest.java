package infra.secondary.token;

import dev.paseto.jpaseto.lang.Keys;
import hexagon.primary.port.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ForGeneratingTokensTest {

    private String encodedKey;

    @BeforeEach
    public void before() {
        SecretKey key = Keys.secretKey();
        encodedKey = Base64.getEncoder()
                .encodeToString(key.getEncoded());
    }

    @Test
    public void validToken() {
        var token = new PasetoForGeneratingTokens(LocalDateTime::now, encodedKey,
                60 * 1000);
        var stringToken = token.tokenFrom(Map.of("id", "1"));
        assertEquals("1", token.verifyAndGetUserIdFrom(stringToken));
    }

    @Test
    public void expiredToken() {
        var token = new PasetoForGeneratingTokens(() -> LocalDateTime.now().minusMinutes(5),
                encodedKey,
                60 * 1000);
        var stringToken = token.tokenFrom(Map.of("id", "1"));

        var e = assertThrows(AuthException.class, () -> {
            token.verifyAndGetUserIdFrom(stringToken);
            fail("I have obtained the user id from an expired token");
        });

        assertEquals(PasetoForGeneratingTokens.INVALID_TOKEN, e.getMessage());
    }
}
