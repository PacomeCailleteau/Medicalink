package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R


class AjoutManuelAdapterR(private val list: MutableList<Prise>) :
    RecyclerView.Adapter<AjoutManuelAdapterR.AjoutManuelViewHolder>() {

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val textNumeroPrise = view.findViewById<TextView>(R.id.numeroPrise)
        val heurePriseInput = view.findViewById<TextInputEditText>(R.id.heurePriseInput)
        val quantiteInput = view.findViewById<TextInputEditText>(R.id.quantiteInput)

        val supprimer = view.findViewById<LinearLayout>(R.id.supprimerlayout)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_add_prise, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list.get(position)

        holder.textNumeroPrise.text="Prise ${item.numeroPrise}"

        holder.heurePriseInput.setText("${item.heurePrise}")

        holder.quantiteInput.setText("${item.quantite} ${item.dosageUnite}(s)")
        holder.supprimer.setOnClickListener {
            list.remove(item)
            notifyItemRemoved(position)
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

        holder.view.setOnLongClickListener {
            item.enMajuscule()
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

    }
