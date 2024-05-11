package com.handlandmarker.AgoraPart.Vedio

import android.content.Context
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.studify.R

class PersonVideoAdapter() : RecyclerView.Adapter<PersonVideoAdapter.PersonViewHolder>() {

    lateinit var  Context1: Context
    lateinit var  arr: ArrayList<SurfaceView>
    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Add references to views inside the item layout if needed
    }
    constructor(cont: Context,arr: ArrayList<SurfaceView>) : this() {
        this.Context1 = cont
        this.arr = arr
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.frame_layout, parent, false)

        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        // Bind data or set up views for each item
       var  SurfaceView1 = arr.get(position)

        val frameLayout: FrameLayout = holder.itemView.findViewById(R.id.Frame_layout_Video)
        frameLayout.addView(SurfaceView1)
    }

    override fun getItemCount(): Int {
        // Return the number of items in your data set
        return arr.size
    }
}
