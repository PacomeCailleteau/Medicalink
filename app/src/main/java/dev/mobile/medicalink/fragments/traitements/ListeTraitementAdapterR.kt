package dev.mobile.medicalink.fragments.traitements

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import java.time.LocalDate


class ListeTraitementAdapterR(
    private val list: MutableList<Traitement>,
    private val onItemClick: (Traitement, Boolean) -> Unit,
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TraitementViewHolder, position: Int) {
        val item = list.get(position)
        holder.nomTraitement.text = item.nomTraitement
        if (item.dosageUnite == "auBesoin") {
            holder.dosage.text = holder.view.resources.getString(R.string.au_besoin)
        } else if (item.dosageUnite == "quotidiennement") {
            holder.dosage.text =
                "${item.dosageNb} ${holder.view.resources.getString(R.string.par_jour)}"
        } else {
            holder.dosage.text =
                "${item.totalQuantite} ${holder.view.resources.getString(R.string.tous_les_min)} ${item.dosageNb} ${item.dosageUnite}"
        }

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

            holder.constraintLayout.setBackgroundResource(R.drawable.squared_yellow_button_background)
            holder.imageView.setImageResource(R.drawable.medicenattente)
            holder.nbComprimesRestants.text =
                "${item.comprimesRestants} ${item.typeComprime.lowercase()}${
                    holder.view.resources.getString(
                        R.string.s_restants
                    )
                }"
            holder.dateExpirationTraitement.text =
                "${holder.view.resources.getString(R.string.debute_le)} ${item.dateDbtTraitement!!.dayOfMonth}/${item.dateDbtTraitement!!.monthValue}/${item.dateDbtTraitement!!.year}"

        } else {
            holder.constraintLayout.setBackgroundResource(R.drawable.squared_blue_button_background)
            holder.imageView.setImageResource(R.drawable.medicencours)
            holder.nbComprimesRestants.text =
                "${item.comprimesRestants} ${item.typeComprime.lowercase()}${
                    holder.view.resources.getString(
                        R.string.s_restants
                    )
                }"
            if (item.dateFinTraitement == null) {
                holder.dateExpirationTraitement.text =
                    holder.view.resources.getString(R.string.indetermine)
            } else {
                holder.dateExpirationTraitement.text =
                    "${holder.view.resources.getString(R.string.jusquau)} ${item.dateFinTraitement!!.dayOfMonth}/${item.dateFinTraitement!!.monthValue}/${item.dateFinTraitement!!.year}"
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
        holder.modifierTraitement.setOnClickListener {
            onItemClick.invoke(item, false)
        }
        /*
                holder.view.setOnClickListener {
                    onItemClick.invoke(item, false)
                }
        */
        holder.supprTraitement.setOnClickListener {
            showConfirmSuppressDialog(holder.itemView.context, item)
        }
    }

    private fun showConfirmSuppressDialog(
        context: Context,
        item: Traitement
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_confirmation_suppression, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        val titreConfirmationSuppression =
            dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        val nonButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val ouiButton = dialogView.findViewById<Button>(R.id.prendreButton)

        nonButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        ouiButton.setOnClickListener {
            list.remove(item)
            notifyDataSetChanged()
            onItemClick.invoke(item, true)
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}
