package dev.mobile.medicalink.fragments.douleur

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.textfield.TextInputEditText
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.Converters
import dev.mobile.medicalink.db.local.entity.EnumTypeStatut
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.entity.StatutDouleur
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.StatutDouleurRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.douleur.enums.FiltreDate
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue

class AffichageGraphFragment : Fragment() {

    // from layout
    lateinit var lineChart: LineChart
    lateinit var spinnerGraph: Spinner
    lateinit var spinnerType: Spinner
    lateinit var inputSeuil: TextInputEditText
    lateinit var spinnerMedicament: Spinner
    lateinit var spinnerPrise: Spinner
    lateinit var valider: AppCompatButton
    lateinit var texteNote: TextInputEditText

    // valeur du layout
    private var valeurSpinner1 = FiltreDate.MOIS
    private var valeurSpinnerType: EnumTypeStatut = EnumTypeStatut.Medicament
    lateinit var valeurSpinnerMedic: String
    private var valeurSpinnerPrise: Boolean = true

    // from bd
    lateinit var userCo: String
    private var traitementUti: List<Medoc> = listOf()
    private var statutDouleur: List<StatutDouleur> = listOf()
    private var entries: ArrayList<Entry> = arrayListOf()
    lateinit var lineDataSet: LineDataSet

