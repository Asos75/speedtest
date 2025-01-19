package si.um.feri.speedii.classes;

import java.time.LocalDateTime;

public class ExtremeEvent {
    public Events type;
    public Location location;
    public LocalDateTime time;
    public User user;

    public ExtremeEvent(Events type, Location location, LocalDateTime time, User user) {
        this.type = type;
        this.location = location;
        this.time = time;
        this.user = user;
    }

    public String toString() {
        return "Event: " + type + " at " + location + " at " + time + " by " + user;
    }
}
