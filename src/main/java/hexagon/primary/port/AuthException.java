package hexagon.primary.port;

public class AuthException extends RuntimeException {
	public AuthException(String msg, Exception e) {
		super(msg, e);
	}

	public AuthException(String msg) {
		super(msg);
	}

}
