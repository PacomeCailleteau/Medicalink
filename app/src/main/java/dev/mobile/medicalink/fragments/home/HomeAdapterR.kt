package dev.mobile.medicalink.fragments.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.PriseValideeRepository
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.utils.NotificationService
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue


class HomeAdapterR(private var list: MutableList<Pair<Prise, Traitement>>,
                   private var listePriseValidee : MutableList<Pair<LocalDate,String>>,
                   private var dateCourante : LocalDate,
                   private val parentRecyclerView: RecyclerView) :
    RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>() {


    var heureCourante: String? = null
    fun updateData(listeTraitementUpdated: MutableList<Pair<Prise, Traitement>>,listePriseValideeUpdated : MutableList<Pair<LocalDate,String>>,date: LocalDate) {
        list = listeTraitementUpdated
        listePriseValidee=listePriseValideeUpdated
        dateCourante=date
    }

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val nomMedic = view.findViewById<TextView>(R.id.nomMedic)
        val nbComprime = view.findViewById<TextView>(R.id.nbComprime)
        val heurePrise = view.findViewById<TextView>(R.id.heurePriseAccueil)
        val circleTick = view.findViewById<ImageView>(R.id.circleTick)
        val imageMedoc = view.findViewById<ImageView>(R.id.itemListeTraitementsImage)
        val mainHeure = view.findViewById<TextView>(R.id.mainHeureMedic)
        val mainHeureLayout = view.findViewById<ConstraintLayout>(R.id.layoutMainHeure)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_accueil, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val db = AppDatabase.getInstance(holder.itemView.context)
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())

        val item = list[position]
        if (item == list.first()) {
            list.first().first.heurePrise.split(":").first()
        }
        holder.nomMedic.text = item.second.nomTraitement
        holder.nbComprime.text = "${item.first.quantite} ${item.first.dosageUnite}"
        holder.heurePrise.text = item.first.heurePrise
        holder.mainHeure.text = "${item.first.heurePrise.split(":").first()}h"
        if (item == list.first() || item.first.heurePrise.split(":").first() != heureCourante) {
            holder.mainHeureLayout.visibility = View.VISIBLE
            heureCourante = item.first.heurePrise.split(":").first()
        } else {
            holder.mainHeureLayout.visibility = View.GONE
        }

        if (dateCourante != LocalDate.now()) {
            holder.circleTick.setImageResource(R.drawable.horloge)
            holder.circleTick.isEnabled = false
            holder.circleTick.isClickable = false
        }else {

            holder.circleTick.isEnabled = true
            holder.circleTick.isClickable = true
            val queue = LinkedBlockingQueue<String>()
            Thread {
                var isPriseCouranteValidee =
                    priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        item.first.numeroPrise.toString()
                    )
                if (isPriseCouranteValidee.isEmpty()) {
                    queue.add("null")
                } else if (isPriseCouranteValidee.first().statut == "prendre") {
                    queue.add("prendre")
                } else {
                    queue.add("sauter")
                }
            }.start()
            var result = queue.take()
            Log.d("RESULTAT", result)
            if (result == "null") {
                holder.circleTick.setImageResource(R.drawable.circle)
            } else if (result == "prendre") {
                holder.circleTick.setImageResource(R.drawable.correct)
            } else {
                holder.circleTick.setImageResource(R.drawable.avertissement)
            }
        }



        /*
        A check pour afficher les détails d'un traitement quand cliqué

        holder.naissance.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
            false
        }
         */

        holder.circleTick.setOnClickListener {
            var listePriseValidee : MutableList<Pair<LocalDate,String>>
            showConfirmPriseDialog(holder, holder.itemView.context)
        }


