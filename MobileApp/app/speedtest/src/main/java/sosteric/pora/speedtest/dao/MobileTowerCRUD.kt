package dao

import MobileTower

interface MobileTowerCRUD: DaoCrud<MobileTower>{
    fun getByConfirmed(status: Boolean) : List<MobileTower>
    fun toggleConfirm(obj: MobileTower) : Boolean
    fun insertMany(list: List<MobileTower>): Boolean
}