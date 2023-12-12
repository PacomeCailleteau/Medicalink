package dev.mobile.medicalink.fragments.traitements

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import java.text.SimpleDateFormat
import java.util.*


class AjoutManuelAdapterR(private val list: MutableList<Prise>) :
    RecyclerView.Adapter<AjoutManuelAdapterR.AjoutManuelViewHolder>() {

    val listePriseLiveData: MutableLiveData<List<Prise>> = MutableLiveData()

    class AjoutManuelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val textNumeroPrise = view.findViewById<TextView>(R.id.numeroPrise)
        val heurePriseInput = view.findViewById<TextInputEditText>(R.id.heurePriseInput)
        val quantiteInput = view.findViewById<TextInputEditText>(R.id.quantiteInput)

        val supprimer = view.findViewById<LinearLayout>(R.id.supprimerlayout)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItems(): MutableList<Prise> {
        return list.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AjoutManuelViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_add_prise, parent, false)
        return AjoutManuelViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]

        holder.textNumeroPrise.text = holder.view.resources.getString(R.string.prise)

        holder.heurePriseInput.setText("${item.heurePrise}")

        holder.quantiteInput.setText("${item.quantite} ${item.dosageUnite}(s)")

        holder.heurePriseInput.setOnClickListener {
            // Utilisez la vue parente de l'élément du RecyclerView pour obtenir le contexte
            val context = holder.itemView.context
            showTimePickerDialog(context, holder.heurePriseInput, item)
        }

        holder.quantiteInput.setOnClickListener {
            showDosageDialog(holder, holder.itemView.context, item)
        }

        holder.supprimer.setOnClickListener {
            list.remove(item)
            notifyDataSetChanged()
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

    fun mettreAJourCouleurTexte(heurePriseInput: TextInputEditText, conditionValidee: Boolean) {
        if (conditionValidee) {
            // Mettez la couleur du texte en rouge
            heurePriseInput.setTextColor(Color.BLACK)
        } else {
            // Remettez la couleur du texte à sa valeur normale
            heurePriseInput.setTextColor(Color.RED)  // Remplacez par la couleur que vous souhaitez utiliser normalement
        }
    }

    private fun showTimePickerDialog(
        context: Context,
        heurePriseInput: TextInputEditText,
        item: Prise
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = formatTime(selectedHour, selectedMinute)
                heurePriseInput.setText(formattedTime)

                item.heurePrise = formattedTime
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat =
            SimpleDateFormat("HH:mm", Locale.FRENCH) // Modifiez le format selon vos besoins
        return timeFormat.format(calendar.time)
    }

    private fun showDosageDialog(holder: AjoutManuelViewHolder, context: Context, prise: Prise) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_dosage, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        val titreDosage = dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        val quantiteInput = dialogView.findViewById<EditText>(R.id.quantiteDialogInput)
        val annulerButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)

        titreDosage.text = holder.view.resources.getString(R.string.dosage)
        // Utilisez cette ligne pour appliquer le filtre à l'EditText
        quantiteInput.filters = arrayOf(RangeInputFilter(1, 99))
        quantiteInput.setText(prise.quantite.toString())

        annulerButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        okButton.setOnClickListener {
            // Mettez à jour la quantité dans l'objet Prise avec la nouvelle valeur
            val nouvelleQuantite = quantiteInput.text.toString().toIntOrNull() ?: 1

            prise.quantite = nouvelleQuantite

            // Mettez à jour l'interface utilisateur (par exemple, TextInputEditText)
            holder.quantiteInput.setText("$nouvelleQuantite ${prise.dosageUnite}(s)")

            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

    class RangeInputFilter(private val minValue: Int, private val maxValue: Int) : InputFilter {

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input = (dest?.toString()?.substring(0, dstart)
                        + source?.subSequence(start, end)
                        + dest?.toString()?.substring(dend))?.toInt()

                if (isInRange(minValue, maxValue, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                // Handle the exception if conversion to Int fails
            }

            return ""
        }

        private fun isInRange(min: Int, max: Int, value: Int?): Boolean {
            return value != null && value in min..max
        }
    }
}
