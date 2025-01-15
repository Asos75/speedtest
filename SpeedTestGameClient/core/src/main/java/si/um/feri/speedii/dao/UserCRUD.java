package si.um.feri.speedii.dao;

import si.um.feri.speedii.classes.User;

public interface UserCRUD extends DaoCrud<User> {

    boolean authenticate(String username, String password);
}
