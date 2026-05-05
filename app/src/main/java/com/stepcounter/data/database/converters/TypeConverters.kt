package com.stepcounter.data.database.converters

import androidx.room.TypeConverter
import com.stepcounter.domain.model.Gender

class TypeConverters {
    @TypeConverter
    fun fromGender(value: Gender): String = value.name

    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)
}
