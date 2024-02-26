package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.CisSideInfosDao
import dev.mobile.medicalink.db.local.entity.CisSideInfos
import dev.mobile.medicalink.utils.CsvCommonFonctionnality

class CisSideInfosRepository(private val CisSideInfosDao: CisSideInfosDao) {
    val commonFonctionnality = CsvCommonFonctionnality()

    fun getAllCisSideInfos(): List<CisSideInfos> {
        return try {
            CisSideInfosDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneCisSideInfosById(CodeCIS: Int): List<CisSideInfos> {
        return try {
            CisSideInfosDao.getById(CodeCIS)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertCisSideInfos(cisSideInfos: CisSideInfos): Pair<Boolean, String> {
        return try {
            CisSideInfosDao.insertAll(cisSideInfos)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSideInfos already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    /**
     * Insert all CIS_side_effect from CSV file in database
     * @param context Context
     */
    fun insertFromCsv(context: Context) {
        try {
            commonFonctionnality.insertCsvContentInBatches(context, 1000, "CIS_side_infos.csv") {
                CisSideInfosDao.insertAll(*it.toTypedArray())
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("CisSideInfosRepository", "CIS_side_infos already exists : ${e.message}")
        } catch (e: SQLiteException) {
            Log.e(
                "CisSideInfosRepository",
                "Database Error while inserting CIS_side_infos : ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(
                "CisSideInfosRepository",
                "Unknown Error while inserting CIS_side_infos : ${e.message}"
            )
        }
    }


    /**
     * Parse CSV file and insert all CIS_side_effect in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    private fun parseCsv(csvContent: String): List<CisSideInfos> {
        val cisSideInfosList = mutableListOf<CisSideInfos>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (i in 1 until lines.size - 1) {
            val line = lines[i]
            val values = commonFonctionnality.parseCsvLine(line)
            if (values.size == 3) {
                val cisSideInfos = CisSideInfos(
                    CodeCIS = values[0].toInt(),
                    contreIndications = values[1],
                    allergies = values[2]
                )
                cisSideInfosList.add(cisSideInfos)
            } else {
                Log.e("CisSideInfosRepository", "Error while parsing CSV line : $line")
            }
        }
        return cisSideInfosList
    }


    fun deleteCisSideInfos(cisSideInfos: CisSideInfos): Pair<Boolean, String> {
        return try {
            CisSideInfosDao.delete(cisSideInfos)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSideInfos doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateCisSideInfos(cisSideInfos: CisSideInfos): Pair<Boolean, String> {
        return try {
            CisSideInfosDao.update(cisSideInfos)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSideInfos doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}