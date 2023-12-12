package com.fititu.logoquizitu.Model.Entity

import androidx.room.TypeConverter
import java.util.Date

class Conv {
    @TypeConverter
    fun toDate(value: Long): Date =  Date(value)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

//    @TypeConverter
//    fun toDuration(value: Long): Duration = value.toDuration(unit = DurationUnit.SECONDS)
//
//    @TypeConverter
//    fun fromDuration(duration: Duration): Long = duration.toLong(unit = DurationUnit.SECONDS)
}