/*
        holder.view.setOnClickListener {
            if (holder.circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.circle
                    )?.constantState
                ) == true
            ) {
                holder.circleTick.setImageResource(R.drawable.correct)
            } else {
                holder.circleTick.setImageResource(R.drawable.circle)
            }

            true
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showConfirmPriseDialog(
        holder: AjoutManuelViewHolder,
        context: Context,
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_prendre_la_prise, null)
        val builder = AlertDialog.Builder(context,R.style.RoundedDialog)
        builder.setView(dialogView)

        val layout = LayoutInflater
            .from(context)
            .inflate(R.layout.item_accueil, parentRecyclerView, false)

        val dosageDialog = builder.create()

        val db = AppDatabase.getInstance(context)
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())

        val nomMedic = holder.nomMedic
        val nbComprime = holder.nbComprime
        val heurePrise = holder.heurePrise
        val circleTick = holder.circleTick
        val imageMedoc = holder.imageMedoc
        val mainHeure = holder.mainHeure
        val mainHeureLayout = holder.mainHeureLayout

        val titreHeurePrise = dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        titreHeurePrise.text = heurePrise.text
        val croixButton = dialogView.findViewById<ImageView>(R.id.croixButton)
        val nomMedicament = dialogView.findViewById<TextView>(R.id.nom_medicament)
        nomMedicament.text = nomMedic.text
        val nombreUnite = dialogView.findViewById<TextView>(R.id.nombre_unité)
        nombreUnite.text = "${nbComprime.text}"
        val sauterLayout = dialogView.findViewById<ConstraintLayout>(R.id.sauterLinear)
        val prendreLayout = dialogView.findViewById<ConstraintLayout>(R.id.prendreLinear)
        val imagePrendre = dialogView.findViewById<ImageView>(R.id.imageView6)
        val prendreButton = dialogView.findViewById<Button>(R.id.prendreButton)
        prendreButton.isEnabled = false
        val sauterButton = dialogView.findViewById<Button>(R.id.sauterButton)
        sauterButton.isEnabled = false

        if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.circle
                )?.constantState
            ) == true
        ){
            prendreButton.text = context.resources.getString(R.string.prendre)
            imagePrendre.setImageResource(R.drawable.verifie)

        } else if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.correct
                )?.constantState
            ) == true
        ) {
            prendreButton.text = context.resources.getString(R.string.pris)
            imagePrendre.setImageResource(R.drawable.valide_vert)
        }

        croixButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        sauterLayout.setOnClickListener {
            // Si l'image est déjà un avertissment, on la remet en cercle, sinon on la met en avertissment
            if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.avertissement
                    )?.constantState
                ) == true
            ) {
                val queue = LinkedBlockingQueue<String>()
                Thread{

                    val priseToDelete=priseValideeDatabaseInterface.getByUUIDTraitementAndDate(dateCourante.toString(),list[holder.adapterPosition].first.numeroPrise)
                    if (priseToDelete.isNotEmpty()){
                        priseValideeDatabaseInterface.deletePriseValidee(priseToDelete.first())
                    }

                    Log.d("priseValideeTestSautee",priseToDelete.toString())
                    Log.d("priseValideeTestSautee2",priseValideeDatabaseInterface.getAllPriseValidee().toString())
                    queue.add("True")

                }.start()
                queue.take()
                circleTick.setImageResource(R.drawable.circle)
            } else {
                val queue = LinkedBlockingQueue<String>()
                Thread{
                    val priseToUpdate=priseValideeDatabaseInterface.getByUUIDTraitementAndDate(dateCourante.toString(),list[holder.adapterPosition].first.numeroPrise)
                    if (priseToUpdate.isNotEmpty()){
                        var maPrise = priseToUpdate.first()
                        maPrise.statut="sauter"
                        priseValideeDatabaseInterface.updatePriseValidee(maPrise)
                    }else{
                        var priseValidee = PriseValidee(
                            uuid = UUID.randomUUID().toString(),
                            date = dateCourante.toString(),
                            uuidPrise = list[holder.adapterPosition].first.numeroPrise,
                            statut = "sauter",
                        )
                        priseValideeDatabaseInterface.insertPriseValidee(priseValidee)
                    }

                    Log.d("priseValideeTest",priseToUpdate.toString())
                    Log.d("priseValideeTest2",priseValideeDatabaseInterface.getAllPriseValidee().toString())
                    queue.add("True")

                }.start()
                queue.take()
                circleTick.setImageResource(R.drawable.avertissement)

            }

            dosageDialog.dismiss()
        }

        prendreLayout.setOnClickListener {
            // Si l'image est déjà un tick correct alors on la remet en cercle, sinon on la met en tick correct
            if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.correct
                    )?.constantState
                ) == true
            ) {

                val queue = LinkedBlockingQueue<String>()
                Thread{

                    val priseToDelete=priseValideeDatabaseInterface.getByUUIDTraitementAndDate(dateCourante.toString(),list[holder.adapterPosition].first.numeroPrise)
                    if (priseToDelete.isNotEmpty()){
                        priseValideeDatabaseInterface.deletePriseValidee(priseToDelete.first())
                    }

                    Log.d("priseValideeTest",priseToDelete.toString())
                    Log.d("priseValideeTest2",priseValideeDatabaseInterface.getAllPriseValidee().toString())
                    queue.add("True")

                }.start()
                queue.take()
                circleTick.setImageResource(R.drawable.circle)
            } else {
                //On fait un toast pour dire que le médicament a été pris (on peut l'enlever si on trouve que ça fait moche)

                //On veut créer une notification pour la prochaine prise du traitement, cette prise peut être plus tard dans la journée ou un jour prochain
                //On récupère le traitement et la prise
                val traitement = list[holder.adapterPosition].second
                val prise = list[holder.adapterPosition].first

                /* ##############################################################
                #  Partie prise en compte dans la bd que la prise a été validée #
                ################################################################# */

                Toast.makeText(
                    context,
                    "Vous avez pris votre médicament ${nomMedic.text} de ${heurePrise.text}",
                    Toast.LENGTH_SHORT).show()

                val queue = LinkedBlockingQueue<String>()
                Thread{
                    val priseToUpdate=priseValideeDatabaseInterface.getByUUIDTraitementAndDate(dateCourante.toString(),list[holder.adapterPosition].first.numeroPrise)
                    if (priseToUpdate.isNotEmpty()){
                        var maPrise = priseToUpdate.first()
                        maPrise.statut="prendre"
                        priseValideeDatabaseInterface.updatePriseValidee(maPrise)
                    }else{
                        var priseValidee = PriseValidee(
                            uuid = UUID.randomUUID().toString(),
                            date = dateCourante.toString(),
                            uuidPrise = list[holder.adapterPosition].first.numeroPrise,
                            statut = "prendre",
                        )
                        priseValideeDatabaseInterface.insertPriseValidee(priseValidee)
                    }
                    queue.add("True")
                }.start()

                queue.take()

                //On récupère l'heure de la prochaine prise en fonction des prises du traitement
                val heureProchainePrise = traitement.getProchainePrise(prise).heurePrise

                //On fait une requête à la base de données pour récupéré le Medoc correspondant au traitement
                Thread {
                    val db = AppDatabase.getInstance(context)
                    val medocDatabaseInterface = MedocRepository(db.medocDao())
                    var dateFinTraitement : String? = null
                    if (traitement.UUID == null) {
                        Log.d("UUID", "UUID null")
                        return@Thread
                    }else {
                        val medoc = medocDatabaseInterface.getOneMedocById(traitement.UUID!!)
                        if (medoc.size == 1) {
                            //On récupère la date de fin du traitement
                            val medicament = medoc[0]
                            dateFinTraitement = medicament.dateFinTraitement
                            medicament.comprimesRestants = medicament.comprimesRestants?.minus(prise.quantite)

                            if (medicament.comprimesRestants!! <= 0) {
                                medicament.comprimesRestants=0
                                NotificationService.sendNotification(context, "Fin de traitement", "La quantité est stock est épuisé", 5000)
                            }

                            //On met à jour le médicament dans la base de données
                            medocDatabaseInterface.updateMedoc(medicament)

                        }else {
                            Log.d("MEDOC", "Le médicament n'a pas été trouvé")
                            return@Thread
                        }
                    }

                    //Si la date n'est pas null et qu'elle est supérieure à la date actuelle, on ne fait rien
                    if (dateFinTraitement != null && dateFinTraitement > LocalTime.now().toString()) {
                        Log.d("FIN TRAITEMENT", "Date fin traitement supérieure à la date actuelle")
                        return@Thread
                    }else {
                        //On créer la notification de la prochaine prise
                        NotificationService.createNextNotif(context, heureProchainePrise, traitement)
                    }
                }.start()


                circleTick.setImageResource(R.drawable.correct)
            }
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}
