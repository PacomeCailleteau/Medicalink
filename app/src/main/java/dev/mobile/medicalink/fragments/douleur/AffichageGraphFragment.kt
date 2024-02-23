package dev.mobile.medicalink.fragments.douleur

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AffichageGraphFragment: Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_affichage_statut_douleur, container, false)

        val lineChart = rootView.findViewById<LineChart>(R.id.lineChart)

        val entries = generateData()

        val lineDataSet = LineDataSet(entries, "Data Set")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.RED

        val lineData = LineData(lineDataSet)

        lineChart.data = lineData

        val description = Description()
        description.text = "Date vs Value"
        lineChart.description = description

        lineChart.invalidate()

        return rootView
    }

    private fun generateData(): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val calendar = Calendar.getInstance()

        // Generating dummy data for demonstration purpose
        for (i in 0 until 10) {
            calendar.add(Calendar.DATE, 1)
            val date = calendar.time
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            val value = (1..10).random().toFloat() // Random value between 1 and 10
            entries.add(Entry(i.toFloat(), value))
        }

        return entries
    }
}