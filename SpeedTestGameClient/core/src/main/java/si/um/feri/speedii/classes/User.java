package si.um.feri.speedii.classes;

import org.bson.types.ObjectId;

public class User {

    private String username;
    private final String password;
    private final String email;
    private final boolean admin;
    private final ObjectId id;

    public User(String username, String password, String email, boolean admin, ObjectId id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.admin = admin;
        this.id = id;
    }

    public User(String username, String password, String email) {
        this(username, password, email, false, new ObjectId());
    }

    @Override
    public String toString() {
        return id + " " + username;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public ObjectId getId() {
        return id;
    }
}