    /**
     * Initialise le fragment et ajoute les fonctionalités des boutons
     * @param inflater layout XML
     * @param container vue parent
     * @param savedInstanceState donnée d'une vue précédente
     *
     * @return une vue
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_affichage_statut_douleur, container, false)
        val db = AppDatabase.getInstance(requireContext())
        val userInterface = UserRepository(db.userDao())
        val medocInterface = MedocRepository(db.medocDao())
        val statutInterface = StatutDouleurRepository(db.statutDouleurDao())


        val queue = LinkedBlockingQueue<String>()
        Thread {
            this.userCo = userInterface.getUsersConnected()[0].uuid
            this.traitementUti = medocInterface.getAllMedocByUserId(this.userCo)
            this.statutDouleur = statutInterface.getStatutByUser(this.userCo)
            Log.d("zeubie", this.statutDouleur.toString())
            setEntries()
            queue.put("done")
        }.start()
        queue.take()

        val retour = rootView.findViewById<ImageView>(R.id.retour_arriere)
        this.spinnerGraph = rootView.findViewById(R.id.spinner1)
        this.lineChart = rootView.findViewById(R.id.lineChart)
        this.spinnerType = rootView.findViewById(R.id.spinnerType)
        this.inputSeuil = rootView.findViewById(R.id.input_seuil)
        this.spinnerMedicament = rootView.findViewById(R.id.spinnerMedicament)
        this.spinnerPrise = rootView.findViewById(R.id.spinnerPrise)
        this.valider = rootView.findViewById(R.id.valider)
        this.texteNote = rootView.findViewById(R.id.input_note)


        spinnerMedicament.visibility = View.GONE
        spinnerPrise.visibility = View.GONE
        rootView.findViewById<TextView>(R.id.textView5).visibility = View.GONE
        rootView.findViewById<TextView>(R.id.textView6).visibility = View.GONE


        //Ajout de la fonctionnalité de retour à la page précédente
        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        // graphe
        this.lineDataSet = LineDataSet(this.entries, "Data Set")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.RED

        val lineData = LineData(lineDataSet)

        lineChart.data = lineData

        // Customizing X axis to display date and time
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            @SuppressLint("ConstantLocale")
            private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(value.toLong())
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 45f // Rotation de 45 degrés

        val description = Description()
        description.text = "Date vs Value"
        lineChart.description = description

        lineChart.invalidate()


        // spinnerGraph
        var items = listOf(
            FiltreDate.getStringFromEnum(FiltreDate.JOUR, requireContext()),
            FiltreDate.getStringFromEnum(FiltreDate.SEMAINE, requireContext()),
            FiltreDate.getStringFromEnum(FiltreDate.MOIS, requireContext()))
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        this.spinnerGraph.adapter = adapter
        this.spinnerGraph.setSelection(2)

        this.spinnerGraph.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val option = listOf(FiltreDate.JOUR, FiltreDate.SEMAINE, FiltreDate.MOIS)
                valeurSpinner1 = option[position]
                recuperePoint()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Rien à faire en cas de sélection vide
            }
        }


        // spinnerType
        items = listOf(
            EnumTypeStatut.getStringFromEnum(EnumTypeStatut.Medicament, requireContext()),
            EnumTypeStatut.getStringFromEnum(EnumTypeStatut.Intervalle, requireContext()),
            EnumTypeStatut.getStringFromEnum(EnumTypeStatut.Spontanee, requireContext()))
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        this.spinnerType.adapter = adapter

        this.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val option = listOf(EnumTypeStatut.Medicament, EnumTypeStatut.Intervalle, EnumTypeStatut.Spontanee)
                valeurSpinnerType = option[position]

                if (valeurSpinnerType == EnumTypeStatut.Medicament) {
                    spinnerMedicament.visibility = View.VISIBLE
                    spinnerPrise.visibility = View.VISIBLE
                    rootView.findViewById<TextView>(R.id.textView5).visibility = View.VISIBLE
                    rootView.findViewById<TextView>(R.id.textView6).visibility = View.VISIBLE
                } else {
                    spinnerMedicament.visibility = View.GONE
                    spinnerPrise.visibility = View.GONE
                    rootView.findViewById<TextView>(R.id.textView5).visibility = View.GONE
                    rootView.findViewById<TextView>(R.id.textView6).visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // inputSeuil
        inputSeuil.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    val value = s.toString().toIntOrNull()

                    if (value == null || value !in 1..10) {

                        // Affichez un Toast pour expliquer l'erreur
                        Toast.makeText(requireContext(), R.string.pb_digit, Toast.LENGTH_SHORT)
                            .show()
                        // La valeur n'est pas valide, videz le champ
                        inputSeuil.clearFocus()
                        inputSeuil.text = null
                    }
                }
            }
        })


        // spinnerMedicament
        if (traitementUti.isEmpty()) {
            traitementUti = listOf(Medoc("EmptyMed",
                "0",
                requireContext().getString(R.string.aucun_medicament),
                "0",
                1,
                EnumFrequence.AUBESOIN,
                null,
                EnumTypeMedic.COMPRIME,
                5,
                false,
                "0",
                "0",
                10,
                LocalDate.now()))
        }
        items = this.traitementUti.map { it.nom }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (items.isNotEmpty()) {
            this.valeurSpinnerMedic = this.traitementUti.map { it.uuid }[0]
            this.spinnerMedicament.adapter = adapter
        }

        this.spinnerMedicament.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (items.isNotEmpty()) {
                    valeurSpinnerMedic = traitementUti[position].uuid
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // spinnerPrise
        items = listOf(
            requireContext().getString(R.string.avant_prise),
            requireContext().getString(R.string.apres_prise)
        )
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinnerPrise.adapter = adapter

        this.spinnerPrise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                valeurSpinnerPrise = position == 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // valider
        this.valider.setOnClickListener {
            enregistrePoint()
        }

        return rootView
    }


    /**
     * récupère tous les points de la base de données et les converties en forme utilisable par le graphe
     * mets à jour le graphe
     * enregistre les points de la période souhaitée dans une variable de la classe
     */
    private fun recuperePoint() {
        val db = AppDatabase.getInstance(requireContext())
        val statutInterface = StatutDouleurRepository(db.statutDouleurDao())

        val queue = LinkedBlockingQueue<String>()
        Thread{
            this.statutDouleur = statutInterface.getStatutByUser(this.userCo)
            queue.put("done")
        }.start()
        queue.take()

        filtreDate()

        setEntries()

        // Effacer les anciennes données
        this.lineDataSet.clear()

        // Ajouter les nouvelles données
        this.lineDataSet = LineDataSet(this.entries, "Data Set")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Redessiner le graphique avec les nouvelles données
        this.lineChart.notifyDataSetChanged()
    }

