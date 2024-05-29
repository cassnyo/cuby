package com.cassnyo.cuby.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cassnyo.cuby.data.database.converter.LocalDateTimeConverter
import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.SolveEntity

@Database(
    entities = [SolveEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(
    LocalDateTimeConverter::class,
)
abstract class CubyDatabase : RoomDatabase() {
    abstract fun solveDao(): SolveDao

    companion object {

        private const val DATABASE_NAME = "cuby.db"

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context = context,
                klass = CubyDatabase::class.java,
                name = DATABASE_NAME,
            ).build()
    }
}