package dev.mobile.medicalink.db.local

import androidx.room.TypeConverter
import dev.mobile.medicalink.db.local.entity.Interactions
import java.time.LocalDate
import java.time.LocalDateTime

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

    /**
     * Convert a pair of int to a string
     * @param pair the pair to convert
     * @return the string
     */
    @TypeConverter
    fun pairToString(pair: Pair<Int, Int>?): String? {
        return pair?.toString()
    }

    /**
     * Convert a string to a pair of int
     * requiert the format of 'pair.toString' -> (int, int)
     * @param str the string to convert
     * @return the pair
     */
    @TypeConverter
    fun stringToPair(str: String?): Pair<Int, Int>? {
        val result = str?.removeSurrounding("(", ")")?.split(", ") ?: return null
        return Pair(result[0].toInt(), result[1].toInt())
    }

    /**
     * Convert a string to a LocalDate
     * @param value the string to convert
     * @return the LocalDate
     */
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    /**
     * Convert a LocalDate to a string
     * @param date the LocalDate to convert
     * @return the string
     */
    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    /**
     * Convert a list of Interactions to a string
     * @param list the list to convert
     * @return the string
     */
    @TypeConverter
    fun listToString(list: List<Interactions>?): String? {
        return list?.joinToString(separator = ";")
    }

    /**
     * Convert a string to a list of Interactions
     * requiert the format of 'list.joinToString' -> substance%com1%com2;substance%com1%com2;...
     * @param str the string to convert
     * @return the list
     */
    @TypeConverter
    fun stringToList(str: String?): List<Interactions>? {
        return str?.split(";")?.map {
            val result = it.split("%")
            Interactions(result[0], result[1], result[2])
        }
    }
}