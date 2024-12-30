package com.evilcorp.anguish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.anguish.BallsActivity.PrintControlPont
import com.evilcorp.anguish.BallsActivity.Rating
import com.evilcorp.anguish.RatingAdapterP.RatingViewHolderP


class RatingAdapterP(private val parent: List<Rating>) :
RecyclerView.Adapter<RatingViewHolderP>() {

    class RatingViewHolderP(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.rating_title)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_rating)

        fun bind(parent: Rating) {
            title.text = parent.title
            setupChildRecyclerView(parent.controlPoints)
        }
        private fun setupChildRecyclerView(points: List<PrintControlPont>) {
            val childAdapter = RatingAdapter(points)
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            childRecyclerView.adapter = childAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolderP {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating_parent, parent, false)
        return RatingViewHolderP(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolderP, position: Int) {
        holder.bind(parent[position])
    }

    override fun getItemCount(): Int {
        return parent.size
    }
}