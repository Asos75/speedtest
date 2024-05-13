package dao

import MobileTower

interface MobileTowerCRUD: DaoCrud<MobileTower>{
    suspend fun getByConfirmed(status: Boolean) : List<MobileTower>
    suspend fun confirm(obj: MobileTower) : Boolean
}