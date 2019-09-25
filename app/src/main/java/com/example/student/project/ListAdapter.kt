package com.example.student.project

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.student.project.R.id.imageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_list_item.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListViewHolder {
        val view: View = LayoutInflater.from(p0?.context).inflate(R.layout.card_list_item, p0, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Cards.cardNames.size
    }

    override fun onBindViewHolder(p0: ListViewHolder, p1: Int) {
        val holder: ListViewHolder = p0
        holder.bindView(p1)
    }

    class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var existingCardN: TextView? = null
        var existingCardI: ImageView? = null

        init {
            existingCardN = v.existingCardName
            existingCardI = v.existingCardImage
        }

        fun bindView(position: Int) {
            existingCardN!!.setText(Cards.cardNames.get(position))
            Picasso.get().load(Cards.cardImgs.get(position)).into(existingCardI);
        }
    }

}