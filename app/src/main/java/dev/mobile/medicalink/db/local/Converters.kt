package dev.mobile.medicalink.db.local

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    /**
     * Convert a string to a LocalDate
     * @param value the string to convert
     * @return the LocalDate
     */
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    /**
     * Convert a LocalDate to a string
     * @param date the LocalDate to convert
     * @return the string
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}