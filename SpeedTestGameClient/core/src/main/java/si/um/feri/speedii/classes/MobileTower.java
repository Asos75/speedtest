package si.um.feri.speedii.classes;

import org.bson.types.ObjectId;

public class MobileTower {

    private Location location;
    private String provider;
    private String type;
    private boolean confirmed;
    private User locator;
    private final ObjectId id;

    public MobileTower(Location location, String provider, String type, boolean confirmed, User locator, ObjectId id) {
        this.location = location;
        this.provider = provider;
        this.type = type;
        this.confirmed = confirmed;
        this.locator = locator;
        this.id = id;
    }

    public MobileTower(Location location, String provider, String type, boolean confirmed, User locator) {
        this(location, provider, type, confirmed, locator, new ObjectId());
    }

    @Override
    public String toString() {
        return location + ", " + provider + ", " + type + ", " + confirmed + ", " + locator + " " + id;
    }

    // Getters and Setters
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User getLocator() {
        return locator;
    }

    public void setLocator(User locator) {
        this.locator = locator;
    }

    public ObjectId getId() {
        return id;
    }
}
