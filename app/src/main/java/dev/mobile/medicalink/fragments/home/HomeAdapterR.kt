package dev.mobile.medicalink.fragments.home

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement


class HomeAdapterR(private var list: MutableList<Pair<Prise, Traitement>>,
                   private val parentRecyclerView: RecyclerView) :
    RecyclerView.Adapter<HomeAdapterR.AjoutManuelViewHolder>() {

    var heureCourante: String? = null
    fun updateData(listeTraitementUpdated: MutableList<Pair<Prise, Traitement>>) {
        list = listeTraitementUpdated
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

    private fun showConfirmPriseDialog(
        holder: AjoutManuelViewHolder,
        context: Context
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_prendre_la_prise, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val layout = LayoutInflater
            .from(context)
            .inflate(R.layout.item_accueil, parentRecyclerView, false)

        val dosageDialog = builder.create()

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
        val sauterLayout = dialogView.findViewById<LinearLayout>(R.id.sauterLinear)
        val plusLayout = dialogView.findViewById<LinearLayout>(R.id.plusLinear)
        val prendreLayout = dialogView.findViewById<LinearLayout>(R.id.prendreLinear)
        val imagePrendre = dialogView.findViewById<ImageView>(R.id.imageView6)
        val prendreButton = dialogView.findViewById<Button>(R.id.prendreButton)
        prendreButton.isEnabled = false
        val plusButton = dialogView.findViewById<Button>(R.id.plusButton)
        plusButton.isEnabled = false
        val sauterButton = dialogView.findViewById<Button>(R.id.sauterButton)
        sauterButton.isEnabled = false


        if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.circle
                )?.constantState
            ) == true
        ){
            prendreButton.text = "Prendre"
            imagePrendre.setImageResource(R.drawable.verifie)

        } else if (circleTick.drawable.constantState?.equals(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.correct
                )?.constantState
            ) == true
        ) {
            prendreButton.text = "Pris"
            imagePrendre.setImageResource(R.drawable.valide_vert)
        }

        croixButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        sauterLayout.setOnClickListener {
            if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.circle
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.avertissement)
            } else if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.avertissement
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.circle)
            } else if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.correct
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.avertissement)
            }
            dosageDialog.dismiss()
        }

        prendreLayout.setOnClickListener {
            if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.circle
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.correct)
            } else if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.correct
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.circle)
            } else if (circleTick.drawable.constantState?.equals(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.avertissement
                    )?.constantState
                ) == true
            ) {
                circleTick.setImageResource(R.drawable.correct)
            }
            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

}
