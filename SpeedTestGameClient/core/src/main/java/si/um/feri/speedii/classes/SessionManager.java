package si.um.feri.speedii.classes;

public class SessionManager {
    private boolean isSet = false;
    private String token = null;
    private User user = null;

    public boolean start(Pair<String, User> pair) {
        if (pair != null) {
            this.isSet = true;
            this.token = pair.getFirst();
            this.user = pair.getSecond();
            return true;
        }
        return false;
    }

    public void destroy() {
        this.isSet = false;
        this.token = null;
        this.user = null;
    }

    public boolean isSet() {
        return isSet;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "SessionManager{" +
            "isSet=" + isSet +
            ", token='" + token + '\'' +
            ", user=" + user +
            '}';
    }
}
