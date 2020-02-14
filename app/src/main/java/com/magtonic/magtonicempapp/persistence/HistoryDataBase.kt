package com.magtonic.magtonicempapp.persistence


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1, exportSchema = false)

abstract class HistoryDataBase: RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "history.db"
    }

    abstract fun historyDao(): HistoryDao
}