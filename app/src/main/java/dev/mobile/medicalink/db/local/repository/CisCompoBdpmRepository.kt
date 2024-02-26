package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.CisCompoBdpmDao
import dev.mobile.medicalink.db.local.entity.CisCompoBdpm
import dev.mobile.medicalink.utils.CsvCommonFonctionnality

class CisCompoBdpmRepository(private val cisCompoBdpmDao: CisCompoBdpmDao) {
    val commonFonctionnality = CsvCommonFonctionnality()

    fun getAllCisCompoBdpm(): List<CisCompoBdpm> {
        return try {
            cisCompoBdpmDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneCisCompoBdpmById(CodeCIS: Int): List<CisCompoBdpm> {
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
            Pair(false, "CisCompoBdpm already exists")
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
        val csvContent = commonFonctionnality.readCsvFromAssets(context, "CIS_COMPO_bdpm.csv")
        val cisCompoBdpmList = parseCsv(csvContent)
        try {
            cisCompoBdpmDao.insertAll(*cisCompoBdpmList.toTypedArray())
        } catch (e: SQLiteConstraintException) {
            Log.e("CisCompoBdpmRepository", "CIS_COMPO_bdpm already exists")
        } catch (e: SQLiteException) {
            Log.e(
                "CisCompoBdpmRepository",
                "Database Error while inserting CIS_COMPO_bdpm : ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(
                "CisCompoBdpmRepository",
                "Unknown Error while inserting CIS_COMPO_bdpm : ${e.message}"
            )
        }
    }

    /**
     * Parse CSV file and insert all CIS_bdpm in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    fun parseCsv(csvContent: String): List<CisCompoBdpm> {
        val cisCompoBdpmList = mutableListOf<CisCompoBdpm>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (i in 1 until lines.size - 1) {
            val line = lines[i]
            val values = commonFonctionnality.parseCsvLine(line)
            if (values.size == 8) {

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


    fun deleteCisBdpm(cisCompoBdpm: CisCompoBdpm): Pair<Boolean, String> {
        return try {
            cisCompoBdpmDao.delete(cisCompoBdpm)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisCompoBdpm doesn't exist")
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
            Pair(false, "CisCompoBdpm doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}