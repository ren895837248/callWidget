package com.ys.callwidget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Query("select * from `myuser` order by name asc")
    fun selectAll(): List<User>


    @Query("select * from myuser where id = :id")
    fun selectById(id: Int): User
}