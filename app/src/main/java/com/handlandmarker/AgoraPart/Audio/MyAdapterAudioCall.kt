package com.handlandmarker.AgoraPart.Audio

import android.content.Context
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studify.R
import com.handlandmarker.accets.Users

class MyAdapterAudioCall() : RecyclerView.Adapter<MyAdapterAudioCall.PersonViewHolder1>() {

    lateinit var  Context1: Context
    lateinit var  arr: ArrayList<String>
    inner class PersonViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add references to views inside the item layout if needed
    }
    constructor(cont: Context,arr: ArrayList<String>) : this() {
        this.Context1 = cont
        this.arr = arr
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder1 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_view, parent, false)

        return PersonViewHolder1(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder1, position: Int) {
        // Bind data or set up views for each item
        var  User = arr.get(position)
        val textView = holder.itemView.findViewById<TextView>(R.id.Audio_call_UserName)
        textView.setText(User)
    }

    override fun getItemCount(): Int {
        // Return the number of items in your data set
        return arr.size
    }
}
