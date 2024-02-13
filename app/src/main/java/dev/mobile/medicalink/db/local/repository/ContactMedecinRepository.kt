package dev.mobile.medicalink.db.local.repository

import dev.mobile.medicalink.db.local.dao.ContactMedecinDao
import dev.mobile.medicalink.db.local.entity.ContactMedecin

class ContactMedecinRepository(private val contactMedecinDao: ContactMedecinDao) {

    fun getAllContactMedecins(): List<ContactMedecin> {
        return try {
            contactMedecinDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneContactMedecinById(rpps: String): ContactMedecin? {
        return try {
            contactMedecinDao.getById(rpps)
        } catch (e: Exception) {
            null
        }
    }

    fun getContactMedecinByUserUuid(userUuid: String): List<ContactMedecin> {
        return try {
            contactMedecinDao.getByUserUuid(userUuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertContactMedecin(contactMedecin: ContactMedecin): Pair<Boolean, String> {
        return try {
            contactMedecinDao.insertAll(contactMedecin)
            Pair(true, "Success")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteContactMedecin(contactMedecin: ContactMedecin): Pair<Boolean, String> {
        return try {
            contactMedecinDao.delete(contactMedecin)
            Pair(true, "Success")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateContactMedecin(contactMedecin: ContactMedecin): Pair<Boolean, String> {
        return try {
            contactMedecinDao.update(contactMedecin)
            Pair(true, "Success")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }


}