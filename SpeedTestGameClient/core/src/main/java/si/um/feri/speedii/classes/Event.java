package si.um.feri.speedii.classes;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;

public class Event {

    private String name;
    private String type;
    private LocalDateTime time;
    private boolean online;
    private Location location;
    private final ObjectId id;

    public Event(String name, String type, LocalDateTime time, boolean online, Location location, ObjectId id) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.online = online;
        this.location = location;
        this.id = id;
    }

    public Event(String name, String type, LocalDateTime time, boolean online, Location location) {
        this(name, type, time, online, location, new ObjectId());
    }

    @Override
    public String toString() {
        return name + ", " + type + ", " + time + ", " + online + ", " + location;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ObjectId getId() {
        return id;
    }
}

