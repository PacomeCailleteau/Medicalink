package dev.mobile.medicalink.fragments.home.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import dev.mobile.medicalink.utils.notification.NotificationService
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

/**
 * Adapter pour la liste des traitements de l'accueil
 */
class HomeAdapterR(
    private var list: List<Pair<Prise, Traitement>>,
    private var listePriseValidee: List<Pair<LocalDate, String>>,
    private var dateCourante: LocalDate,
    private val parentRecyclerView: RecyclerView,
    private val viewTypeEmpty: Int = 0,
    private val viewTypeNormal: Int = 1,
    private val viewTypeRapport: Int = 2

) :
    RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>() {

    private var heureCourante: String? = null

    /**
     * Mettre à jour les données de l'adaptateur
     * @param listeTraitementUpdated : liste des traitements
     * @param listePriseValideeUpdated : liste des prises validées
     * @param date : date courante
     */

    fun updateData(
        listeTraitementUpdated: List<Pair<Prise, Traitement>>,
        listePriseValideeUpdated: List<Pair<LocalDate, String>>,
        date: LocalDate
    ) {
        list = listeTraitementUpdated
        listePriseValidee = listePriseValideeUpdated
        dateCourante = date
        updatePriseValideeList(listePriseValideeUpdated) // Mettez à jour la listePriseValidee
        updateRapportText() // Mettez à jour le texte du rapport
    }

    /**
     * ViewHolder pour la liste des traitements
     * @param view : vue
     * @return : RecyclerView.ViewHolder
     */
    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val nomMedic = view.findViewById<TextView>(R.id.nomMedic)
        val nbComprime = view.findViewById<TextView>(R.id.nbComprime)
        val heurePrise = view.findViewById<TextView>(R.id.heurePriseAccueil)
        val circleTick = view.findViewById<ImageView>(R.id.circleTick)
        val mainHeure = view.findViewById<TextView>(R.id.mainHeureMedic)
        val mainHeureLayout = view.findViewById<ConstraintLayout>(R.id.layoutMainHeure)


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
            viewTypeEmpty
        } else if (position == 0) {
            viewTypeRapport
        } else {
            viewTypeNormal
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
            viewTypeEmpty -> {
                val layoutEmpty = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_accueil_vide, parent, false)
                AjoutManuelViewHolder(layoutEmpty)
            }
            // Il y a des traitements, affiche la vue rapport puis les vues normales
            viewTypeRapport -> {
                val layoutRapport = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_accueil_rapport, parent, false)
                AjoutManuelViewHolder(layoutRapport)
            }
            // Il y a des traitements, affiche la vue rapport puis les vues normales
            viewTypeNormal -> {
                val layoutNormal = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_accueil, parent, false)
                AjoutManuelViewHolder(layoutNormal)
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

    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        if (list.isEmpty()) {
            return
        }

        // Si la position est 0, on affiche le rapport
        if (list[position] == list[0]) {
            val rapport = holder.view.findViewById<TextView>(R.id.rapport)
            val listePriseAjd = mutableListOf<Pair<LocalDate, String>>()
            for (element in listePriseValidee) {
                if (element.first == LocalDate.now()) {
                    listePriseAjd.add(element)
                }
            }
            rapport.text = "${listePriseAjd.size}/${list.size - 1}"
            return
        }

        val item = list[position]
        if (item == list[1]) {
            list[1].first.heurePrise.split(":").first()
        }

        holder.nomMedic.text = item.second.nomTraitement
        holder.nbComprime.text = "${item.first.quantite} ${item.first.typeComprime}"
        holder.heurePrise.text = item.first.heurePrise
        holder.mainHeure.text = "${item.first.heurePrise.split(":").first()}h"
        if (item == list[1] || item.first.heurePrise.split(":").first() != heureCourante) {
            holder.mainHeureLayout.visibility = View.VISIBLE
            heureCourante = item.first.heurePrise.split(":").first()
        } else {
            holder.mainHeureLayout.visibility = View.GONE
        }

        updateImageCercle(holder, item)

        // Si le bouton est cliqué, on affiche la fenêtre de dialogue
        holder.circleTick.setOnClickListener {
            showConfirmPriseDialog(holder, holder.itemView.context)
        }
    }

    /**
     * Met à jour l'image du cercle en fonction de la date
     * @param holder : AjoutManuelViewHolder
     * @param item : Pair<Prise, Traitement>
     */
    private fun updateImageCercle(holder: AjoutManuelViewHolder, item: Pair<Prise, Traitement>) {
        val db = AppDatabase.getInstance(holder.itemView.context)
        val priseValideeDatabaseInterface =
            PriseValideeRepository(db.priseValideeDao())// Si la prise est dans le futur, on affiche l'horloge et on désactive le bouton

        if (dateCourante >= LocalDate.now().plusDays(1)) {
            holder.circleTick.setImageResource(R.drawable.horloge)
            holder.circleTick.isEnabled = false
            holder.circleTick.isClickable = false
        } else {
            // Sinon, on affiche le cercle et on active le bouton
            holder.circleTick.isEnabled = true
            holder.circleTick.isClickable = true
            val queue = LinkedBlockingQueue<String>()
            Thread {
                val isPriseCouranteValidee =
                    priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                        dateCourante.toString(),
                        item.first.numeroPrise
                    )
                if (isPriseCouranteValidee.isEmpty()) {
                    queue.add("null")
                } else if (isPriseCouranteValidee.first().statut == "prendre") {
                    queue.add("prendre")
                } else {
                    queue.add("sauter")
                }
            }.start()

            when (queue.take()) {
                "null" -> {
                    holder.circleTick.setImageResource(R.drawable.circle)
                }

                "prendre" -> {
                    holder.circleTick.setImageResource(R.drawable.correct)
                }

                else -> {
                    holder.circleTick.setImageResource(R.drawable.avertissement)
                }
            }
        }
    }

    /**
     * Met à jour la liste des prises validées
     * @param newListePriseValidee : nouvelle liste des prises validées
     */
    private fun updatePriseValideeList(newListePriseValidee: List<Pair<LocalDate, String>>) {
        this.listePriseValidee = newListePriseValidee
        notifyDataSetChanged() // Mettre à jour seulement l'élément à la position 0 (le rapport)
    }


    /**
     * Met à jour le texte du rapport
     */

    private fun updateRapportText() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val rapport =
                parentRecyclerView.findViewHolderForAdapterPosition(0)?.itemView?.findViewById<TextView>(
                    R.id.rapport
                )

            if (list.isNotEmpty()) {
                val listePriseAjd = mutableListOf<Pair<LocalDate, String>>()
                for (element in listePriseValidee) {
                    if (element.first == LocalDate.now()) {
                        listePriseAjd.add(element)
                    }
                }
                if (rapport != null) {
                    rapport.text = "${listePriseAjd.size}/${list.size - 1}"
                }
                // Mettre à jour l'image du cercle en fonction du nombre de prises validées
                val face =
                    parentRecyclerView.findViewHolderForAdapterPosition(0)?.itemView?.findViewById<ImageView>(
                        R.id.circleTick
                    )
                when {
                    listePriseAjd.isEmpty() -> {
                        // Aucune prise validée, afficher le visage triste
                        face?.setImageResource(R.drawable.sad_face)
                    }

                    listePriseAjd.size < list.size - 1 -> {
                        // Plus de la moitié de la liste validée, afficher le visage content
                        face?.setImageResource(R.drawable.good_face)
                    }

                    listePriseAjd.size == list.size - 1 -> {
                        // Toute la liste est validée, afficher le visage avec un grand sourire
                        face?.setImageResource(R.drawable.perfect_face)
                    }

                    else -> {
                        // Aucune des conditions ci-dessus n'est remplie, afficher le cercle par défaut
                        face?.setImageResource(R.drawable.circle)
                    }
                }

                rapport?.text = "${listePriseAjd.size}/${list.size - 1}"
                rapport?.requestLayout()
                rapport?.invalidate()
            }
        }
    }


    /**
     * Affiche la fenêtre de dialogue pour confirmer la prise
     * @param holder : AjoutManuelViewHolder
     * @param context : contexte
     */

    private fun showConfirmPriseDialog(
        holder: AjoutManuelViewHolder,
        context: Context,
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_prendre_la_prise, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        // Récupération des éléments de la vue
        val nomMedic = holder.nomMedic
        val nbComprime = holder.nbComprime
        val heurePrise = holder.heurePrise
        val circleTick = holder.circleTick

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
        val sauterButton = dialogView.findViewById<Button>(R.id.sauterButton)
        sauterButton.isEnabled = false
        imagePrendre.setColorFilter(ContextCompat.getColor(context, R.color.black))

        // On vérifie si la prise est déjà validée et on affiche le bon bouton
        if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.circle
                )?.constantState
            ) == true
        ) {
            // Si l'image est un cercle, on affiche le bouton prendre
            prendreButton.text = context.resources.getString(R.string.prendre)
            imagePrendre.setImageResource(R.drawable.verifie)

        } else if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.correct
                )?.constantState
            ) == true
        ) {
            // Si l'image est un tick correct, on affiche le bouton pris
            prendreButton.text = context.resources.getString(R.string.pris)
            imagePrendre.colorFilter = null
            imagePrendre.setImageResource(R.drawable.valide_vert)
        }
        croixButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        // Listener pour le bouton prendre
        // Si le bouton prendre est cliqué, on affiche la fenêtre de dialogue
        sauterLayout.setOnClickListener {
            onClick("sauter", circleTick, holder, dosageDialog)
        }

        // Listener pour le bouton prendre
        // Si le bouton sauter est cliqué, on affiche la fenêtre de dialogue
        prendreLayout.setOnClickListener {
            onClick("prendre", circleTick, holder, dosageDialog)
        }

        notifyDataSetChanged()
        updateRapportText()
        dosageDialog.show()
    }

    /**
     * Gère le clic sur les boutons prendre et sauter
     * @param typeBouton : type de bouton
     * @param circleTick : image du cercle
     * @param holder : AjoutManuelViewHolder
     * @param dosageDialog : fenêtre de dialogue
     */
    private fun onClick(
        typeBouton: String,
        circleTick: ImageView,
        holder: AjoutManuelViewHolder,
        dosageDialog: AlertDialog
    ) {
        val context = holder.itemView.context
        val db = AppDatabase.getInstance(context)
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())
        val drawable = if (typeBouton == "prendre") {
            R.drawable.correct
        } else {
            R.drawable.avertissement
        }

        if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    drawable
                )?.constantState
            ) == true
        ) {
            val queue = LinkedBlockingQueue<String>()
            Thread {
                val priseToDelete = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                    dateCourante.toString(),
                    list[holder.bindingAdapterPosition].first.numeroPrise
                )
                if (priseToDelete.isNotEmpty()) {
                    priseValideeDatabaseInterface.deletePriseValidee(priseToDelete.first())
                }
                queue.add("True")
            }.start()
            queue.take()
            circleTick.setImageResource(R.drawable.circle)
        } else {
            val queue = LinkedBlockingQueue<String>()
            Thread {
                val priseToUpdate = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                    dateCourante.toString(),
                    list[holder.bindingAdapterPosition].first.numeroPrise
                )
                if (priseToUpdate.isNotEmpty()) {
                    val maPrise = priseToUpdate.first()
                    maPrise.statut = typeBouton
                    priseValideeDatabaseInterface.updatePriseValidee(maPrise)
                } else {
                    val priseValidee = PriseValidee(
                        uuid = UUID.randomUUID().toString(),
                        date = dateCourante.toString(),
                        uuidPrise = list[holder.bindingAdapterPosition].first.numeroPrise,
                        statut = typeBouton,
                    )
                    priseValideeDatabaseInterface.insertPriseValidee(priseValidee)
                }
                queue.add("True")
            }.start()
            queue.take()

            if (typeBouton == "prendre") {
                gererNotif(holder)
                circleTick.setImageResource(R.drawable.correct)
            }
        }
        notifyDataSetChanged()
        updateRapportText()
        dosageDialog.dismiss()
    }

    /**
     * Fonction qui gère l'envoie ou non d'une notification une fois le clic sur prendre effectué
     * @param holder : AjoutManuelViewHolder
     */
    private fun gererNotif(holder: AjoutManuelViewHolder) {
        //On veut créer une notification pour la prochaine prise du traitement, cette prise peut être plus tard dans la journée ou un jour prochain
        //On récupère le traitement et la prise
        val traitement = list[holder.bindingAdapterPosition].second
        val prise = list[holder.bindingAdapterPosition].first

        // On récupère toutes les infos dont on aura besoin
        val context = holder.itemView.context
        val db = AppDatabase.getInstance(context)
        val nomMedic = holder.nomMedic
        val heurePrise = holder.heurePrise

        /* ##############################################################
        #  Partie prise en compte dans la bd que la prise a été validée #
        ################################################################# */
        Toast.makeText(
            context,
            "Vous avez pris votre médicament ${nomMedic.text} de ${heurePrise.text}",
            Toast.LENGTH_SHORT
        ).show()

        //On récupère l'heure de la prochaine prise en fonction des prises du traitement
        val heureProchainePrise = traitement.getProchainePrise(prise).heurePrise

        //On fait une requête à la base de données pour récupéré le Medoc correspondant au traitement
        Thread {
            val medocDatabaseInterface = MedocRepository(db.medocDao())
            val dateFinTraitement: LocalDate?
            if (traitement.uuid == null) {
                return@Thread
            } else {
                val medoc = medocDatabaseInterface.getOneMedocById(traitement.uuid!!)
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
                    return@Thread
                }
            }

            //Si la date n'est pas null et qu'elle est supérieure à la date actuelle, on ne fait rien
            if ((dateFinTraitement != null) && (dateFinTraitement > LocalDate.now())) {
                return@Thread
            } else {
                val date = dateCourante.toString()
                val numero = list[holder.bindingAdapterPosition].first.numeroPrise
                //On créer la notification de la prochaine prise
                NotificationService.createNextNotif(
                    context,
                    heureProchainePrise,
                    traitement,
                    Pair(date, numero)
                )
            }
        }.start()
    }


}
