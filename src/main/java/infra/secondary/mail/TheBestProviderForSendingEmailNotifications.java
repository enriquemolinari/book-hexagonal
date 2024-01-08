package infra.secondary.mail;

import hexagon.secondary.port.ForSendingEmailNotifications;

public class TheBestProviderForSendingEmailNotifications implements ForSendingEmailNotifications {

    @Override
    public void send(String to, String subject, String body) {
        // mails sending always succeed
    }

}
