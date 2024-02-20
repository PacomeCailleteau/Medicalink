package dev.mobile.medicalink.fragments.traitements.adapter

import android.annotation.SuppressLint
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
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Prise
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

    @SuppressLint("SetTextI18n")
    
    override fun onBindViewHolder(holder: AjoutManuelViewHolder, position: Int) {
        val item = list[position]
        //Création de l'item prise dans la vue d'ajout du traitement, lorsque l'on ajoute des prises
        holder.textNumeroPrise.text = holder.view.resources.getString(R.string.prise)

        holder.heurePriseInput.setText(item.heurePrise)

        holder.quantiteInput.setText("${item.quantite} ${item.typeComprime}(s)")

        holder.heurePriseInput.setOnClickListener {
            // Utilisez la vue parente de l'élément du RecyclerView pour obtenir le contexte
            val context = holder.itemView.context
            showTimePickerDialog(context, holder.heurePriseInput, item)
        }

        // on fait un texte watcher sur l'heure qui change la couleur du texte
        holder.heurePriseInput.doOnTextChanged() { _, _, _, _ ->
            holder.heurePriseInput.setTextColor(Color.BLACK)
        }

        holder.quantiteInput.setOnClickListener {
            showDosageDialog(holder, holder.itemView.context, item)
        }

        holder.supprimer.setOnClickListener {
            list.remove(item)
            notifyDataSetChanged()
        }

        holder.view.setOnLongClickListener {
            item.enMajuscule()
            notifyDataSetChanged()

            val context = holder.itemView.context
            true
        }
    }

    /**
     * Affiche un TimePickerDialog et met à jour l'heure de la prise
     */
    private fun showTimePickerDialog(
        context: Context,
        heurePriseInput: TextInputEditText,
        item: Prise
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]

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

    /**
     * Formate l'heure sélectionnée dans le TimePickerDialog
     */
    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        val timeFormat =
            SimpleDateFormat("HH:mm", Locale.FRENCH)
        return timeFormat.format(calendar.time)
    }

    /**
     * Affiche un AlertDialog et met à jour la quantité de la prise
     * @param holder ViewHolder de l'élément du RecyclerView
     * @param context Contexte de l'application
     * @param prise Prise à modifier
     */
    @SuppressLint("SetTextI18n")
    private fun showDosageDialog(holder: AjoutManuelViewHolder, context: Context, prise: Prise) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_dosage, null)
        val builder = AlertDialog.Builder(context, R.style.RoundedDialog)
        builder.setView(dialogView)

        val dosageDialog = builder.create()

        val titreDosage = dialogView.findViewById<TextView>(R.id.titreHeurePrise)
        val quantiteInput = dialogView.findViewById<EditText>(R.id.quantiteDialogInput)
        val annulerButton = dialogView.findViewById<Button>(R.id.sauterButton)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)

        titreDosage.text = holder.view.resources.getString(R.string.dosage)
        quantiteInput.filters = arrayOf(RangeInputFilter(1, 99))
        quantiteInput.setText(prise.quantite.toString())

        annulerButton.setOnClickListener {
            dosageDialog.dismiss()
        }

        okButton.setOnClickListener {
            val nouvelleQuantite = quantiteInput.text.toString().toIntOrNull() ?: 1

            prise.quantite = nouvelleQuantite

            holder.quantiteInput.setText("$nouvelleQuantite ${prise.typeComprime}(s)")

            dosageDialog.dismiss()
        }

        dosageDialog.show()
    }

    /**
     * Filtre pour limiter la saisie de l'utilisateur à un intervalle de valeurs
     * @param minValue Valeur minimale
     * @param maxValue Valeur maximale
     */
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
                return ""
            }

            return ""
        }

        private fun isInRange(min: Int, max: Int, value: Int?): Boolean {
            return value != null && value in min..max
        }
    }
}
