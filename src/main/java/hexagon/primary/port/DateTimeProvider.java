package hexagon.primary.port;

import java.time.LocalDateTime;

@FunctionalInterface
public interface DateTimeProvider {

    LocalDateTime now();

    static DateTimeProvider create() {
        return LocalDateTime::now;
    }
}
