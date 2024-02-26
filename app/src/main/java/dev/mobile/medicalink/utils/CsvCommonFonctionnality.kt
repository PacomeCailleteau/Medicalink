package dev.mobile.medicalink.utils

import android.content.Context
import android.util.Log
import dev.mobile.medicalink.db.local.entity.CisSideInfos

class CsvCommonFonctionnality {
    /**
     * Read CSV file from assets folder
     * @param context Context
     * @param filePath CSV file path
     * @return CSV file content
     */
    fun readCsvFromAssets(context: Context, filePath: String): String {
        return context.assets.open(filePath).bufferedReader().use {
            it.readText()
        }
    }


    /**
     * Parse CSV line and return list of values
     * We need this function because there some values with comma inside quotes and sometimes no quotes
     * @param line CSV line
     */
    fun parseCsvLine(line: String): List<String> {
        val values = mutableListOf<String>()
        var value = ""
        var isInsideQuote = false
        for (char in line) {
            when (char) {
                ',' -> {
                    if (isInsideQuote) {
                        value += char
                    } else {
                        values.add(value)
                        value = ""
                    }
                }

                '"' -> {
                    isInsideQuote = !isInsideQuote
                }

                else -> {
                    value += char
                }
            }
        }
        values.add(value)
        return values
    }

    fun insertCsvContentInBatches(
        context: Context,
        batchSize: Int,
        filePath: String,
        insertFunction: (List<CisSideInfos>) -> Unit
    ) {
        context.assets.open(filePath).bufferedReader().useLines { lines ->
            val batch = mutableListOf<CisSideInfos>()

            lines.drop(1) // Skip header
                .filterNot { it.isBlank() } // Skip empty lines
                .forEach { line ->
                    val values = parseCsvLine(line)
                    if (values.size == 3) {
                        try {
                            batch.add(CisSideInfos(values[0].toInt(), values[1], values[2]))
                            if (batch.size >= batchSize) {
                                insertFunction(batch.toList())
                                batch.clear()
                            }
                        } catch (e: NumberFormatException) {
                            Log.e(
                                "CsvCommonFonctionnality",
                                "Error parsing integer from CSV line: $line"
                            )
                        }
                    } else {
                        Log.e("CsvCommonFonctionnality", "Invalid CSV line format: $line")
                    }
                }

            if (batch.isNotEmpty()) {
                insertFunction(batch.toList())
            }
        }
    }

}