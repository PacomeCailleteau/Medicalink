package dev.mobile.medicalink.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.ListeTraitementAdapterR


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //Get elements from view
        val paramBtn : ImageView = view.findViewById(R.id.btnParam)
        /*
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHome)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListeTraitementAdapterR(traitementsTries)
        */


        //Set click listener
        paramBtn.setOnClickListener {
            //Navigate to parametre fragment
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, ParametreFragement())
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

}


















