package dev.mobile.medicalink.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.mobile.medicalink.db.local.dao.CisBdpmDao
import dev.mobile.medicalink.db.local.dao.CisSubstanceDao
import dev.mobile.medicalink.db.local.dao.ContactMedecinDao
import dev.mobile.medicalink.db.local.dao.InteractionsMedicDao
import dev.mobile.medicalink.db.local.dao.MedocDao
import dev.mobile.medicalink.db.local.dao.PriseValideeDao
import dev.mobile.medicalink.db.local.dao.StatutDouleurDao
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisSubstance
import dev.mobile.medicalink.db.local.entity.ContactMedecin
import dev.mobile.medicalink.db.local.entity.InteractionsMedic
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.entity.StatutDouleur
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import dev.mobile.medicalink.db.local.repository.CisSubstanceRepository
import dev.mobile.medicalink.db.local.repository.InteractionsMedicRepository

@Database(
    entities = [User::class, Medoc::class, CisBdpm::class, PriseValidee::class, CisSubstance::class, ContactMedecin::class, StatutDouleur::class, InteractionsMedic::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // On déclare les DAOs
    abstract fun userDao(): UserDao
    abstract fun medocDao(): MedocDao
    abstract fun cisBdpmDao(): CisBdpmDao
    abstract fun priseValideeDao(): PriseValideeDao
    abstract fun cisSubstanceDao(): CisSubstanceDao
    abstract fun contactMedecinDao(): ContactMedecinDao
    abstract fun statutDouleurDao(): StatutDouleurDao
    abstract fun interactionsMedicDao(): InteractionsMedicDao

    companion object {
        private const val DATABASE_NAME = "medicalink.db"
        private var INSTANCE: AppDatabase? = null

        /**
         * Fonction qui permet de récupérer l'instance de la base de données.
         * Elle est dans le companion object pour que l'on puisse l'appeler sans avoir à instancier la classe AppDatabase car on veut un singleton.
         * @param context Le contexte de l'application
         * @return L'instance de la base de données
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Créer la base de données si elle n'existe pas
                //Si on créé la base de données, alors on va la remplir avec les données de la base de données médicamenteuse
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance

                //On créer un thread pour remplir la base de données (oui c'est pas la meilleure manière de faire)
                Thread {
                    // On ajoute les données de la base de données médicamenteuse avant de retourner l'instance
                    CisBdpmRepository(instance.cisBdpmDao()).insertFromCsv(context)
                    CisSubstanceRepository(instance.cisSubstanceDao()).insertFromCsv(context)
                    InteractionsMedicRepository(instance.interactionsMedicDao()).importFromJson(context)
                }.start()
                instance
            }
        }
    }


}
