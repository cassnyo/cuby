package com.cassnyo.cuby.data.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {

    @TypeConverter
    fun toString(value: LocalDateTime?): String? =
        value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter
    fun toLocalDateTime(value: String?) =
        value?.let { LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }

}