package dev.mobile.medicalink.fragments.traitements

import MessagesFragmentAdapterR
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.LinkedBlockingQueue


class MessagesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)




        val messagesDeTest = mutableListOf<Pair<String,String>>(
            Pair("Durandal","Je vous envoie la prescription..."),
            Pair("Richard","Programmation rendez-vous le..."),
            Pair("Hervouet","Avez-vous un conseil pour..."),
        )

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = MessagesFragmentAdapterR(messagesDeTest)

        //Gestion espacement entre items RecyclerView
        val espacementEnDp = 10
        recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))


        return view
    }
}