    /**
     * mets à jour les entrées du graphe à partir des points récupérés de la bd et filtrés
     */
    private fun setEntries() {
        val retour = arrayListOf<Entry>()
        val converters = Converters()

        for (s: StatutDouleur in this.statutDouleur) {
            val date = converters.stringToLocalDateTime(s.date)!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toFloat()
            val value = s.valeur.toFloat()
            retour.add(Entry(date, value))
        }

        this.entries = retour
    }

    /**
     * filtre les points pour qu'ils correspondent au filtre de date
     * enregistre les résultats dans une variable de classe
     */
    private fun filtreDate() {
        val converters = Converters()
        val today = LocalDate.now()
        val statuts = mutableListOf<StatutDouleur>()

        when (this.valeurSpinner1) {
            FiltreDate.JOUR -> {
                for (s: StatutDouleur in this.statutDouleur) {
                    if (converters.stringToLocalDateTime(s.date)!!.toLocalDate() == today) {
                        statuts.add(s)
                    }
                }
            }
            FiltreDate.SEMAINE -> {
                val weekFields = WeekFields.of(Locale.getDefault())
                var date: LocalDateTime

                for (s: StatutDouleur in this.statutDouleur) {
                    date = converters.stringToLocalDateTime(s.date)!!

                    if ((date.get(weekFields.weekOfYear()) == today.get(weekFields.weekOfYear())) && (date.year == today.year)) {
                        statuts.add(s)
                    }
                }
            }
            FiltreDate.MOIS -> {
                var date: LocalDateTime

                for (s: StatutDouleur in this.statutDouleur) {
                    date = converters.stringToLocalDateTime(s.date)!!

                    if ((date.month == today.month) && (date.year == today.year)) {
                        statuts.add(s)
                    }
                }
            }
        }

        this.statutDouleur = statuts
    }


    /**
     * Crée un nouveau point et l'ajoute à la bd
     * si pas assez d'information sont rentré, alors crée juste un toast
     * mets à jour le graphe
     */
    private fun enregistrePoint() {
        val newStatut: StatutDouleur
        val converters = Converters()
        val selmed = this.traitementUti.find { it.uuid == this.valeurSpinnerMedic }

        Log.d("zeubie", this.valeurSpinner1.toString())

        if (selmed == null) {
            Toast.makeText(requireContext(), R.string.echec_ajout, Toast.LENGTH_SHORT)
                .show()
        } else if (this.inputSeuil.text.toString().toIntOrNull() == null || selmed.uuid == "EmptyMed") {
            Toast.makeText(requireContext(), R.string.echec_ajout, Toast.LENGTH_SHORT)
                .show()
        } else {

            if (valeurSpinnerType == EnumTypeStatut.Medicament) {
                newStatut = StatutDouleur(
                    UUID.randomUUID().toString(),
                    this.valeurSpinnerType,
                    this.valeurSpinnerMedic,
                    this.valeurSpinnerPrise,
                    converters.localDateTimeToTimestamp(LocalDateTime.now())!!,
                    this.inputSeuil.text.toString().toInt(),
                    this.userCo,
                    this.texteNote.toString()
                )
            } else {
                newStatut = StatutDouleur(
                    UUID.randomUUID().toString(),
                    this.valeurSpinnerType,
                    null,
                    null,
                    converters.localDateTimeToTimestamp(LocalDateTime.now())!!,
                    this.inputSeuil.text.toString().toInt(),
                    this.userCo,
                    this.texteNote.toString()
                )
            }

            val db = AppDatabase.getInstance(requireContext())
            val statutInterface = StatutDouleurRepository(db.statutDouleurDao())
            val queue = LinkedBlockingQueue<String>()
            Thread {
                statutInterface.insertStatutDouleur(newStatut)
                queue.put("done")
            }.start()
            queue.take()

            Toast.makeText(requireContext(), R.string.statut_ajoute, Toast.LENGTH_SHORT)
                .show()
            recuperePoint()
        }
        this.texteNote.text = null
        this.inputSeuil.text = null
    }
}