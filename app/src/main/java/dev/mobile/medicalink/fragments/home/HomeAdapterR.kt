package dev.mobile.medicalink.fragments.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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

enum class CircleState {
    PRENDRE, SAUTER, NULL, VIDE
}

val CircleListStates = mapOf<CircleState, Int>(
    CircleState.PRENDRE to R.drawable.correct,
    CircleState.SAUTER to R.drawable.avertissement,
    CircleState.NULL to R.drawable.circle
)

interface CircleStateCallback {
    fun changeEtat(etat: CircleState, position: Int)
}

/**
 * Adapter pour la liste des traitements de l'accueil
 */
class HomeAdapterR(
    private var list: MutableList<Pair<Prise, Traitement>>,
    private var dateCourante: LocalDate,
    private val callback: RapportJourCallback,
    parentRecyclerView: RecyclerView,

) : RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>(), CircleStateCallback {
    private val VIEW_TYPE_EMPTY: Int = 0
    private val VIEW_TYPE_NORMAL: Int = 1

    private val db = AppDatabase.getInstance(parentRecyclerView.context)
    private val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())

    val listeEtatPrise: MutableMap<Int, CircleState> = mutableMapOf()
    var heureCourante: String? = null

    init {
        for (i in 0 until list.size) {
            changeEtat(CircleState.NULL, i)
            val queue = LinkedBlockingQueue<CircleState>()
            Thread {
                val isPriseCouranteValidee =
                    priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[i].first.numeroPrise
                    )
                if (isPriseCouranteValidee.isEmpty()) {
                    queue.add(CircleState.NULL)
                } else if (isPriseCouranteValidee.first().statut == "prendre") {
                    queue.add(CircleState.PRENDRE)
                } else {
                    queue.add(CircleState.SAUTER)
                }
            }.start()
            val result = queue.take()
            Log.d("RESULTAT", result.toString())
            changeEtat(result, i)
        }
    }

    override fun changeEtat(etat: CircleState, position: Int) {
        listeEtatPrise[position] = etat
        Log.d("position", position.toString())
        Log.d("états", listeEtatPrise.toString())
        callback.updateRapport(listeEtatPrise)
    }

    /**
     * Mettre à jour les données de l'adaptateur
     * @param listeTraitementUpdated : liste des traitements
     * @param listePriseValideeUpdated : liste des prises validées
     * @param date : date courante
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(
        listeTraitementUpdated: MutableList<Pair<Prise, Traitement>>,
        date: LocalDate
    ) {
        listeEtatPrise.clear()
        list = listeTraitementUpdated

        dateCourante = date

        for (i in 0 until list.size) {
            changeEtat(CircleState.NULL, i)
            val queue = LinkedBlockingQueue<CircleState>()
            Thread {
                val isPriseCouranteValidee =
                    priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[i].first.numeroPrise
                    )
                if (isPriseCouranteValidee.isEmpty()) {
                    queue.add(CircleState.NULL)
                } else if (isPriseCouranteValidee.first().statut == "prendre") {
                    queue.add(CircleState.PRENDRE)
                } else {
                    queue.add(CircleState.SAUTER)
                }
            }.start()
            val result = queue.take()
            Log.d("RESULTAT", result.toString())
            changeEtat(result, i)
        }
        //updateRapportText() // Mettez à jour le texte du rapport
        //updatePriseValideeList(listePriseValideeUpdated) // Mettez à jour la listePriseValidee
        notifyDataSetChanged() // Mettez à jour l'adaptateur
    }

    /**
     * ViewHolder pour la liste des traitements
     * @param view : vue
     * @return : RecyclerView.ViewHolder
     */
    class AjoutManuelViewHolder(val view: View, val callback: CircleStateCallback) : RecyclerView.ViewHolder(view) {
        val nomMedic = view.findViewById<TextView>(R.id.nomMedic)
        val nbComprime = view.findViewById<TextView>(R.id.nbComprime)
        val heurePrise = view.findViewById<TextView>(R.id.heurePriseAccueil)
        val circleTick = view.findViewById<ImageView>(R.id.circleTick)
        val imageMedoc = view.findViewById<ImageView>(R.id.itemListeTraitementsImage)
        val mainHeure = view.findViewById<TextView>(R.id.mainHeureMedic)
        val mainHeureLayout = view.findViewById<ConstraintLayout>(R.id.layoutMainHeure)

        var circleState = CircleState.NULL
        fun changeEtat(etat: CircleState) {
            circleState = etat
            callback.changeEtat(etat, bindingAdapterPosition)
            if (etat == CircleState.VIDE) {
                return
            }
            circleTick.setImageResource(CircleListStates[etat]!!)
        }
    }

    /**
     * Retourne le nombre d'éléments dans la liste
     * @return : Int
     */
    override fun getItemCount(): Int {
        return if (list.isEmpty()) {
            1 // Retourne 1 pour la vue vide
        } else {
            list.size
        }
    }

    /**
     * Retourne le type de vue en fonction de la position
     * @param position : position de l'élément
     * @return : Int
     */
    override fun getItemViewType(position: Int): Int {

        return if (list.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    /**
     * Crée une nouvelle vue
     * @param parent : ViewGroup
     * @param viewType : Int
     * @return : AjoutManuelViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        return when (viewType) {
            // Il n'y a pas de traitement, affiche la vue vide
            VIEW_TYPE_EMPTY -> {
                val layoutEmpty = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_accueil_vide, parent, false)
                AjoutManuelViewHolder(layoutEmpty, this)
            }
            // Il y a des traitements, affiche la vue rapport puis les vues normales
            VIEW_TYPE_NORMAL -> {
                val layoutNormal = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_accueil, parent, false)
                AjoutManuelViewHolder(layoutNormal, this)
            }

            else -> throw IllegalArgumentException("Type de vue inconnu")
        }
    }

    /**
     * Met à jour les données de la vue
     * @param holder : AjoutManuelViewHolder
     * @param position : position de l'élément
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {


        if (list.isEmpty()) {
            holder.changeEtat(CircleState.VIDE)
            return
        }

        val item = list[position]
        if (item == list[0]) {
            list[0].first.heurePrise.split(":").first()
        }
        holder.nomMedic.text = item.second.nomTraitement
        holder.nbComprime.text = "${item.first.quantite} ${item.first.dosageUnite}"
        holder.heurePrise.text = item.first.heurePrise
        holder.mainHeure.text = "${item.first.heurePrise.split(":").first()}h"
        if (item == list[0] || item.first.heurePrise.split(":").first() != heureCourante) {
            holder.mainHeureLayout.visibility = View.VISIBLE
            heureCourante = item.first.heurePrise.split(":").first()
        } else {
            holder.mainHeureLayout.visibility = View.GONE
        }
        // Si la prise est dans le futur, on affiche l'horloge et on désactive le bouton
        if (dateCourante >= LocalDate.now().plusDays(1)) {
            holder.changeEtat(CircleState.NULL)
            holder.circleTick.setImageResource(R.drawable.horloge)
            holder.circleTick.isEnabled = false
            holder.circleTick.isClickable = false
        } else {
            // Sinon, on affiche le cercle et on active le bouton
            holder.circleTick.isEnabled = true
            holder.circleTick.isClickable = true
            val queue = LinkedBlockingQueue<CircleState>()
            Thread {
                val isPriseCouranteValidee =
                    priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        item.first.numeroPrise
                    )
                if (isPriseCouranteValidee.isEmpty()) {
                    queue.add(CircleState.NULL)
                } else if (isPriseCouranteValidee.first().statut == "prendre") {
                    queue.add(CircleState.PRENDRE)
                } else {
                    queue.add(CircleState.SAUTER)
                }
            }.start()
            val result = queue.take()
            Log.d("RESULTAT", result.toString())
            holder.changeEtat(result)
        }

        // Si le bouton est cliqué, on affiche la fenêtre de dialogue
        holder.circleTick.setOnClickListener {
            showConfirmPriseDialog(holder, holder.itemView.context)
        }
    }

    /**
     * Affiche la fenêtre de dialogue pour confirmer la prise
     * @param holder : AjoutManuelViewHolder
     * @param context : contexte
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showConfirmPriseDialog(
        holder: AjoutManuelViewHolder,
        context: Context,
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_prendre_la_prise, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        // Création de la connexion à la base de données
        val db = AppDatabase.getInstance(context)
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())

        // Récupération des éléments de la vue
        val nomMedic = holder.nomMedic
        val nbComprime = holder.nbComprime
        val heurePrise = holder.heurePrise

        // On récupère les éléments de la vue et leurs valeurs
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
        val sauterButton = dialogView.findViewById<Button>(R.id.annulerButton)
        sauterButton.isEnabled = false
        imagePrendre.setColorFilter(ContextCompat.getColor(context, R.color.black))

        // On vérifie si la prise est déjà validée et on affiche le bon bouton
        if (holder.circleState == CircleState.NULL) {
            // Si l'image est un cercle, on affiche le bouton prendre
            prendreButton.text = context.resources.getString(R.string.prendre)
            imagePrendre.setImageResource(R.drawable.verifie)

        } else if (holder.circleState == CircleState.PRENDRE) {
            // Si l'image est un tick correct, on affiche le bouton pris
            prendreButton.text = context.resources.getString(R.string.pris)
            imagePrendre.colorFilter = null
            imagePrendre.setImageResource(R.drawable.valide_vert)
        }
        croixButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        // Si le bouton prendre est cliqué, on affiche la fenêtre de dialogue
        sauterLayout.setOnClickListener {
            // Si l'image est déjà un avertissment, on la remet en cercle, sinon on la met en avertissment
            if (holder.circleState == CircleState.SAUTER) {
                val queue = LinkedBlockingQueue<String>()
                Thread {

                    val priseToDelete = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[holder.adapterPosition].first.numeroPrise
                    )
                    if (priseToDelete.isNotEmpty()) {
                        priseValideeDatabaseInterface.deletePriseValidee(priseToDelete.first())
                    }

                    Log.d("priseValideeTestSautee", priseToDelete.toString())
                    Log.d(
                        "priseValideeTestSautee2",
                        priseValideeDatabaseInterface.getAllPriseValidee().toString()
                    )
                    queue.add("True")

                }.start()
                queue.take()
                holder.changeEtat(CircleState.NULL)
            } else {
                val queue = LinkedBlockingQueue<String>()
                Thread {
                    val priseToUpdate = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[holder.adapterPosition].first.numeroPrise
                    )
                    if (priseToUpdate.isNotEmpty()) {
                        val maPrise = priseToUpdate.first()
                        maPrise.statut = "sauter"
                        priseValideeDatabaseInterface.updatePriseValidee(maPrise)
                    } else {
                        val priseValidee = PriseValidee(
                            uuid = UUID.randomUUID().toString(),
                            date = dateCourante.toString(),
                            uuidPrise = list[holder.adapterPosition].first.numeroPrise,
                            statut = "sauter",
                        )
                        priseValideeDatabaseInterface.insertPriseValidee(priseValidee)
                    }

                    Log.d("priseValideeTest", priseToUpdate.toString())
                    Log.d(
                        "priseValideeTest2",
                        priseValideeDatabaseInterface.getAllPriseValidee().toString()
                    )
                    queue.add("True")

                }.start()
                queue.take()
                holder.changeEtat(CircleState.SAUTER)
            }
            notifyDataSetChanged()
            dosageDialog.dismiss()
        }

        // Listener pour le bouton prendre
        prendreLayout.setOnClickListener {
            // Si l'image est déjà un tick correct alors on la remet en cercle, sinon on la met en tick correct
            if (holder.circleState == CircleState.PRENDRE) {
                val queue = LinkedBlockingQueue<String>()
                Thread {

                    val priseToDelete = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[holder.adapterPosition].first.numeroPrise
                    )
                    if (priseToDelete.isNotEmpty()) {
                        priseValideeDatabaseInterface.deletePriseValidee(priseToDelete.first())
                    }

                    Log.d("priseValideeTest", priseToDelete.toString())
                    Log.d(
                        "priseValideeTest2",
                        priseValideeDatabaseInterface.getAllPriseValidee().toString()
                    )
                    queue.add("True")

                }.start()
                queue.take()
                holder.changeEtat(CircleState.NULL)
            } else {
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
                    Toast.LENGTH_SHORT
                ).show()

                val queue = LinkedBlockingQueue<String>()
                Thread {
                    val priseToUpdate = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        list[holder.adapterPosition].first.numeroPrise
                    )
                    if (priseToUpdate.isNotEmpty()) {
                        val maPrise = priseToUpdate.first()
                        maPrise.statut = "prendre"
                        priseValideeDatabaseInterface.updatePriseValidee(maPrise)
                    } else {
                        val priseValidee = PriseValidee(
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
                    val medocDatabaseInterface = MedocRepository(db.medocDao())
                    var dateFinTraitement: String? = null
                    if (traitement.UUID == null) {
                        Log.d("UUID", "UUID null")
                        return@Thread
                    } else {
                        val medoc = medocDatabaseInterface.getOneMedocById(traitement.UUID!!)
                        if (medoc.size == 1) {
                            //On récupère la date de fin du traitement
                            val medicament = medoc[0]
                            dateFinTraitement = medicament.dateFinTraitement
                            medicament.comprimesRestants =
                                medicament.comprimesRestants?.minus(prise.quantite)

                            if (medicament.comprimesRestants!! <= 0) {
                                medicament.comprimesRestants = 0
                                NotificationService.createStockNotif(
                                    context,
                                    "Stock épuisé",
                                    "La quantité du médicament ${medicament.nom} est épuisée"
                                )
                            }

                            //On met à jour le médicament dans la base de données
                            medocDatabaseInterface.updateMedoc(medicament)
                        } else {
                            Log.d("MEDOC", "Le médicament n'a pas été trouvé")
                            return@Thread
                        }
                    }

                    //Si la date n'est pas null et qu'elle est supérieure à la date actuelle, on ne fait rien
                    if (dateFinTraitement != null && dateFinTraitement > LocalTime.now()
                            .toString()
                    ) {
                        Log.d("FIN TRAITEMENT", "Date fin traitement supérieure à la date actuelle")
                        return@Thread
                    } else {
                        val date = dateCourante.toString()
                        val numero = list[holder.adapterPosition].first.numeroPrise
                        //On créer la notification de la prochaine prise
                        NotificationService.createNextNotif(
                            context,
                            heureProchainePrise,
                            traitement,
                            Pair(date, numero)
                        )
                    }
                }.start()
                holder.changeEtat(CircleState.PRENDRE)
            }
            updateData(list, dateCourante)
            notifyDataSetChanged()
            dosageDialog.dismiss()
        }
        notifyDataSetChanged()
        dosageDialog.show()
    }
}
