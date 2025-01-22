package si.um.feri.speedii.classes;

import org.bson.types.ObjectId;
import si.um.feri.speedii.classes.Type;

import java.time.LocalDateTime;

public class Measurement {

    private long speed;
    private Type type;
    private String provider;
    private Location location;
    private LocalDateTime time;
    private User user;
    private final ObjectId id;

    public Measurement(long speed, Type type, String provider, Location location, LocalDateTime time, User user, ObjectId id) {
        this.speed = speed;
        this.type = type;
        this.provider = provider;
        this.location = location;
        this.time = time;
        this.user = user;
        this.id = id;
    }

    public Measurement(long speed, Type type, String provider, Location location, LocalDateTime time, User user) {
        this(speed, type, provider, location, time, user, new ObjectId());
    }

    @Override
    public String toString() {
        return speed + ", " + type + ", " + provider + ", "
            + location.coordinates.get(0) + ", " + location.coordinates.get(1)
            + ", " + time + ", " + user;
    }

    // Getters and Setters
    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectId getId() {
        return id;
    }
}
