package com.handlandmarker.AgoraPart.ScreenShare


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

class ScreenShareAdapter() : RecyclerView.Adapter<ScreenShareAdapter.PersonViewHolder>() {

    lateinit var  Context1: Context
    lateinit var  arr: ArrayList<String>
    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add references to views inside the item layout if needed
    }
    constructor(cont: Context,arr: ArrayList<String>) : this() {
        this.Context1 = cont
        this.arr = arr
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_info_layout, parent, false)

        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        // Bind data or set up views for each item
        var p = holder.itemView.findViewById<TextView>(R.id.Screen_Share_User_Name)
        p.setText(arr.get(position))
    }

    override fun getItemCount(): Int {
        // Return the number of items in your data set
        return arr.size
    }
}
