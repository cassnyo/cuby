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
)
@TypeConverters(
    LocalDateTimeConverter::class,
)
abstract class CubyDatabase : RoomDatabase() {
    abstract fun solveDao(): SolveDao

    companion object {

        private const val DATABASE_NAME = "cuby.db"

        @Volatile
        private var instance: CubyDatabase? = null

        fun getInstance(context: Context): CubyDatabase =
            instance ?: synchronized(this) {
                instance ?: createDatabase(context).also { instance = it }
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context = context,
                klass = CubyDatabase::class.java,
                name = DATABASE_NAME,
            )
                .build()
    }
}