package si.um.feri.speedii.dao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import si.um.feri.speedii.classes.Measurement;
import si.um.feri.speedii.classes.User;

public interface MeasurementCRUD extends DaoCrud<Measurement> {

    List<Measurement> getByUser(User user) throws IOException;

    List<Measurement> getByTimeFrame(LocalDateTime start, LocalDateTime end) throws IOException;

    boolean insertMany(List<Measurement> list) throws IOException;
}
