package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.google.gson.Gson
import dev.mobile.medicalink.db.local.dao.InteractionsMedicDao
import dev.mobile.medicalink.db.local.entity.InteractionsMedic

class InteractionsMedicRepository(private val interactionsMedicDao: InteractionsMedicDao) {
    fun getAllInteractionsMedic() = try {
        interactionsMedicDao.getAll()
    } catch (e: Exception) {
        emptyList()
    }

    fun getInteractionsMedicBySubstance(substance: String) = try {
        interactionsMedicDao.getAllBySubstance(substance)
    } catch (e: Exception) {
        emptyList()
    }

    fun insertInteractionsMedic(interactionsMedic: InteractionsMedic) = try {
        interactionsMedicDao.insertAll(interactionsMedic)
        Pair(true, "Success")
    } catch (e: SQLiteConstraintException) {
        Pair(false, "InteractionsMedic already exists")
    } catch (e: SQLiteException) {
        Pair(false, "Database Error : ${e.message}")
    } catch (e: Exception) {
        Pair(false, "Unknown Error : ${e.message}")
    }

    fun deleteInteractionsMedic(interactionsMedic: InteractionsMedic) = try {
        interactionsMedicDao.delete(interactionsMedic)
        Pair(true, "Success")
    } catch (e: SQLiteConstraintException) {
        Pair(false, "InteractionsMedic doesn't exist")
    } catch (e: SQLiteException) {
        Pair(false, "Database Error : ${e.message}")
    } catch (e: Exception) {
        Pair(false, "Unknown Error : ${e.message}")
    }

    fun deleteAllInteractionsMedic() = try {
        interactionsMedicDao.deleteAll()
        Pair(true, "Success")
    } catch (e: SQLiteException) {
        Pair(false, "Database Error : ${e.message}")
    } catch (e: Exception) {
        Pair(false, "Unknown Error : ${e.message}")
    }

    fun updateInteractionsMedic(interactionsMedic: InteractionsMedic) = try {
        interactionsMedicDao.update(interactionsMedic)
        Pair(true, "Success")
    } catch (e: SQLiteConstraintException) {
        Pair(false, "InteractionsMedic doesn't exist")
    } catch (e: SQLiteException) {
        Pair(false, "Database Error : ${e.message}")
    } catch (e: Exception) {
        Pair(false, "Unknown Error : ${e.message}")
    }

    fun importFromJson(context: Context) {
        val gson = Gson()
        val json = context.assets.open("interactions.json").bufferedReader().use { it.readText() }
        val interactionsMedic = gson.fromJson(json, Array<InteractionsMedic>::class.java)
        interactionsMedic.forEach {
            insertInteractionsMedic(it)
        }
    }
}