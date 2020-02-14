package com.magtonic.magtonicempapp.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = History.TABLE_NAME)
class History(code: String, desc: String, date: String, time: String, latitude: Double, longitude: Double) {
    companion object {
        const val TABLE_NAME = "punchcard"
    }

    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0

    @ColumnInfo(name = "code")
    private var code: String? = ""

    @ColumnInfo(name = "desc") // column name will be "list_title" instead of "title" in table
    private var desc: String? = ""

    @ColumnInfo(name = "date")
    private var date: String? = ""

    @ColumnInfo(name = "time")
    private var time: String? = ""

    @ColumnInfo(name = "latitude")
    private var latitude: Double? = 0.0

    @ColumnInfo(name = "longitude")
    private var longitude: Double? = 0.0

    init {
        this.code = code
        this.desc = desc
        this.date = date
        this.time = time
        this.latitude = latitude
        this.longitude = longitude
    }

    fun getId(): Int {
        return id
    }

    fun setId(id : Int) {
        this.id = id
    }

    fun getCode(): String? {
        return code
    }

    /*fun setCode(code: String) {
        this.code = code
    }*/

    fun getDesc(): String? {
        return desc
    }

    /*fun setDesc(desc: String) {
        this.desc = desc
    }*/

    fun getDate(): String? {
        return date
    }

    /*fun setDate(date: String) {
        this.date = date
    }*/

    fun getTime(): String? {
        return time
    }

    /*fun setTime(time: String) {
        this.time = time
    }*/

    fun getLatitude(): Double? {
        return latitude
    }

    /*fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }*/

    fun getLongitude(): Double? {
        return longitude
    }

    /*fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }*/
}