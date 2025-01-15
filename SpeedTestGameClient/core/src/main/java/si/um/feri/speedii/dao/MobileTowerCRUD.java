package si.um.feri.speedii.dao;

import java.util.List;

import si.um.feri.speedii.classes.MobileTower;

public interface MobileTowerCRUD extends DaoCrud<MobileTower> {

    List<MobileTower> getByConfirmed(boolean status);

    boolean toggleConfirm(MobileTower obj);

    boolean insertMany(List<MobileTower> list);
}
