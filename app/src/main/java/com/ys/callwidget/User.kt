package com.ys.callwidget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="myuser")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int = 0,
    @ColumnInfo(name = "phone")
    var phone:String,
    @ColumnInfo(name = "name")
    var name:String,
    @ColumnInfo(name = "picture")
    var picture:String
){


}
