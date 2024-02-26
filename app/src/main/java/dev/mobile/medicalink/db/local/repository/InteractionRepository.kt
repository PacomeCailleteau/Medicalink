package dev.mobile.medicalink.db.local.repository


import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.InteractionDao
import dev.mobile.medicalink.db.local.entity.Interaction
import dev.mobile.medicalink.utils.CsvCommonFonctionnality


class InteractionRepository(private val interactionDao: InteractionDao) {
    private val commonFonctionnality = CsvCommonFonctionnality()

    fun getAllInteractionLikeSubstance(substance: String): List<Interaction> {
        return try {
            interactionDao.getAllLike(substance)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneInteractionBySubstance(substance: String): List<Interaction> {
        return try {
            interactionDao.getBySubstance(substance)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Insert all CIS_bdpm from CSV file in database
     * @param context Context
     */
    fun insertFromCsv(context: Context) {
        val csvContent = commonFonctionnality.readCsvFromAssets(context, "interactions.csv")
        val interactionList = parseCsv(csvContent)

        try {
            interactionDao.insertAll(*interactionList.toTypedArray())
        } catch (e: SQLiteConstraintException) {
            Log.e("InteractionRepository", "InteractionRepository already exists : ${e.message}")
        } catch (e: SQLiteException) {
            Log.e(
                "InteractionRepository",
                "Database Error while inserting Interaction : ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(
                "InteractionRepository",
                "Unknown Error while inserting Interaction : ${e.message}"
            )
        }
    }


    /**
     * Parse CSV file and insert all CIS_bdpm in database, the first line of the CSV file must be the header
     * @param csvContent CSV file content
     * @return Pair<Boolean, String> : Boolean is true if success, String is error message if error
     */
    private fun parseCsv(csvContent: String): List<Interaction> {
        val interactionList = mutableListOf<Interaction>()
        val lines = csvContent.split("\n")
        //On ne prend ni la première ligne (header) ni la dernière ligne (vide)
        for (i in 1 until lines.size - 1) {
            val line = lines[i]
            val values = commonFonctionnality.parseCsvLine(line)
            if (values.size == 2) {
                val interaction = Interaction(
                    substance = values[0],
                    incompatibles = values[1],
                )

                interactionList.add(interaction)

            } else {
                Log.e("InteractionRepository", "Error while parsing CSV line : $line")
            }
        }
        return interactionList
    }

}