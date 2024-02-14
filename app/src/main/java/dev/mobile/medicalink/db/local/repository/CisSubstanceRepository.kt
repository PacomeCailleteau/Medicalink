package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.CisSubstanceDao
import dev.mobile.medicalink.db.local.entity.CisSubstance

class CisSubstanceRepository (private val CisSubstanceDao: CisSubstanceDao) {

    fun getAllCisSubstances(): List<CisSubstance> {
        return try {
            CisSubstanceDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneCisSubstanceById(CodeCIS: String): List<CisSubstance> {
        return try {
            CisSubstanceDao.getById(CodeCIS)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertCisSubstance(cisSubstance: CisSubstance): Pair<Boolean, String> {
        return try {
            CisSubstanceDao.insertAll(cisSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSubstance already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteCisSubstance(cisSubstance: CisSubstance): Pair<Boolean, String> {
        return try {
            CisSubstanceDao.delete(cisSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSubstance doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateCisSubstance(cisSubstance: CisSubstance): Pair<Boolean, String> {
        return try {
            CisSubstanceDao.update(cisSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "CisSubstance doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun insertFromCsv(context: Context) {
        val csv = Csv()
        val csvContent = csv.readCsvFromAssets(context, "CIS_COMPO_bdpm.csv")
        val cisSubstanceList = parseCsv(csvContent)
        try {
            CisSubstanceDao.insertAll(*cisSubstanceList.toTypedArray())
        } catch (e: SQLiteConstraintException) {
            Log.e("CisSubstanceRepository", "CIS_substance already exists")
        } catch (e: SQLiteException) {
            Log.e("CisSubstanceRepository", "Database Error while inserting CIS_substance : ${e.message}")
        } catch (e: Exception) {
            Log.e("CisSubstanceRepository", "Unknown Error while inserting CIS_substance : ${e.message}")
        }
    }

    /**
     * Parse CSV file and insert all CIS_COMPO_bdpm in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    private fun parseCsv(csvContent: String): List<CisSubstance> {
        val csv = Csv()
        val cisSubstanceList = mutableListOf<CisSubstance>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (index in 1 until lines.size - 1) {
            val line = lines[index]
            val csvValues = csv.parseCsvLine(line)
            val cisSubstance = CisSubstance(
                codeCIS = csvValues[0],
                elementPharmaceutique = csvValues[1],
                codeSubstance = csvValues[2].toInt(),
                denominationSubstance = csvValues[3],
                dosageSubstance = csvValues[4],
                referenceDosage = csvValues[5],
                natureComposant = csvValues[6],
                numeroLiaison = csvValues[7].toInt(),
            )
            cisSubstanceList.add(cisSubstance)
        }
        return cisSubstanceList
    }

}