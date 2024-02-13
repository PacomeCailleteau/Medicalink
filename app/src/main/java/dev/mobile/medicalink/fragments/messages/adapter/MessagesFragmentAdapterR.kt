package dev.mobile.medicalink.fragments.messages.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R

class MessagesFragmentAdapterR(private val list: MutableList<Pair<String, String>>) :
    RecyclerView.Adapter<MessagesFragmentAdapterR.MessagesFragmentViewHolder>() {

    class MessagesFragmentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titreMessage: TextView = view.findViewById(R.id.titreMessage)
        val textMessage: TextView = view.findViewById(R.id.textMessage)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesFragmentViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessagesFragmentViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessagesFragmentViewHolder, position: Int) {
        val item = list.get(position)

        holder.titreMessage.text = item.first
        holder.textMessage.text = item.second

    }

}