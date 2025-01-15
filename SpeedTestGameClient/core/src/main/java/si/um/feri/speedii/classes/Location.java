package si.um.feri.speedii.classes;

import java.util.List;

public class Location {

    public String type;
    public List<Double> coordinates;

    public Location(String type, List<Double> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public Location(List<Double> coordinates) {
        this("Point", coordinates);
    }

    @Override
    public String toString() {
        return "Location{" +
            "type='" + type + '\'' +
            ", coordinates=" + coordinates +
            '}';
    }
}
