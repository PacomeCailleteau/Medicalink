package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.CisBdpmDao
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.utils.CsvCommonFonctionnality

class CisBdpmRepository(private val CISbdpmDao: CisBdpmDao) {
    val commonFonctionnality = CsvCommonFonctionnality()

    fun getAllCisBdpm(): List<CisBdpm> {
        return try {
            CISbdpmDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun search100(searchQuery: String): List<CisBdpm> {
        return try {
            CISbdpmDao.search100(searchQuery)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneCisBdpmById(CodeCIS: Int): List<CisBdpm> {
        return try {
            CISbdpmDao.getById(CodeCIS)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertCisBdpm(cisBdpm: CisBdpm): Pair<Boolean, String> {
        return try {
            CISbdpmDao.insertAll(cisBdpm)
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
        val csvContent = commonFonctionnality.readCsvFromAssets(context, "CIS_bdpm.csv")
        //val csvContent = commonFonctionnality.readCsvFromAssets(context, "CIS_bdpm_new.csv")
        val cisBdpmList = parseCsv(csvContent)
        try {
            CISbdpmDao.insertAll(*cisBdpmList.toTypedArray())
        } catch (e: SQLiteConstraintException) {
            Log.e("CisBdpmRepository", "CIS_bdpm already exists")
        } catch (e: SQLiteException) {
            Log.e("CisBdpmRepository", "Database Error while inserting CIS_bdpm : ${e.message}")
        } catch (e: Exception) {
            Log.e("CisBdpmRepository", "Unknown Error while inserting CIS_bdpm : ${e.message}")
        }
    }


    /**
     * Parse CSV file and insert all CIS_bdpm in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    private fun parseCsv(csvContent: String): List<CisBdpm> {
        val cisBdpmList = mutableListOf<CisBdpm>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (i in 1 until lines.size - 1) {
            val line = lines[i]
            val values = commonFonctionnality.parseCsvLine(line)
            if (values.size == 12) {
                val cisBdpm = CisBdpm(
                    CodeCIS = values[0].toInt(),
                    denomination = values[1],
                    formePharmaceutique = values[2],
                    voiesAdministration = values[3],
                    statutAdministratifAMM = values[4],
                    typeProcedureAMM = values[5],
                    etatCommercialisation = values[6],
                    dateAMM = values[7],
                    statutBdm = values[8],
                    numeroAutorisationEuropeenne = values[9],
                    titulaire = values[10],
                    surveillanceRenforcee = values[11],
                )
                cisBdpmList.add(cisBdpm)
            } else {
                Log.e("CisBdpmRepository", "Error while parsing CSV line : $line")
            }
        }
        return cisBdpmList
    }


    fun deleteCisBdpm(cisBdpm: CisBdpm): Pair<Boolean, String> {
        return try {
            CISbdpmDao.delete(cisBdpm)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisBdpm doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateCisBdpm(cisBdpm: CisBdpm): Pair<Boolean, String> {
        return try {
            CISbdpmDao.update(cisBdpm)
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