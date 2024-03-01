package dev.mobile.medicalink.fragments.traitements.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence.Companion.getStringFromEnum
import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic.Companion.getStringFromEnum
import java.time.LocalDate


class ListeTraitementAdapterR(
    private val list: MutableList<Traitement>,
    private val onItemClick: (Traitement, Boolean?) -> Unit,
) :
    RecyclerView.Adapter<ListeTraitementAdapterR.TraitementViewHolder>() {

    class TraitementViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nomTraitement: TextView = view.findViewById(R.id.numeroPrise)
        val dosage: TextView = view.findViewById(R.id.dosage)
        val dateExpirationTraitement: TextView = view.findViewById(R.id.provoquePar)
        val nbComprimesRestants: TextView = view.findViewById(R.id.nbComprimesRestants)
        val constraintLayout: ConstraintLayout = view.findViewById(R.id.layout_item_recap)
        val imageView: ImageView = view.findViewById(R.id.itemListeTraitementsImage)
        val modifierTraitement: ImageView = view.findViewById(R.id.modifierTraitement)
        val supprTraitement: ImageView = view.findViewById(R.id.supprTraitement)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitementViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_liste_traitements, parent, false)
        return TraitementViewHolder(layout)
    }


    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val textAucunTraitement = holder.view.findViewById<TextView>(R.id.textAucunTraitement)
        if (list.isEmpty()) {
            if (textAucunTraitement != null) {
                textAucunTraitement.visibility = View.VISIBLE
            }
            return
        }

        if (textAucunTraitement != null) {
            textAucunTraitement.visibility = View.GONE
        }
        val item = list[position]
        holder.nomTraitement.text = item.nomTraitement
        holder.dosage.text = getAfficheDosage(item, holder)

        //Si le traitement est expiré, un format spécial lui est appliqué
        setCorrectInfos(holder, item)

        holder.constraintLayout.setOnClickListener {
            onItemClick.invoke(item, null)
        }

        holder.modifierTraitement.setOnClickListener {
            onItemClick.invoke(item, false)
        }
        holder.supprTraitement.setOnClickListener {
            showConfirmSuppressDialog(holder.itemView.context, item)
        }
    }

    @SuppressLint("SetTextI18n")
    /**
     * Fonction pour obtenir l'affichage correct des informations d'un traitement
     * @param holder le viewholder
     * @param item le traitement
     */
    private fun setCorrectInfos(
        holder: TraitementViewHolder,
        item: Traitement
    ) {
        if (item.expire) {
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_gray_button_background)
            holder.imageView.setImageResource(R.drawable.medicexpire)
            holder.nbComprimesRestants.text =
                holder.view.resources.getString(R.string.traitement_expire)
            if (item.dateFinTraitement == null) {
                holder.dateExpirationTraitement.text =
                    holder.view.resources.getString(R.string.termine)
            } else {
                holder.dateExpirationTraitement.text =
                    "${holder.view.resources.getString(R.string.termine_le)} ${item.dateFinTraitement!!.dayOfMonth}/${item.dateFinTraitement!!.monthValue}/${item.dateFinTraitement!!.year}"
            }
        } else if (LocalDate.now() < item.dateDbtTraitement) {
            //Sinon on vérifie si le traitement n'a pas encore débuté, si oui le traitement prend un autre format spécial
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_yellow_button_background)
            holder.imageView.setImageResource(R.drawable.medicenattente)
            holder.nbComprimesRestants.text =
                "${item.comprimesRestants} ${
                    getStringFromEnum(
                        item.typeComprime,
                        holder.view.context
                    )
                }${
                    holder.view.resources.getString(
                        R.string.s_restants
                    )
                }"
            holder.dateExpirationTraitement.text =
                "${holder.view.resources.getString(R.string.debute_le)} ${item.dateDbtTraitement.dayOfMonth}/${item.dateDbtTraitement.monthValue}/${item.dateDbtTraitement.year}"

        } else {
            //Sinon, le traitement prend le format "normal", le plus courant
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_blue_button_background)
            holder.imageView.setImageResource(R.drawable.medicencours)
            holder.nbComprimesRestants.text =
                "${item.comprimesRestants} ${
                    getStringFromEnum(
                        item.typeComprime,
                        holder.view.context
                    )
                }${
                    holder.view.resources.getString(
                        R.string.s_restants
                    )
                }"
            if (item.dateFinTraitement == null) {
                holder.dateExpirationTraitement.text =
                    holder.view.resources.getString(R.string.sans_fin)
            } else {
                holder.dateExpirationTraitement.text =
                    "${holder.view.resources.getString(R.string.jusquau)} ${item.dateFinTraitement!!.dayOfMonth}/${item.dateFinTraitement!!.monthValue}/${item.dateFinTraitement!!.year}"
            }
        }
    }

    /**
     * Fonction pour obtenir l'affichage du dosage d'un traitement
     * @param item le traitement
     * @param holder le viewholder
     * @return le dosage du traitement
     */
    private fun getAfficheDosage(item: Traitement, holder: TraitementViewHolder): String {
        return when (item.frequencePrise) {
            EnumFrequence.AUBESOIN -> {
                holder.view.resources.getString(R.string.au_besoin)
            }

            EnumFrequence.QUOTIDIEN -> {
                "${item.totalQuantite} ${holder.view.resources.getString(R.string.par_jour)}"
            }

            else -> {
                "${item.totalQuantite} ${holder.view.resources.getString(R.string.tous_les_min)} ${item.dosageNb} ${
                    getStringFromEnum(
                        item.frequencePrise,
                        holder.view.context
                    )
                }"
            }
        }
    }

    /**
     * Fonction pour la fenetre de confirmation lors de la suppression d'un traitement
     * @param context le contexte de l'application
     * @param item le traitement à supprimer
     */
    private fun showConfirmSuppressDialog(
        context: Context,
        item: Traitement
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_suppression, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        val nonButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val ouiButton = dialogView.findViewById<Button>(R.id.prendreButton)

        nonButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        ouiButton.setOnClickListener {
            notifyItemRemoved(list.indexOf(item))
            list.remove(item)
            onItemClick.invoke(item, true)
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}
