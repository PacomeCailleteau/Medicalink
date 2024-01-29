package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.CisBdpmDao
import dev.mobile.medicalink.db.local.dao.CisCompoBdpmDao
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisCompoBdpm

class CisCompoBdpmRepository(private val cisCompoBdpmDao: CisCompoBdpmDao) {

    fun getAllCisCompoBdpm(): List<CisBdpm> {
        return try {
            cisCompoBdpmDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneCisCompoBdpmById(CodeCIS: Int): List<CisBdpm> {
        return try {
            cisCompoBdpmDao.getById(CodeCIS)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertCisCompoBdpm(cisCompoBdpm: CisCompoBdpm): Pair<Boolean, String> {
        return try {
            cisCompoBdpmDao.insertAll(cisCompoBdpm)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisBdpm already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    /**
     * Insert all CIS_bdpm from CSV file in database
     * @param context Context
     */
    fun insertFromCsv(context: Context) {
        val csvContent = readCsvFromAssets(context, "CIS_COMPO_bdpm.csv")
        val cisCompoBdpmList = parseCsv(csvContent)
        try {
            cisCompoBdpmDao.insertAll(*cisCompoBdpmList.toTypedArray())
        } catch (e: SQLiteConstraintException) {
            Log.e("CisCompoBdpmRepository", "CIS_COMPO_bdpm already exists")
        } catch (e: SQLiteException) {
            Log.e("CisCompoBdpmRepository", "Database Error while inserting CIS_COMPO_bdpm : ${e.message}")
        } catch (e: Exception) {
            Log.e("CisCompoBdpmRepository", "Unknown Error while inserting CIS_COMPO_bdpm : ${e.message}")
        }
    }

    /**
     * Read CSV file from assets folder
     * @param context Context
     * @param filePath CSV file path
     * @return CSV file content
     */
    private fun readCsvFromAssets(context: Context, filePath: String): String {
        return context.assets.open(filePath).bufferedReader().use {
            it.readText()
        }
    }


    /**
     * Parse CSV file and insert all CIS_bdpm in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    private fun parseCsv(csvContent: String): List<CisCompoBdpm> {
        val cisCompoBdpmList = mutableListOf<CisCompoBdpm>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (i in 1 until lines.size - 1) {
            val line = lines[i]
            val values = parseCsvLine(line)
            if (values.size == 12) {

                val cisCompoBdpm = CisCompoBdpm(
                    CodeCIS = values[0].toInt(),
                    designationForme = values[1],
                    codeSubstance = values[2],
                    denomination = values[3],
                    dosage = values[4],
                    referenceDosage = values[5],
                    natureComposant = values[6],
                    numeroLiaisonSAFT = values[7]

                )
                cisCompoBdpmList.add(cisCompoBdpm)
            } else {
                Log.e("CisBdpmRepository", "Error while parsing CSV line : $line")
            }
        }
        return cisCompoBdpmList
    }

    /**
     * Parse CSV line and return list of values
     * We need this function because there some values with comma inside quotes and sometimes no quotes
     * @param line CSV line
     */
    private fun parseCsvLine(line: String): List<String> {
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

    fun deleteCisBdpm(cisCompoBdpm: CisCompoBdpm): Pair<Boolean, String> {
        return try {
            cisCompoBdpmDao.delete(cisCompoBdpm)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisBdpm doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateCisBdpm(cisCompoBdpm: CisCompoBdpm): Pair<Boolean, String> {
        return try {
            cisCompoBdpmDao.update(cisCompoBdpm)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisBdpm doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}