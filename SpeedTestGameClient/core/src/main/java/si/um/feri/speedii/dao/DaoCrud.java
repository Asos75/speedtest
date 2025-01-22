package si.um.feri.speedii.dao;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public interface DaoCrud<T> {

    T getById(ObjectId id) throws IOException;

    List<T> getAll() throws IOException;

    boolean insert(T obj) throws IOException;

    boolean update(T obj) throws IOException;

    boolean delete(T obj) throws IOException;

}
