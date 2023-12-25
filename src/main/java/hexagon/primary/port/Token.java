package hexagon.primary.port;

import java.util.Map;

public interface Token {
	String tokenFrom(Map<String, Object> payload);

	Long verifyAndGetUserIdFrom(String token);
}
