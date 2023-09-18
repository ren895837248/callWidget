package com.ys.callwidget

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class CallWidgetDatabase :RoomDatabase(){


    abstract fun userDao():UserDao
    companion object {

        @Volatile
        private var INSTANCE: CallWidgetDatabase? = null
        fun getDatabase(context: Context): CallWidgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CallWidgetDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}