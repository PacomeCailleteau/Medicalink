package dev.mobile.medicalink.fragments.douleur

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
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
import dev.mobile.medicalink.db.local.repository.StatutDouleurRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.douleur.enums.FiltreDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

class AffichageGraphFragment : Fragment() {

    // from layout
    lateinit var lineChart: LineChart
    lateinit var spinnerGraph: Spinner
    lateinit var spinnerType: Spinner
    lateinit var inputSeuil: TextInputEditText
    lateinit var spinnerMedicament: Spinner
    lateinit var valider: AppCompatButton

    // valeur du layout
    private var valeurSpinner1 = FiltreDate.MOIS
    private var valeurSpinnerType: EnumTypeStatut? = null
    private var valeurSpinnerMedic: String? = null

    // from bd
    lateinit var userCo: String
    private var traitementUti: List<Medoc> = listOf()
    private var statutDouleur: List<StatutDouleur> = listOf()
    private var entries: ArrayList<Entry> = arrayListOf()

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
        Thread {
            this.userCo = userInterface.getUsersConnected()[0].uuid
        }.start()


        //Ajout de la fonctionnalité de retour à la page précédente
        val retour = rootView.findViewById<ImageView>(R.id.retour_arriere)
        retour.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        // spinnerGraph
        this.spinnerGraph = rootView.findViewById(R.id.spinner1)
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


        // graphe
        this.lineChart = rootView.findViewById(R.id.lineChart)

        val entries = generateData()

        val lineDataSet = LineDataSet(entries, "Data Set")
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


        // spinnerType
        this.spinnerType = rootView.findViewById(R.id.spinnerType)
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
                //ajouter code pour afficher ou faire disparaître *SpinnerMedicament* et *SpinnerPrise*
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                valeurSpinnerType = null
            }
        }

        return rootView
    }

    /**
     * |Temporaire|
     * Génère des points aléatoire pour voir comment se comporte le graphe
     * @return liste de point
     */
    private fun generateData(): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val calendar = Calendar.getInstance()

        // Generating dummy data for demonstration purpose
        for (i in 0 until 10) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            val date = calendar.timeInMillis
            val value = (1..10).random().toFloat() // Random value between 1 and 10
            entries.add(Entry(date.toFloat(), value))
        }

        return entries

    }

    /**
     * récupère tous les points de la base de données et les converties en forme utilisable par le graphe
     * mets à jour le graphe
     * enregistre les points de la période souhaitée dans une variable de la classe
     */
    private fun recuperePoint() {
        val db = AppDatabase.getInstance(requireContext())
        val statutInterface = StatutDouleurRepository(db.statutDouleurDao())
        Thread{
            this.statutDouleur = statutInterface.getStatutByUser(this.userCo)
        }.start()

        filtreDate()

        val retour = arrayListOf<Entry>()
        val converters = Converters()

        for (s: StatutDouleur in this.statutDouleur) {
            val date = converters.stringToLocalDateTime(s.date)!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toFloat()
            val value = s.valeur.toFloat()
            retour.add(Entry(date, value))
        }

        this.entries = retour
        // Obtenez une référence à votre LineData existant
        val lineData = lineChart.data as LineData

        // Ajoutez ou modifiez les ensembles de données avec de nouvelles données
        val lineDataSet = LineDataSet(this.entries, "New Data Set")
        lineDataSet.color = Color.GREEN
        lineDataSet.valueTextColor = Color.BLACK

        // Ajoutez le nouvel ensemble de données à votre LineData existant ou modifiez-le si nécessaire
        lineData.addDataSet(lineDataSet)

        // Appelez notifyDataSetChanged() sur votre LineData pour informer le graphique des changements
        lineData.notifyDataChanged()

        // Appelez invalidate() sur votre LineChart pour forcer le réaffichage du graphique avec les nouvelles données
        this.lineChart.invalidate()
    }

    /**
     * filtre les points qu'ils correspondent au filtre de date
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
}