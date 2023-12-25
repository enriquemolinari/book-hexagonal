package hexagon.primary.port;

public class BusinessException extends RuntimeException {

	public BusinessException(String msg, Exception e) {
		super(msg, e);
	}

	public BusinessException(String msg) {
		super(msg);
	}
}
