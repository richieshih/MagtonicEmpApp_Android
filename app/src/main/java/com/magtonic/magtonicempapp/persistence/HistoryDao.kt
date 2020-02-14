package com.magtonic.magtonicempapp.persistence

import androidx.room.*
@Dao
interface HistoryDao {
    @Query("SELECT * FROM " + History.TABLE_NAME)

    fun getAll(): List<History>
    //@Insert
    //void insertAll(List<PlayList> playLists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Delete
    fun delete(history: History)

    //@Update
    //fun update(history: History)


}