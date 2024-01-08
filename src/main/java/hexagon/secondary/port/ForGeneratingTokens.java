package hexagon.secondary.port;

import java.util.Map;

public interface ForGeneratingTokens {
    String tokenFrom(Map<String, Object> payload);

    String verifyAndGetUserIdFrom(String token);
}
