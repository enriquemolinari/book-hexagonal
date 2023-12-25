package hexagon.secondary.port;

public interface ForSendingEmailNotifications {

	void send(String to, String subject, String body);

}
