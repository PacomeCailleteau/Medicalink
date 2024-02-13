package dev.mobile.medicalink.db.local.repository

import android.content.Context

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

}